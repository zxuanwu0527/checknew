package com.szkingdom.submission.sync;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.szkingdom.submission.datacheck.po.Msg;
import com.szkingdom.submission.datacheck.po.TaskFinishInfo;
import com.szkingdom.submission.datacheck.po.TaskQueueStatus;


public class MsgUtils {

	private static final Logger logger = Logger.getLogger(MsgUtils.class);
	
	//获取消息实际内容
	public static Object getMsgInstance(String msg){
		Msg msgObj = JSON.parseObject(msg, Msg.class);
		if(msgObj.getContent() == null){
			return null;
		}
		switch (msgObj.getType()) {
		case TaskQueueStatus:
			
			TaskQueueStatus tqs =  JSON.parseObject(msgObj.getContent().toString(), TaskQueueStatus.class); 
			return tqs;
		case TaskFinishedInfo:
			TaskFinishInfo tfi = JSON.parseObject(msgObj.getContent().toString(), TaskFinishInfo.class); 
			return tfi;
		default:
			break;
		}
		return null;
	}
	public static String getServerInfo(String host, String port){
		return host+":"+port;
	}
	public static List<String> getLocalIp(){
		List<String> ipList = new ArrayList<String>();
		Enumeration<NetworkInterface> netInterfaces = null;    
		try {    
		    netInterfaces = NetworkInterface.getNetworkInterfaces();    
		    while (netInterfaces.hasMoreElements()) {    
		        NetworkInterface ni = netInterfaces.nextElement();    
		        Enumeration<InetAddress> ips = ni.getInetAddresses();    
		        while (ips.hasMoreElements()) {  
		        	ipList.add(ips.nextElement().getHostAddress());
		        }    
		    }    
		} catch (Exception e) {    
		    e.printStackTrace();    
		} 
		return ipList;
	}
	
	public static String getRemoteAddr(Socket s){
		SocketAddress sa = s.getRemoteSocketAddress();
		System.out.println("address:"+sa);
		String clientAddr = sa.toString();
		return clientAddr;
	}
}
