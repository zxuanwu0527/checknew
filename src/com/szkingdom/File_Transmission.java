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
	 * ��������ȯ�̱������ݵ�ʱ�䣬�Ա��ĳ��������ʱ��ִ��
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
				File notice = new File(reportfile_path+"/"+receivefile_name+"/��ܰ��ʾ.txt");
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
						//���ɼ�����󱨸���fdepƽ̨�ϵ�ok��־
						File okfile = new File(notice.getPath()+".ok");						
						try {
							FileOutputStream fos = new FileOutputStream(notice);				
							BufferedWriter bw   = new BufferedWriter(new OutputStreamWriter(fos));
							String s ="  ÿ��"+stoptimebegin.substring(0,2)+":"+stoptimebegin.substring(2,4)+"~"+stoptimeend.substring(0,2)+":"+stoptimeend.substring(2,4)+"Ϊϵͳά��ʱ��,���ǻ���ͣ���ݴ�����򣬸���Ҵ����Ĳ������½⣡";
							bw.write(s+"\n");
							bw.close();
							fos.close();
							FileOutputStream fis = new FileOutputStream(okfile);
							fis.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							logger.debug("������OK�ļ�ʱ�������ļ�ʧ��!");
							LogUtils.debugStackTrace(e.getStackTrace());
						}
					}
				}
				else
				{
					//���ɼ�����󱨸���fdepƽ̨�ϵ�ok��־
					File okfile = new File(notice.getPath()+".ok");						
					try {
						
						FileOutputStream fos1 = new FileOutputStream(notice);				
						BufferedWriter bw1   = new BufferedWriter(new OutputStreamWriter(fos1));
						String s ="  ÿ��"+stoptimebegin.substring(0,2)+":"+stoptimebegin.substring(2,4)+"~"+stoptimeend.substring(0,2)+":"+stoptimeend.substring(2,4)+"Ϊϵͳά��ʱ��,���ǻ���ͣ���ݴ�����򣬸���Ҵ����Ĳ������½⣡";
						bw1.write(s+"\n");
						bw1.close();
						fos1.close();
						FileOutputStream fis1 = new FileOutputStream(okfile);
						fis1.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						logger.debug("������OK�ļ�ʱ�������ļ�ʧ��!");
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
	 * �������ȯ�̱��͹�������ز��Ϸ��ļ����Ա�ȯ����Ϊ���Ϸ��ļ����·������������ļ��У���7z��list�ļ���
	 * �����Ҫ�����ļ����µ��ļ�Ϊ�գ��������±��͡�
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
	 * �����ļ��Ƿ������
	 * @param file  ָ��ȯ�̱��͹������ļ���ŵ��ļ���
	 */
	public static String check_Transmission(File file,String interfacecount)
	{
		
		try
		{
			File[] fileList = file.listFiles();
			//if(!String.valueOf(fileList.length-1).equals(interfacecount))
			for (int i = 0; i < fileList.length; i++) {
				//����fdepƽ̨��־��δ������ɵ��ļ�����.szt!
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
	    * ���ڴ����ļ��У���������ڣ��򴴽����ļ���
	    * @param path 
	    */
	static  void   CreateFile(String   path)
	{ 
		
     //ѭ�������ļ���
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
		       System.out.println("have   made   a   dir   ��"   );       
		    }
			s = s.replaceFirst("/", ".");
				
			j = s.indexOf('/');
			
		}
       
	}    


}
