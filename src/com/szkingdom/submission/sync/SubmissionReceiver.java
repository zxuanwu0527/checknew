package com.szkingdom.submission.sync;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


public class SubmissionReceiver implements Runnable{
	private static Logger logger = Logger.getLogger(SubmissionReceiver.class);
	
	private static SubmissionReceiver rsvr = new SubmissionReceiver();
	
	public static ExecutorService es = Executors.newFixedThreadPool(1);
	
	private Socket sock;
	private Integer selfPort;
	OutputStream os = null;
	InputStream is = null;
	
	private SubmissionReceiver(Integer selfPort)
	{
		this.selfPort=selfPort;
	}
	
	private SubmissionReceiver()
	{
	}
	
	public static SubmissionReceiver getRsvr(){
		return rsvr;
	}
	
	public void init(int port){
		logger.info("init receiver"); 
		System.out.println(port); 
		Integer selfPort = null;
		try{
			es.execute(new SubmissionReceiver(selfPort));
		} catch (Exception e) {
			logger.error("", e); 
		}
	}
	public void run()
	{
		ServerSocket ss;
		try {
			ss = new ServerSocket(selfPort);
			Socket s=ss.accept();
			sock = s;

			String rtMsg = "";
			while(true){
				try{
					is =sock.getInputStream();
					os=sock.getOutputStream();
					byte []buf=new byte[100];
					int len=is.read(buf);
					String rsv = new String(buf,0,len);
					logger.debug("receive:"+rsv);
					Object o = SubmissionMsgDecoder.decode(rsv);
					rtMsg = SubmissionMsgHandler.handleRequest(o);
					os.write(rtMsg.getBytes());
					Thread.sleep(100); 
				}catch(SocketException e){
					sock = ss.accept();
					logger.error("", e);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch(Exception e1){
			logger.error("", e1);
		}
//			os.close();
//			is.close();
//			sock.close();
	}
	
	
	public static void main(String[] args) {
		DOMConfigurator.configure("./mastloadconf/log4j.xml");
//		Receiver rsvr = getRsvr();
//		rsvr.init();
//		System.out.println("inited finished");
	}
	
}