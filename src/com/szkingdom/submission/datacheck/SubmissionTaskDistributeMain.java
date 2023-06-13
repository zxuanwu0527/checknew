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
		 * ��鱾��path���Ƿ���7z
		 */
		if(!check7zFunc()){
			logger.info("not found 7z cmd local...");
			creatNew7zBat();
			actualBatFile = "7zFile.bat";
		}
		ConcurrentLinkedQueue<CheckFile> allReadyTaskQueue = ObjectFactory.instance().getPreTaskQueue();
		
		logger.info("loading all zqgs file");
		//������е��ļ��У�����һ����׼��
		HashMap<String, CheckFile> allFiles = new HashMap<String, CheckFile>();
		// ����֤ȯ��˾��Ŀ¼
		String roodDir = dcp.getRootDir();
		// ������Ҫ�����֤ȯ��˾��ӦĿ¼
		List<String> zqgsFileDirs = dcp.getZqgsDirList();
		//��ʼ������֤ȯ��˾�ļ���
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
		 * ��ʼ��tcp����
		 */
		SubmissionReceiver.getRsvr().init(spt.getMasterPort());
		/**
		 * ��ʼ�����ж���
		 */
		ConcurrentLinkedQueue<CheckFile> runningTaskQueue = new ConcurrentLinkedQueue<CheckFile>();
		//�����Ѿ�ע��Ĵ������
		if(spt != null && spt.getProcessors() != null && !spt.getProcessors().isEmpty()){
			//��ȡ����IP������뱾��IP��ͬ���򽫿ͻ���IPת��Ϊ127.0.0.1
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
							//���processor�Ƿ��Ѿ���������Ϣͬ��
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
		//��ȡ����
		es.execute(new ZqgsFileSelectRunner(allReadyTaskQueue, runningTaskQueue, allFiles));
		//��������
		
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
