package com.szkingdom.submission.datacheck;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.szkingdom.submission.datacheck.po.CheckTask;
import com.szkingdom.submission.datacheck.po.SyncProperty;
import com.szkingdom.submission.sync.SubmissionSender;

public class TaskDistributer implements Runnable {
	
	private static final Logger logger = Logger.getLogger(TaskDistributer.class);
	//预制任务队列
	private final ConcurrentLinkedQueue<CheckTask> preTaskQueue;
	
	public TaskDistributer(ConcurrentLinkedQueue<CheckTask> preTaskQueue) {
		this.preTaskQueue = preTaskQueue;
	}

	public void run() {
		SubmissionSender taskSender = SubmissionSender.getSender();
		SyncProperty spt = (SyncProperty)ObjectFactory.instance().getBean("syncProperty");
		//依次初始化连接
		Map<String, String> processors = spt.getProcessors();
		if(processors == null || processors.isEmpty()){
			logger.error("did not find check processor");
			System.exit(-1);
		}else{
			for(String serverInfo: processors.values()){
				
			}
		}
		while(true){
			if(preTaskQueue != null && !preTaskQueue.isEmpty()){
				
			}else{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	private void init(){
		
	}

}
