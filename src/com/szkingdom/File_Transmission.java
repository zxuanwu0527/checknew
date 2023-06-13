package com.szkingdom;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import org.apache.log4j.Logger;

import com.szkingdom.b.TestFileXML;

public class File_Transmission {

	/**
	 * @param args
	 */
	private static Logger logger = Logger.getLogger(File_Transmission.class);
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/*
	 * 用于限制券商报送数据的时间，以便别的程序可以有时间执行
	 */	
	public static String restrictTime(File reportfile,File receivefile,String stoptimebegin,String stoptimeend) 
	{
		try
    	{
			
			String  reportfile_path = reportfile.getPath();
			String  receivefile_name = receivefile.getName();
			Date   report_file_date = new Date();
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
			String  nowdate = sdf.format(report_file_date);
			
			System.out.println("nowdate="+nowdate);
			int   reportdate = Integer.parseInt(nowdate.substring(0, 8));
			int   reporttime = Integer.parseInt(nowdate.substring(8, 14));
			System.out.println("reportdate="+reportdate+"");
			System.out.println("reporttime="+reporttime+"");
			
			if((reporttime>=Integer.parseInt(stoptimebegin))&&(reporttime<=Integer.parseInt(stoptimeend)))
			{
				File notice = new File(reportfile_path+"/"+receivefile_name+"/温馨提示.txt");
				CreateFile(reportfile_path+"/"+receivefile_name+"/");
				System.out.println(notice.getPath());
				if(notice.exists())
				{
					long  updatetime = notice.lastModified();
					String   lastdate  =  sdf.format(new Date(updatetime));
					int      updatedate =  Integer.parseInt(lastdate.substring(0, 8));
					System.out.println("update="+updatedate);
					if(reportdate>updatedate)
					{
						notice.delete();
						//生成检验错误报告在fdep平台上的ok标志
						File okfile = new File(notice.getPath()+".ok");						
						try {
							FileOutputStream fos = new FileOutputStream(notice);				
							BufferedWriter bw   = new BufferedWriter(new OutputStreamWriter(fos));
							String s ="  每天"+stoptimebegin.substring(0,2)+":"+stoptimebegin.substring(2,4)+"~"+stoptimeend.substring(0,2)+":"+stoptimeend.substring(2,4)+"为系统维护时间,我们会暂停数据处理程序，给大家带来的不便请谅解！";
							bw.write(s+"\n");
							bw.close();
							fos.close();
							FileOutputStream fis = new FileOutputStream(okfile);
							fis.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							logger.debug("在生成OK文件时，创建文件失败!");
							LogUtils.debugStackTrace(e.getStackTrace());
						}
					}
				}
				else
				{
					//生成检验错误报告在fdep平台上的ok标志
					File okfile = new File(notice.getPath()+".ok");						
					try {
						
						FileOutputStream fos1 = new FileOutputStream(notice);				
						BufferedWriter bw1   = new BufferedWriter(new OutputStreamWriter(fos1));
						String s ="  每天"+stoptimebegin.substring(0,2)+":"+stoptimebegin.substring(2,4)+"~"+stoptimeend.substring(0,2)+":"+stoptimeend.substring(2,4)+"为系统维护时间,我们会暂停数据处理程序，给大家带来的不便请谅解！";
						bw1.write(s+"\n");
						bw1.close();
						fos1.close();
						FileOutputStream fis1 = new FileOutputStream(okfile);
						fis1.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						logger.debug("在生成OK文件时，创建文件失败!");
						LogUtils.debugStackTrace(e.getStackTrace());
					}
				}
			}
			else
			{
				return "1";
			}
			return "0";
			/*
		    for (int i = 0; i < tempList.length; i++) 
		    {
		        
		    	 
		    	 if (tempList[i].isDirectory()) 
		         {
		        	 delFolder(tempList[i]);
		         } 
		         else 
		         {
		        	 String type =tempList[i].getName().split("\\.")[1];
		        	 if((!"LIST".equals(type.toUpperCase()))&&(!"7Z".equals(type.toUpperCase())))
		        	{
		        		 tempList[i].delete();
		        	}
			     }
		     }*/
    	
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		LogUtils.debugStackTrace(e.getStackTrace());
    		return "0";
    	}
	}
	
	/*
	 * 用于清空券商报送过来的相关不合法文件，以便券商因为不合法文件导致反馈有误差，（如文件夹，非7z和list文件）
	 * 设计上要求报送文件夹下的文件为空，才能重新报送。
	 */	
	public static void delFolderorFile(File folderFile) 
	{
		try
    	{
			
			File[] tempList = folderFile.listFiles();
		     for (int i = 0; i < tempList.length; i++) 
		     {
		        
		    	 
		    	 if (tempList[i].isDirectory()) 
		         {
		        	 delFolder(tempList[i]);
		         } 
		         else 
		         {
		        	 String type =tempList[i].getName().split("\\.")[1];
		        	 if((!"LIST".equals(type.toUpperCase()))&&(!"7Z".equals(type.toUpperCase())))
		        	{
		        		 tempList[i].delete();
		        	}
			     }
		     }
    	
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
	}
	
	
	
	
	static void delFolder(File folderFile) 
	{
		try
    	{
			 File[] tempList = folderFile.listFiles();
		     for (int i = 0; i < tempList.length; i++) 
		     {
		         if (tempList[i].isDirectory()) 
		         {
		        	 delFolder(tempList[i]);	        	         	
		         } 
		         else 
		         {
		        	 tempList[i].delete();
			     }
		     }
		     folderFile.delete();	
	
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
	}
	
	
	
	/*
	 * 检验文件是否传输完毕
	 * @param file  指向券商报送过来的文件存放的文件夹
	 */
	public static String check_Transmission(File file,String interfacecount)
	{
		
		try
		{
			File[] fileList = file.listFiles();
			//if(!String.valueOf(fileList.length-1).equals(interfacecount))
			for (int i = 0; i < fileList.length; i++) {
				//检验fdep平台标志，未传送完成的文件会有.szt!
			    	String filename =fileList[i].getName();
			    	if(filename.indexOf(".szt!")>-1)
			    	{
				    	return "0";
			    	}
		  	}
			/**
			 * ???? -1
			 */
			if((fileList.length-1)<Integer.parseInt(interfacecount))
			{
				return "0";
			}
			return "1";
		}catch (NullPointerException e){ 
			e.printStackTrace();
			LogUtils.debugStackTrace(e.getStackTrace());
			return "0";
	       }catch (ArrayIndexOutOfBoundsException e){ 
	      	 e.printStackTrace();
	      	 LogUtils.debugStackTrace(e.getStackTrace());
	      	 return "0";
		  }
	}
	
	/**
	    * 用于创建文件夹，如果不存在，则创建该文件夹
	    * @param path 
	    */
	static  void   CreateFile(String   path)
	{ 
		
     //循环创建文件夹
		String s = path;
		String m =""; 
		int j = path.indexOf("/");
	    for(;j>0;)
		{
			
			m = path.substring(0, j+1);
			File   file   =   new   File(m);    
		    if(file.isDirectory()){       
		       System.out.println("the   dir   is   exits");       
		    }else{       
		       file.mkdir();       
		       System.out.println("have   made   a   dir   ！"   );       
		    }
			s = s.replaceFirst("/", ".");
				
			j = s.indexOf('/');
			
		}
       
	}    


}
