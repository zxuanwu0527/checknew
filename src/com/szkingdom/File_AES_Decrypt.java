package com.szkingdom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import org.apache.log4j.Logger;
public class File_AES_Decrypt {
	/**
	 * @param args
	 */
	private static Logger logger = Logger.getLogger(File_AES_Decrypt.class);
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/*
	 * ���ڼ���*.list�ļ������������ڱ�־��*.list�����¼���ļ������������ڱ�־�Ƿ�һ��
	 * @param listfile  ָ��list�ļ�5
	 */
	public static String check_List_Sjrq(File listfile) 
	{
		if(listfile.isFile())
		{
			String list_sjrq = null;
			try {
			   FileInputStream fis = new FileInputStream(listfile);
			   BufferedReader br  = new BufferedReader(new InputStreamReader(fis));
			   String s ="";
			   while((s = br.readLine())!=null)
			   {
				   String sjrq = s.split("_")[2];
				   if((list_sjrq==null) || ("".equals(list_sjrq)))
				   {
					   list_sjrq = sjrq;
				   }
				   else
				   {
					 if(!sjrq.equals(list_sjrq))
					 {
					   br.close();
					   fis.close();
					   return "0";
					 }
				   }
			   }
			   br.close();
			   fis.close();
		   }catch (IOException e){
			   e.printStackTrace();
			   LogUtils.debugStackTrace(e.getStackTrace());
			   return "0";
			   
		   }catch (NullPointerException e){ 
	         
			   e.printStackTrace();
			   LogUtils.debugStackTrace(e.getStackTrace());
			   return "0";
	       }catch (ArrayIndexOutOfBoundsException e){ 
		         
	      	 e.printStackTrace();
	      	 LogUtils.debugStackTrace(e.getStackTrace());
	      	 return "0";
		  }
			return "1";
		}
		else
		{
		   return "0";
		}
	
	
	}
	
	/*
	 * ���ڼ���.list�ļ�����ļ�¼���������ļ�������
	 */
	public static int count(File listfile)
	{
		
		int j =0; //��¼�ļ�����
		try {
		   FileInputStream fis = new FileInputStream(listfile);
		   BufferedReader br  = new BufferedReader(new InputStreamReader(fis));
		   String s ="";
		   while((s = br.readLine())!=null)
		   {
			  j++;
		   }
		   br.close();
		   fis.close();
	   }catch (IOException e){
		   e.printStackTrace();
		   return 0;
		   
	   }
		
	   return j;
	
	}
	
	/*
	 * ���ڼ���list�ļ����ļ��Ƿ��ʵ���ļ�ƥ��
	 * @param listfile  ָ��list�ļ�
	 * @param file      ָ��ǰ�����ȯ���ļ���
	 */
	public static String Check_ListFile(File listfile,File file) 
	{
		
		
		if(listfile.isFile())
		{
			String listfiles = "";
			try {
			   FileInputStream fis = new FileInputStream(listfile);
			   BufferedReader br  = new BufferedReader(new InputStreamReader(fis));
			   String s ="";
			   File  checkfile = null;
			   
			   while((s = br.readLine())!=null)
			   {
				   listfiles +=s;
				   checkfile = new File(file.getPath()+"\\"+s);
				   logger.debug(checkfile.getPath());
				   if(!checkfile.exists())
				   {
					   br.close();
					   fis.close();
					   return "0";
				   }
			   }
			   br.close();
			   fis.close();
		   }catch (IOException e){
			   e.printStackTrace();
			   return "0";
			   
		   }
		   logger.debug(listfiles);
		   return Delete_ListFile(listfiles,file);
		}
		else
		{
		   return "0";
		}
	
	 
	}
	/*
	 * ���ڼ���*.list�����¼���ļ�������������Ƿ�ͱ��͹���������һ�£�����ɾ��*.list�ļ�
	 * ���磺���͵���*.TXT����list�ļ������Ƿ�Ҳ��*.TXT��
	 * @param listfiles  list�ļ����ļ����ַ���
	 * @param file      ָ��ǰ�����ȯ���ļ���
	 * ע�⣺*.TXT ��*.txtͬ������Ϊ�ļ�����һ��
	 */
	public static String Delete_ListFile(String listfiles,File file) 
	{
		File checkfile[] = file.listFiles();
		//int count = checkfile.length;
		for(int i=0;i<checkfile.length;i++)
		{
			String filename = checkfile[i].getName();		
			if(listfiles.indexOf(filename)==-1)
			{
				
				String type =checkfile[i].getName().split("\\.")[1];
		    	if("LIST".equals(type.toUpperCase()))
		    	{
				   checkfile[i].delete();
		    	}
		    	else
		    	{
		    		return "0";
		    	}
				
			}
		}
		/*
		if(file.listFiles().length<count)
		{
			return "0";
		}
		else
		{
			return "1";
		}*/
		return "1";
	   
	
	}
	
