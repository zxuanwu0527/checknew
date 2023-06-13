package com.szkingdom.submission.sync;

import static com.szkingdom.submission.sync.MsgUtils.getServerInfo;

import org.apache.log4j.Logger;

import com.szkingdom.submission.datacheck.ObjectFactory;
import com.szkingdom.submission.datacheck.po.CheckFile;
import com.szkingdom.submission.datacheck.po.CheckTask;
import com.szkingdom.submission.datacheck.po.TaskQueueStatus;

/**
 * @author leipeng
 *
 */
public class SubmissionMsgHandler {

	private static final Logger logger = Logger.getLogger(SubmissionMsgHandler.class);

	//处理请求消息，并返回一个响应
	public  static String handleRequest(Object msg){
		if(msg == null){
			return "error! msg is null";
		}
		if(msg instanceof TaskQueueStatus){
			//接手任务状态
			logger.debug("get a task status msg");
			put2taskStatusPool((TaskQueueStatus)msg);
			return "ok";
		}else if(msg instanceof CheckFile){
			//接收任务
			logger.debug("get a task  msg");
			addTask((CheckFile)msg);
			return "ok";
		}else{
			return "";
		}
	}
	
	public static void handleResponse(Object msg){
		
	}
	
	public static void addTask(CheckFile task){
		ObjectFactory.instance().addPreTask(task);
	}
	public static void put2taskStatusPool(TaskQueueStatus o){
		if(o != null){
			logger.debug("put msg:"+o.toString()); 
			String key = getServerInfo(o.getHost(), Integer.toString(o.getPort()));
			TaskQueueStatus tqsBefore = ObjectFactory.instance().getTaskStatusPool().get(key);
			if(tqsBefore != null){
				if(o.getMsgid() < tqsBefore.getMsgid()){
					o = tqsBefore;
				}
			}
			if(key != null){
				ObjectFactory.instance().putTaskStatus2Pool(key, o);
			}
		}
	}
	
	public static void main(String[] args){
	}
	
}
