package com.szkingdom.submission.datacheck;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.szkingdom.submission.datacheck.po.CheckFile;

/**
 * 用来选取那些需要处理的文件夹（对应证券公司）， 放入任务队列
 * 主要做一些初步验证
 * @author leipeng
 *
 */
public class ZqgsFileSelectRunner implements Runnable {

	static final Logger logger  = Logger.getLogger(ZqgsFileSelectRunner.class);
	//预制任务队列
	private final ConcurrentLinkedQueue<CheckFile> preTaskQueue;
	//正在运行任务队列
	private final ConcurrentLinkedQueue<CheckFile> runingTaskQueue;
	//存放所有的文件夹（看作一个基准）key:zqgsdm
	private final HashMap<String, CheckFile> allFiles;
	
	public ZqgsFileSelectRunner(ConcurrentLinkedQueue<CheckFile> preTaskQueue, 
			ConcurrentLinkedQueue<CheckFile> runningTaskQueue,
			HashMap<String, CheckFile> allFiles) {
		this.preTaskQueue = preTaskQueue;
		this.runingTaskQueue = runningTaskQueue;
		this.allFiles = allFiles;
	}
	public void run() {
		long start = 0l;
		long end = 0l;
		String appendZqgsdm = "";
		/**
		 * 不断将带有数据的文件夹放入任务队列
		 */
		int cycleCount = 0;  
		/**
		 * 不断将带有数据的文件夹放入任务队列
		 */
		while(true){
			for(Entry<String, CheckFile> zqgsFileEntry : allFiles.entrySet()){
				if(zqgsFileEntry.getValue()!= null && zqgsFileEntry.getValue().getZqgsDir().exists() 
						&& !containsTask(preTaskQueue, zqgsFileEntry.getValue())
						&& !containsTask(runingTaskQueue, zqgsFileEntry.getValue())){
					File[] checkFiles = zqgsFileEntry.getValue().getZqgsDir().listFiles();
					if(checkFiles != null && checkFiles.length > 0){
						preTaskQueue.offer(zqgsFileEntry.getValue());
					}
				}
			}
			end  = System.currentTimeMillis();
			if(runingTaskQueue.size() >  0 && (end - start) > 1000){
				cycleCount++;
				appendZqgsdm = runingTaskQueue.toString();
				if(cycleCount % 5 ==0){
					logger.info("有"+runingTaskQueue.size()+"家公司数据正在进行校验. . ."+appendZqgsdm);
				}else{
					System.out.println("有"+runingTaskQueue.size()+"家公司数据正在进行校验. . . "+appendZqgsdm);
				}
				start = end;
			}
			if(runingTaskQueue.size() == 0 && (end - start) > 20000){
				System.out.println("没有公司数据正在进行校验... ...");
				start = end;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					logger.error("error", e);
				}
			}
		}
	}

	private boolean containsTask(ConcurrentLinkedQueue<CheckFile> taskQueue, CheckFile cf){
		if(taskQueue.isEmpty()){
			return false;
		}else{
			for(CheckFile ct : taskQueue){
				if(ct.getZqgsdm().equals(cf.getZqgsdm())){
					return true;
				}
			}
			return false;
		}
	}
	
	public static void main(String[] args) {
		ZqgsFileSelectRunner sr = new ZqgsFileSelectRunner(null, null, null);
		sr.run();
	}

}
