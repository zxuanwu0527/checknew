package com.szkingdom.submission.datacheck;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.szkingdom.submission.datacheck.po.CheckFile;

/**
 * ����ѡȡ��Щ��Ҫ������ļ��У���Ӧ֤ȯ��˾���� �����������
 * ��Ҫ��һЩ������֤
 * @author leipeng
 *
 */
public class ZqgsFileSelectRunner implements Runnable {

	static final Logger logger  = Logger.getLogger(ZqgsFileSelectRunner.class);
	//Ԥ���������
	private final ConcurrentLinkedQueue<CheckFile> preTaskQueue;
	//���������������
	private final ConcurrentLinkedQueue<CheckFile> runingTaskQueue;
	//������е��ļ��У�����һ����׼��key:zqgsdm
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
		 * ���Ͻ��������ݵ��ļ��з����������
		 */
		int cycleCount = 0;  
		/**
		 * ���Ͻ��������ݵ��ļ��з����������
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
					logger.info("��"+runingTaskQueue.size()+"�ҹ�˾�������ڽ���У��. . ."+appendZqgsdm);
				}else{
					System.out.println("��"+runingTaskQueue.size()+"�ҹ�˾�������ڽ���У��. . . "+appendZqgsdm);
				}
				start = end;
			}
			if(runingTaskQueue.size() == 0 && (end - start) > 20000){
				System.out.println("û�й�˾�������ڽ���У��... ...");
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
