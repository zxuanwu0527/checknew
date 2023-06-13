package com.szkingdom.submission.datacheck;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.szkingdom.submission.datacheck.po.CheckFile;

public class SubmissionDataCheckMain {

	static final Logger logger = Logger.getLogger(SubmissionDataCheckMain.class);
	
	static final String lineSeparator =	(String) java.security.AccessController.doPrivileged(
	               new sun.security.action.GetPropertyAction("line.separator"));
		
	public static void main(String[] args) {
		DOMConfigurator.configure("conf/log4j.xml");
		DataCheckProperty dcp = (DataCheckProperty) ObjectFactory.instance().getBean("dataCheckProperty");

		String actualBatFile = null;
		/**
		 * 检查本机path下是否有7z
		 */
		if(!check7zFunc()){
			logger.info("not found 7z cmd local...");
			creatNew7zBat();
			actualBatFile = "7zFile.bat";
		}
		ConcurrentLinkedQueue<CheckFile> preTaskQueue = ObjectFactory.instance().getPreTaskQueue();
		ConcurrentLinkedQueue<CheckFile> runingTaskQueue = ObjectFactory.instance().getRuningTaskQueue();
		
		logger.info("loading all zqgs file");
		//存放所有的文件夹（看作一个基准）
		HashMap<String, CheckFile> allFiles = new HashMap<String, CheckFile>();
		// 所有证券公司根目录
		String roodDir = dcp.getRootDir();
		// 所有需要处理的证券公司对应目录
		List<String> zqgsFileDirs = dcp.getZqgsDirList();
		//初始化所有证券公司文件夹
		if(zqgsFileDirs != null && zqgsFileDirs.size() > 0){
			for(String zqgsFileName : zqgsFileDirs){
				CheckFile zqgsFile = allFiles.get(zqgsFileName);
				if(zqgsFile == null){
					logger.info("add new file :" + zqgsFileName);
					zqgsFile = new CheckFile(zqgsFileName, new File(roodDir + File.separator + zqgsFileName));
					allFiles.put(zqgsFileName, zqgsFile);
				}
			}
		}
		AtomicBoolean isRunning = new AtomicBoolean(true);
		logger.info("init threadpool");	
		ThreadParam tp = (ThreadParam)ObjectFactory.instance().getBean("threaParam");
		ExecutorService es = Executors.newFixedThreadPool(tp.getBasicCheckThreadNum());
		es.execute(new ZqgsFileSelectRunner(preTaskQueue, runingTaskQueue, allFiles));
		for(int i = 1; i < tp.getBasicCheckThreadNum(); i++){
			es.execute(new DataCheckRunner(preTaskQueue, runingTaskQueue, isRunning, actualBatFile));
		}
//		new ZqgsFileSelectRunner(preTaskQueue, runingTaskQueue, allFiles).run();
//		logger.info("start datacheck");
//		new DataCheckRunner(preTaskQueue, runingTaskQueue, isRunning, actualBatFile).run();
//		long end = System.currentTimeMillis();
//		System.out.println("total cost:"+(end - start));
	}

	public static boolean check7zFunc(){
		String  strs = "7z";
		RuntimeResult rr = CmdRunner.runCmd(strs);
		return rr.isSuccess();
	}
	
	public static void creatNew7zBat(){
		String relativelyPath=System.getProperty("user.dir"); 
		File runBat = new File(relativelyPath + File.separator + "7zFile.bat");
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(runBat));
			bw.write("@echo off");
			bw.write(lineSeparator);
			bw.write("echo %0");
			bw.write(lineSeparator);
			bw.write("echo %1");
			bw.write(lineSeparator);
			bw.write("cd /d " + relativelyPath + File.separator +"7-Zip");
			bw.write(lineSeparator);
			bw.write("7z x %1\\*.7z  -aoa -o%1");
			bw.write(lineSeparator);
			bw.write("del \"%1\\*.7z\"");
			bw.write(lineSeparator);
			bw.write("del \"%1\\ok\"");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(bw != null){
				try {
					bw.close();
				} catch (IOException e) {}
			}
		}
	}
	
}