	/*
	 * ���ڲ���*.list�ļ�,ע�⣺ȯ��ֻ�ܴ�һ��list�ļ�
	 * @param file      ָ��ǰ�����ȯ���ļ���
	 */
	public static String Find_ListFile(File file) 
	{
		File checkfile[] = file.listFiles();
		for(int i=0;i<checkfile.length;i++)
		{
			String filename = checkfile[i].getName();		
			if(filename.toUpperCase().indexOf(".LIST")>-1)
			{
				return  filename;
			}
		}
	
	    return null;
	}
	
	
	/*
	 * ���ڱ���ȯ�̱��͹�����ԭʼ�ļ�
	 */
	public static  void CopyFile(File file,String copyjmfile,String path_mulu,String sjrq)
	{
		String filename = file.getName();
		
		File   wf = null; 
		//File   wf_directory = null;
		/*�洢Ҫ���ݵ�������ļ�Ŀ¼·��*/
		String path =copyjmfile;
		
		/*�洢��Ҫ���ݵ��ļ���Է�������Ŀ·�����·��*/
		String sub_path ="";
		
		sub_path = (file.getPath()).substring(path_mulu.length());
		
		if(file.isDirectory())
		{
			CreateFile(path+sub_path+"/");
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
		     	  	String filePath = fileList[i].getPath();
		     	  	CopyFile(new File(filePath),copyjmfile,path_mulu,sjrq);
		  	}
		}
		else
		{
			try {
				/*				
				String sjrq = null;
				//����list�ļ�
		    		String type =file.getName().split("\\.")[1];
				if("LIST".equals(type.toUpperCase()))
		    		{
			    	sjrq = filename.split("_")[2].substring(0, 8);
		    		}
				else
				{
					sjrq = filename.split("_")[2];
				}*/
				String sub_directory = sub_path.substring(0,sub_path.length()-filename.length());
				CreateFile(path+sub_directory+sjrq+"/");
				wf = new File(path+sub_directory+sjrq+"/"+filename);
				/**
				 * ????
				 */
				if(wf.exists())
				{
					wf = null;
					wf = new File( path+sub_path.substring(0,sub_path.length()-filename.length())+sjrq+"/"+filename.split("\\.")[0]+"_"+new Date().getTime()+"."+filename.split("\\.")[1]);
				}
				FileInputStream fis = new FileInputStream(file);
				logger.debug(file.getPath());
				logger.debug(wf.getPath());
				
				FileOutputStream fos = new FileOutputStream(wf);
				
				byte[] byteContent = new byte[1024*4];
 	            
				int  count = 0;
 				while((count=fis.read(byteContent, 0, 1024*4))>0)
 				{
 					 if(count<1024*4)
	 				{
	 						byte[] content1=new byte[count];
	 						for(int j=0;j<count;j++)
		 					{
		 						content1[j]=byteContent[j];
		 					}
	 						fos.write(content1);
	 				}
 					 else
 					 {
 						 fos.write(byteContent);
 					 }
 				}
				/*
				BufferedReader  br  = new BufferedReader(new InputStreamReader(fis));
				
				BufferedWriter bw   = new BufferedWriter(new OutputStreamWriter(fos));
				String s ="";
	
				while((s=br.readLine())!=null)
				{

					bw.write(s+"\n");
					
				}
				*/
				fos.close();
				fis.close();
			}catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				logger.debug("�ڱ����ļ�ʱ,"+file.getName()+"û���ҵ�!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.debug("�ڱ����ļ�ʱ,��ȡ"+file.getName()+"�ļ�����!");
			}
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
