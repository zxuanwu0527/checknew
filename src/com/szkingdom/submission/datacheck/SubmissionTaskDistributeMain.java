package com.szkingdom.submission.datacheck;

import static com.szkingdom.submission.sync.MsgUtils.*;

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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.szkingdom.submission.datacheck.po.CheckFile;
import com.szkingdom.submission.datacheck.po.CheckTask;
import com.szkingdom.submission.datacheck.po.SyncProperty;
import com.szkingdom.submission.sync.SubmissionMsgHandler;
import com.szkingdom.submission.sync.SubmissionReceiver;

public class SubmissionTaskDistributeMain {

	static final Logger logger = Logger.getLogger(SubmissionTaskDistributeMain.class);
	
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
		ConcurrentLinkedQueue<CheckFile> allReadyTaskQueue = ObjectFactory.instance().getPreTaskQueue();
		
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
		
		
		logger.info("init task distributer");	
		SyncProperty spt = (SyncProperty)ObjectFactory.instance().getBean("syncProperty");
		/**
		 * 初始化tcp连接
		 */
		SubmissionReceiver.getRsvr().init(spt.getMasterPort());
		/**
		 * 初始化运行队列
		 */
		ConcurrentLinkedQueue<CheckFile> runningTaskQueue = new ConcurrentLinkedQueue<CheckFile>();
		//所有已经注册的处理进程
		if(spt != null && spt.getProcessors() != null && !spt.getProcessors().isEmpty()){
			//获取本机IP，如果与本机IP相同，则将客户端IP转化为127.0.0.1
			List<String> localIps = getLocalIp();
			for(String key : spt.getProcessors().keySet()){
				String processorInfo = spt.getProcessors().get(key);
				if(StringUtils.isNotBlank(processorInfo)){
					if(processorInfo.length() > 5){
						if(processorInfo.contains(":")){
							String[] ipPorts = processorInfo.split(":");
							String ip = ipPorts[0];
							if(localIps.contains(ip)){
								ip = "127.0.0.1";
							}
							String port =  ipPorts[1];
							processorInfo = getServerInfo(ip, port);
							spt.getProcessors().put(key, processorInfo);
							//检查processor是否已经将任务信息同步
							logger.info("check the task sync info for:"+processorInfo);
							ObjectFactory.instance().getTaskStatusPool().get(processorInfo);
						}else{
							logger.error("what is this,"+processorInfo+", an ip?");
						}
					}
				}
			}
			ObjectFactory.instance().setBean("syncProperty", spt);
		}else{
			logger.error("did not find the processor config!!");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
			System.exit(-1);
		}
		
		ExecutorService es = Executors.newFixedThreadPool(2);
		//获取任务
		es.execute(new ZqgsFileSelectRunner(allReadyTaskQueue, runningTaskQueue, allFiles));
		//分配任务
		
	}

	public static boolean check7zFunc(){
		CmdRunner cr = (CmdRunner)ObjectFactory.instance().getBean("cmdRunner");
		String  strs = "7z";
		RuntimeResult rr = cr.runCmd(strs);
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
