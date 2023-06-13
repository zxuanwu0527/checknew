package com.szkingdom.submission.datacheck;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.szkingdom.submission.datacheck.po.CheckFile;

public class StatusReporter extends Thread {

	private static final Logger logger = Logger.getLogger(StatusReporter.class);
	//正在执行的任务队列
	private final ConcurrentLinkedQueue<CheckFile> runingTaskQueue;
	public StatusReporter(	ConcurrentLinkedQueue<CheckFile> runingTaskQueue) {
		this.runingTaskQueue  = runingTaskQueue;
	}

	public void run() {
		List<String> zqgslist = new ArrayList<String>();;
		int noZqgsCyle = 1;
		while (true) {
			try {
				if(!runingTaskQueue.isEmpty()){
					noZqgsCyle = 1;
					zqgslist.clear();
					Iterator<CheckFile> ite = runingTaskQueue.iterator();
					while(ite.hasNext()){
						zqgslist.add(ite.next().getZqgsdm());
					}
					System.out.println(zqgslist.size() + " 家证券公司正在校验："+zqgslist.toString());
					Thread.sleep(1000);
				}else{
					System.out.println("没有证券公司正在校验");
					Thread.sleep(noZqgsCyle * 5000);
					noZqgsCyle = noZqgsCyle > 11 ? noZqgsCyle : noZqgsCyle+1;
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

	public static void main(String[] args) {
		System.out.println("start=========="+System.currentTimeMillis());
		ConcurrentLinkedQueue<CheckFile> runingTaskQueue = new ConcurrentLinkedQueue<CheckFile>();
		StatusReporter sr = new StatusReporter(runingTaskQueue);
		sr.run();
		System.out.println("end=========="+System.currentTimeMillis());
	}

}
