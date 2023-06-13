package com.szkingdom.submission.datacheck;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.szkingdom.submission.datacheck.po.CheckFile;

public class DataCheckOracleQueueMain {

	static final Logger logger = Logger.getLogger(DataCheckOracleQueueMain.class);
	
	static final String lineSeparator =	(String) java.security.AccessController.doPrivileged(
	               new sun.security.action.GetPropertyAction("line.separator"));
	static String logconf = "";
	static String confpath = "";
	//当前进程ID
	static String pid = "";
	
	public static void main(String[] args) {
		logconf = System.getProperty("logconf", "conf/log4j.xml");
		confpath = System.getProperty("confpath", "conf");
		DOMConfigurator.configure(logconf);
		
		pid = getPid();
		logger.info("check processor pid:"+pid); 

		String actualBatFile = null;
//		/**
//		 * 检查本机path下是否有7z
//		 */
		if(!check7zFunc()){
			logger.info("not found 7z cmd local...");
			creatNew7zBat();
			actualBatFile = "7zFile.bat";
		}
		ConcurrentLinkedQueue<CheckFile> preTaskQueue = ObjectFactory.instance().getPreTaskQueue();
		ConcurrentLinkedQueue<CheckFile> runingTaskQueue = ObjectFactory.instance().getRuningTaskQueue();
		
		AtomicBoolean isRunning = new AtomicBoolean(true);
		logger.info("init threadpool");	
		ThreadParam tp = (ThreadParam)ObjectFactory.instance().getBean("threaParam");
		ExecutorService es = Executors.newFixedThreadPool(tp.getBasicCheckThreadNum());
		for(int i = 1; i < tp.getBasicCheckThreadNum(); i++){
			es.execute(new DataCheckRunner(preTaskQueue, runingTaskQueue, isRunning, actualBatFile));
		}
		StatusReporter sr = new StatusReporter(runingTaskQueue);
		sr.start();
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
	
	private static String getPid(){
		// get name representing the running Java virtual machine.
		String name = ManagementFactory.getRuntimeMXBean().getName();
		// get pid
		String pid = name.split("@")[0];
		return pid;
	}
}
