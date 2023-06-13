package com.szkingdom.submission.datacheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class CmdRunner {
//	private static Logger logger = Logger.getLogger(CmdRunner.class);
	static String lineSeparator =	(String) java.security.AccessController.doPrivileged(
	               new sun.security.action.GetPropertyAction("line.separator"));
	
    private static String zipDir;
	
	public static String getZipDir() {
		return zipDir;
	}
	
	public void setZipDir(String zipDir) {
		CmdRunner.zipDir = zipDir;
	}
	
	public static RuntimeResult runCmd(String cmd){
		Process proc = null;
		RuntimeResult rr = new RuntimeResult();
		try{
			Runtime run = Runtime.getRuntime();
			proc = run.exec(getZipDir() + cmd);
			//记录一次执行的日志 
			StringBuffer errLogSb = new StringBuffer();
			StringBuffer normalLogSb = new StringBuffer();
			StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "Error", errLogSb);
			StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "Output", normalLogSb);
			errorGobbler.start();
			outputGobbler.start();
			proc.waitFor();
			rr.setSuccess(true);
			if(StringUtils.isNotBlank(errLogSb.toString())){
				rr.setSuccess(false);
				rr.setErrInfo(errLogSb.toString());
			}
			rr.setNormalInfo(normalLogSb.toString());
		}catch(Exception e){
//			logger.error("", e);
			rr.setSuccess(false);
			rr.setErrInfo(e.getMessage());
		}finally{
			if(proc != null){
				proc.destroy();
			}
		}
		return rr;
	}

	
	
	public static void main(String[] args) {
		String  strs = "E:\\export\\check\\新版\\run.bat";
		RuntimeResult rr = runCmd(strs);
		System.out.println(rr);
		RuntimeResult rr2 =  runCmd(strs);
		System.out.println(rr2);
	}
}
class StreamGobbler extends Thread {
	static Logger logger = Logger.getLogger(StreamGobbler.class);
	
	 InputStream is;
	 String type;
	 StringBuffer logSb;
	 StreamGobbler(InputStream is, String type, StringBuffer logSb) {
		  this.is = is;
		  this.type = type;
		  this.logSb = logSb;
	 }
	 public void run() {
		 BufferedReader br  = null;
		  try {
			   InputStreamReader isr = new InputStreamReader(is);
			   br = new BufferedReader(isr);
			   String line = null;
			   while ((line = br.readLine()) != null) {
				   if (type.equals("Error")){
					   logSb.append(CmdRunner.lineSeparator +"error:"+line);
				   }else{
					   logSb.append(CmdRunner.lineSeparator +"output:"+line);
				   }
			   }
		  } catch (IOException ioe) {
			  logger.error("error!", ioe);
		  }finally{
			  if(br != null){
				  try {
					br.close();
				} catch (IOException e) {
				}
			  }
		  }
	 }
}