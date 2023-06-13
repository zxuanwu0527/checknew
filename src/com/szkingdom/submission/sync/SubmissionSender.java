package com.szkingdom.submission.sync;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.szkingdom.submission.datacheck.ObjectFactory;

public class SubmissionSender {
	private static final Logger logger = Logger.getLogger(SubmissionSender.class);
	
	//标识是否已成功初始化
	public static AtomicBoolean inited = new AtomicBoolean(false);
	public static ExecutorService singleEs = Executors.newSingleThreadExecutor();
	
	private OutputStream os = null;
	private InputStream is= null;
	private Socket s = null;
	private Integer serverPort = null;
	private String serverIp = null;
	
	private static SubmissionSender sender = new SubmissionSender();
	private SubmissionSender(){
	}
	public static SubmissionSender getSender(){
		return sender;
	}
	
	public synchronized void init(String serverIp, int serverPort){
		if(inited.get()){
			logger.warn("sender has inited, ip:"+serverIp +", port:"+serverPort); 
		}
		logger.info("init sender, ip:"+serverIp +", port:"+serverPort); 
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		
		if(serverIp != null && serverPort > 0){
			try {
				 s = new Socket(serverIp, serverPort);
				os = s.getOutputStream();
				is = s.getInputStream();
				inited.set(true);
			} catch(ConnectException e){
				logger.error("connction error, retry...");
				singleEs.execute(new ConnectRetrier(serverIp, serverPort));
			}
			catch (Exception e) {
				logger.error("", e);
			}
		}else{
			logger.error("init msg sender error, serverPort==null!");
		}
	}
	public void sendMsg(String msg){
		try{
			if(inited.get()){
				os.write(msg.getBytes());
				byte []buf=new byte[100];
				int len=is.read(buf);
				String resv = new String(buf,0,len);
				logger.debug("response msg:"+resv);
				Object o = SubmissionMsgDecoder.decode(resv);
				SubmissionMsgHandler.handleResponse(o);
			}else{
				logger.error("the connection has not inited. please check!!!"); 
			}
		}catch(SocketException e){
			inited.set(false);
			ObjectFactory.instance().removeProcessor(serverIp+""+serverPort);
			logger.error("", e);
			singleEs.execute(new ConnectRetrier(serverIp, serverPort));
		}
		catch(Exception e){
			logger.error("", e);
		}
	}
	
	public void close(){
		try{
			if(os != null){
				os.close();
			}
		}catch(Exception e){
			logger.error("", e);
		}
		try{
			if(is != null){
				is.close();
			}
		}catch(Exception e){
			logger.error("", e);
		}
		try{
			if(s != null){
				s.close();
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
	//重新获取连接
	class ConnectRetrier implements Runnable{

		private  final Integer serverPort;
		private final String serverIp;
		
		private ConnectRetrier(String serverIp, int serverPort) {
			this.serverIp = serverIp;
			this.serverPort = serverPort;
		}

		public void run() {
			while(true){
				try {
					s = new Socket(serverIp, serverPort.intValue());
					os = s.getOutputStream();
					is = s.getInputStream();
					inited.set(true);
					logger.info("connection inited.");
					ObjectFactory.instance().addProcessor(serverIp+""+serverPort);
					break;
				} catch(ConnectException e){
					logger.error("connction to ["+serverPort.intValue()+"] error, retry...", e);
				}
				catch (Exception e) {
					logger.error("", e);
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {}
			}
		}
		
	}
	
	public static void main(String []args) throws InterruptedException 
	{
		DOMConfigurator.configure("./mastloadconf/log4j.xml");
		SubmissionSender sender = SubmissionSender.getSender();
		sender.sendMsg("ppp");
		sender = SubmissionSender.getSender();
		sender.sendMsg("ppp2");
		sender = SubmissionSender.getSender();
		sender.sendMsg("ppp3");
		sender = SubmissionSender.getSender();
		sender.sendMsg("ppp4");
		Thread.sleep(10000);
	}

}
