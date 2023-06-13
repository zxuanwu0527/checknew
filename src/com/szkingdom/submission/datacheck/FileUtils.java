package com.szkingdom.submission.datacheck;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;

import com.szkingdom.submission.datacheck.DataCheckProperty;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.szkingdom.submission.datacheck.DataConstants.DataTye;

import com.zj.sm4.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

public class FileUtils {

	/**
	 * @param args
	 */
	private static Logger logger = Logger.getLogger(FileUtils.class);
	
	static String RUN_ORDER = "run_orders";
	static String CHECK_TYPE = "check_type";
	static String CHECK_DETAIL_TYPE = "check_detail_type";
	static Pattern headPattern = Pattern.compile("Version=\\d+\\.\\d+\\|RecordCount=(\\d+)");
	private static final DateFormat yyyymmdd = new SimpleDateFormat("yyyyMMdd");
    
    static Pattern namePattern = Pattern.compile("^SC_\\w{8}_\\d{8}_(Y|N)\\w\\.7Z$");
    static Pattern namePatternTmp = Pattern.compile("^SC_\\w{8}_\\d{8}_(Y|N)\\w\\.7Z$");
	
	public static void main(String[] args) {
//		PMFile(new File("E:\\rzrqcheck\\original\\11010000"));
//		System.out.println(checkListFile(new File("E:\\rzrqcheck\\original\\11010000\\SC_11010000_20130326091422.LIST"), new File("E:\\rzrqcheck\\original\\11010000")));
//		init("11010000","20130326","1409800055735");
//		String head = "Version=1.0|RecordCount=1";
//		Matcher matcher = headPattern.matcher(head);
//		if(matcher.find()){
//			System.out.println(matcher.group(1));
//		}
//		
//		RandomAccessFile br = null;
//		LineNumberReader lnr = null;
//		try {
//			File f = new File("E:\\rzrqcheck\\successfile\\11010000\\20130326\\SC_11010000_20130326_001N_B01_Q.TXT");
//			// ������ļ��Ĵ洢���������˲���,ִ������if�µĴ���
//			br = new RandomAccessFile("E:\\rzrqcheck\\successfile\\11010000\\20130326\\SC_11010000_20130326_001N_B01_Q.TXT", "r");
//			lnr = new LineNumberReader(new FileReader(f));
//			int lineNum = 0;
//			while( br.readLine() != null){
//				lineNum++;
//			}
//			System.out.println(lineNum);
//			lnr.skip(f.length());
//			System.out.println(lnr.getLineNumber());
//			br.close();
//			lnr.close();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		System.out.println(check_List_Sjrq(new File("E:\\rzrqcheck\\checkFile\\10150000\\SC_10150000_20121221000548.LIST")));
//		System.out.println(getInterfaceDate(new File("E:\\rzrqcheck\\checkFile\\10150000")));
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("a", "a");
//		map.put("b", "b");
//		map.put("c", "c");
//		System.out.println(map.size());
//		map.remove("a");
//		System.out.println(map.size());
//		CheckTxtHeadNRecordCount(new File("E:\\rzrqcheck\\successfile\\10730000\\20130712\\SC_10730000_20130712_001N_C09_Q.TXT"), null, null);
		boolean q = checkIsQDayList(new File("E:\\rzrqcheck\\checkFile\\10040000\\20141017\\SC_10040000_20141017140817.LIST"));
		System.out.println(q);
		q = checkIsQDayList(new File("E:\\rzrqcheck\\checkFile\\10040000\\20141020\\SC_10040000_20141020085616.LIST"));
		System.out.println(q);
		System.out.println("20150206 isfriday:"+isFriDay("20150206"));
		System.out.println("20150205 isfriday:"+isFriDay("20150205"));
	}

	static void saveToKrcs(DataCheckProperty dcp, File file){
		if(file.isDirectory()){
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				saveToKrcs(dcp, fileList[i]);
		  	}
		}else{
			String filename = file.getName();
			String encoding =dcp.getFileEncoding();
			/* ʵ�ʱ������,��xml/FileOrder.xml�����õ� */
			int cols_table = DataCheckRunner.colNumMap.get(filename.split("_")[4]);
			FileUtils.insertData(file, encoding, cols_table);
		}
	}
	
	static void saveListFilesToKrcs(DataCheckProperty dcp,File[] fileList){
		for (int i = 0; i < fileList.length; i++) {

			String filename = fileList[i].getName();
			//ֻ�����list�嵥�е��ļ�
			String encoding =dcp.getFileEncoding();
			/* ʵ�ʱ������,��xml/FileOrder.xml�����õ� */
			int cols_table = DataCheckRunner.colNumMap.get(filename.split("_")[5]);
			FileUtils.insertData(fileList[i], encoding, cols_table);
		}
	}
	
	
	
	/**
	 * ����洢�����ļ������ҽ��ɹ������ݴ������ݿ�
	 * @param file
	 * @param zqgsdm
	 * @param sjrq
	 * @param fileCheckSucc
	 */
	static  void classifySubmissionFiles(DataCheckProperty dcp, File file, String zqgsdm, String sjrq, boolean fileCheckSucc) {
		
		String filename = file.getName();
		/**
		 * ���ñ��ݵ�Ŀ��·��
		 */
		String destDir = fileCheckSucc ? dcp.getSuccessFileDir() : dcp.getFailFileDir();
		File   wf = null; 
		if(file.isDirectory()){
			CreateFile(destDir+ File.separator + zqgsdm);
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
		     	  	classifySubmissionFiles(dcp,  fileList[i], zqgsdm, sjrq, fileCheckSucc);
		  	}
		}else {
			try {
				CreateFile(destDir + File.separator + zqgsdm + File.separator + sjrq);
				wf = new File(destDir + File.separator + zqgsdm + File.separator + sjrq + File.separator + filename);
				if (wf.exists()) {
					wf = new File(destDir + File.separator + zqgsdm + File.separator +  sjrq
							+ File.separator  + filename.split("\\.")[0] + "_" + new Date().getTime() + "."
							+ filename.split("\\.")[1]);
				}
				if(logger.isDebugEnabled()){
					logger.debug("copy file ,FROM: "+file.getPath() + " TO:"+wf.getPath());
				}
				fileChannelCopy(file, wf);
			}  catch (NumberFormatException e) {
				logger.error("�ڱ����ļ�ʱ,��ȡ" + file.getName() + "�ļ��ı�ų���!", e);
			} finally {
				file.delete();
			}
		}
	}
	
	
	/**
	 * ����洢�����ļ������ҽ��ɹ������ݴ������ݿ�
	 * @param file
	 * @param zqgsdm
	 * @param fileLists   list�嵥�е��ļ���
	 * @param sjrq
	 */
	static  void classifySubmissionListFiles(DataCheckProperty dcp, File[] fileList, String zqgsdm, String sjrq) {		
		/**
		 * ���ñ��ݵ�Ŀ��·��
		 */
		String destDir =  dcp.getSuccessFileDir();
		File   wf = null; 

		CreateFile(destDir+ File.separator + zqgsdm);
		CreateFile(destDir + File.separator + zqgsdm + File.separator + sjrq);
		for (int i = 0; i < fileList.length; i++) {				
//			File srcFile = new File(file.getPath()+File.separator+fileLists.get(i));
				
			try {			
				wf = new File(destDir + File.separator + zqgsdm + File.separator + sjrq + File.separator + fileList[i].getName().substring(2));
				if (wf.exists()) {
					wf = new File(destDir + File.separator + zqgsdm + File.separator +  sjrq
							+ File.separator  + fileList[i].getName().split("\\.")[0].substring(2) + "_" + new Date().getTime() + "."
							+ fileList[i].getName().split("\\.")[1]);
				}
				if(logger.isDebugEnabled()){
					logger.debug("copy file ,FROM: "+fileList[i].getPath() + " TO:"+wf.getPath());
				}
				
				
				if(fileList[i].exists()){fileChannelCopy(fileList[i], wf);}
				else logger.error("�ڱ����ļ�ʱ,Դ·���µ� " +fileList[i].getPath() +"������");
				
						
			}  catch (NumberFormatException e) {
				logger.error("�ڱ����ļ�ʱ,��ȡ" + fileList[i].getName() + "�ļ��ı�ų���!", e);
			} finally {
//				srcFile.delete();
			}				
		}
	}
	
	/**
	 * ���ڼ���*.list�ļ������������ڱ�־��*.list�����¼���ļ������������ڱ�־�Ƿ�һ��
	 * @param listfile  ָ��list�ļ�5
	 */
	public static boolean check_List_Sjrq(File listfile) {
        if (listfile.isFile()) {
            String list_sjrq = null;
            FileInputStream fis = null;
            BufferedReader br = null;
            try {
                fis = new FileInputStream(listfile);
                br = new BufferedReader(new InputStreamReader(fis));
                String s = "";
                while ((s = br.readLine()) != null) {
                    String sjrq = s.split("_")[3];
                    if ((list_sjrq == null) || ("".equals(list_sjrq))) {
                        list_sjrq = sjrq;
                    } else {
                        if (!sjrq.equals(list_sjrq)) {
                            return false;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("error", e);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }
            }
            return true;
        }
        return false;
    }
	
	/**
	 * ���� *.list �е��ļ���
	 * @param listfile  ָ��list�ļ�5
	 */
	public static List<String> getFileListFromList(File listfile) {
		List<String> fileList = new ArrayList<String>();
		
		if(listfile.isFile()) {
			FileInputStream fis = null;
			BufferedReader br = null;
			try {
				fis = new FileInputStream(listfile);
				br  = new BufferedReader(new InputStreamReader(fis));
				String s ="";
				while((s = br.readLine())!=null) {
						fileList.add(s.trim());
				}
			}catch (Exception e){
				logger.error("error", e);
			}finally{
				if(br != null){
					try {
						br.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
				if(fis != null){
					try {
						fis.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
			return fileList;
		}
		return fileList;
	}
	
	
	
	
	
	
	
	/**
	 * �ж������������Ƿ�Ϊ����
	 * @param sjrq
	 * @return
	 */
	public static boolean isFriDay(String sjrq){
		try {
			Date date = yyyymmdd.parse(sjrq);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
				return true;
			}
		} catch (ParseException e) {
			logger.error(sjrq+" is not friday", e);
		}
		return false;
	}
	/**
	 * ���ڼ���*.list�ļ��е��ļ��Ƿ���ȫ��
	 * @param listfile  ָ��list�ļ�
	 */
	public static boolean checkIsQDayList(File listfile) {
		if(listfile.isFile()) {
			FileInputStream fis = null;
			BufferedReader br = null;
			try {
				fis = new FileInputStream(listfile);
				br  = new BufferedReader(new InputStreamReader(fis));
				String s ="";
				while((s = br.readLine())!=null) {
					String flag = s.split("_")[5];
					if(!(flag.startsWith("q" ) || flag.startsWith("Q"))){
						return false;
					}
				}
			}catch (Exception e){
				logger.error("error", e);
			}finally{
				if(br != null){
					try {
						br.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
				if(fis != null){
					try {
						fis.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
			return true;
		}
		return false;
	}
	
	
	/*
	 * ���ڼ���.list�ļ�����ļ�¼���������ļ�������
	 */
	 public static int count(File listfile) {
		 int j = 0; //��¼�ļ�����
		 BufferedReader br = null;
		 try {
			 FileInputStream fis = new FileInputStream(listfile);
			 br = new BufferedReader(new InputStreamReader(fis));
			 while (StringUtils.isNotBlank(br.readLine())) {
				 j++;
			 }
		 } catch (IOException e) {
			 logger.error(e);
			 return 0;
		 } finally {
			 if (br != null) {
				 try {
					 br.close();
				 } catch (IOException e) {
				 }
			 }
		 }
		 return j;
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
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String  nowdate = sdf.format(report_file_date);
			
//			System.out.println("nowdate="+nowdate);
			int   reportdate = Integer.parseInt(nowdate.substring(0, 8));
			int   reporttime = Integer.parseInt(nowdate.substring(8, 14));
//			System.out.println("reportdate="+reportdate+"");
//			System.out.println("reporttime="+reporttime+"");
			
			if((reporttime>=Integer.parseInt(stoptimebegin))&&(reporttime<=Integer.parseInt(stoptimeend)))
			{
				File notice = new File(reportfile_path+"/"+receivefile_name+"/��ܰ��ʾ.txt");
				CreateFile(reportfile_path+"/"+receivefile_name+"/");
//				System.out.println(notice.getPath());
				if(notice.exists())
				{
					long  updatetime = notice.lastModified();
					String   lastdate  =  sdf.format(new Date(updatetime));
					int      updatedate =  Integer.parseInt(lastdate.substring(0, 8));
//					System.out.println("update="+updatedate);
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
							if(logger.isDebugEnabled()){
								logger.debug("������OK�ļ�ʱ�������ļ�ʧ��!",  e);
							}
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
						logger.error("������OK�ļ�ʱ�������ļ�ʧ��!", e);
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
    		logger.error("e", e);
    		return "0";
    	}
	}
	
	/**
	 * �������ȯ�̱��͹�������ز��Ϸ��ļ����Ա�ȯ����Ϊ���Ϸ��ļ����·������������ļ��У���7z��list�ļ���
	 * �����Ҫ�����ļ����µ��ļ�Ϊ�գ��������±��͡�
	 */	
	public static void delFolderorFile(File folderFile) {
		try{
			File[] tempList = folderFile.listFiles();
			for (int i = 0; i < tempList.length; i++)  {
				if (tempList[i].isDirectory())  {
					delFolder(tempList[i]);
				} else {
					String type =tempList[i].getName().split("\\.")[1];
					if((!"LIST".equals(type.toUpperCase()))&&(!"7Z".equals(type.toUpperCase()))) {
						tempList[i].delete();
					}
				}
		     }
		}
	    	catch(Exception e) {
	    		logger.error(e);
	    	}
	}
	
	/**
     * �������ȯ�̱��͹�������ز��Ϸ��ļ����Ա�ȯ����Ϊ���Ϸ��ļ����·������������ļ��У���7z��list�ļ���
     * �����Ҫ�����ļ����µ��ļ�Ϊ�գ��������±��͡�
     */
    public static void delFolderorFileWithRegex(File folderFile) {
    	try {
    		File[] tempList = folderFile.listFiles();
    		for (int i = 0; i < tempList.length; i++) {
    			if (tempList[i].isDirectory()) {
    				delFolder(tempList[i]);
    			} else {
    				String filename = tempList[i].getName().toUpperCase();
    				Matcher matcherFile = namePattern.matcher(filename);
    				if (!matcherFile.find()) {
    					tempList[i].delete();
    				}
    			}
    		}
        } catch(Exception e) {
	    	logger.error(e);
		}
    }
	
	public static boolean delBak(String zqgsdm){
		if(logger.isDebugEnabled()){
			logger.debug("***********��ʼɾ��"+zqgsdm+"���ÿ�����***********");
		}
		String  sql ="{call krcs.proc_check_delete_byksj(?,?)}";
		String pro_flag = null;
		DbOper dao = (DbOper)ObjectFactory.instance().getBean("dao");
		Object[] inParams = {zqgsdm};
		DataTye[] inType = {DataTye.VARCHAR};
		int[] inOrder = {1}; 
		DataTye[] outType =  {DataTye.VARCHAR};;
		int[] outOrder ={2};							
		try{
			Map<Integer, Object> result = 
					dao.exeProcedure(sql, inParams, inType, inOrder, outType, outOrder);
			pro_flag = (String)result.get(2);
			if(StringUtils.equals("1", pro_flag)){
				return true;
			}
		}catch (Exception e){
			logger.error("ɾ�����ÿ�洢���̳���", e);
		}
		return false;
	}
	
	/**
	 *
	 * �������ȯ�̱��͹���������ļ����Ա�ȯ�̿��Լ������ͣ�
	 * �����Ҫ�����ļ����µ��ļ�Ϊ�գ��������±��͡�
	 * @param file
	 * @return
	 */
	static void deleteAllFile(File file) {
		File[] fileList = file.listFiles();
		boolean delFlag = true;
		for (int i = 0; i < fileList.length; i++) {
			File deletefile = fileList[i];
			if(deletefile.isDirectory()) {
				delFolder(deletefile);
			}
			else {
			    	try {
			    		delFlag = fileList[i].delete();
			    		if(!delFlag){
			    			logger.error("del file :"+fileList[i].getPath()+" failed!");
			    		}
			    	}catch(Exception e) {
			    		logger.error("del file:"+fileList[i].getPath() +" error", e);
			    	}
			}
	  	}
	}
	
	static void delFolder(File folderFile)  {
		try {
			File[] tempList = folderFile.listFiles();
			for (int i = 0; i < tempList.length; i++)  {
				if (tempList[i].isDirectory()) {
					delFolder(tempList[i]);	        	         	
				} else{
					tempList[i].delete();
				}
			}
			folderFile.delete();	
	    	}
	    	catch(Exception e){
	    		logger.error("error", e);
	    	}
	}
	
	
	
	/*
	 * �����ļ��Ƿ������
	 * @param file  ָ��ȯ�̱��͹������ļ���ŵ��ļ���
	 */
	public static boolean check_Transmission(File file,int interfacecount){
		try{
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				//����fdepƽ̨��־��δ������ɵ��ļ�����.szt!
			    	String filename =fileList[i].getName();
			    	if(filename.indexOf(".szt!")>-1) {
				    	return false;
			    	}
			    	if(!fileList[i].canRead()){
			    		return false;
			    	}
		  	}
			if((fileList.length-1) < interfacecount) {
				return false;
			}
			return true;
		}catch (Exception e){ 
			logger.error("error", e);
			return false;
	       }
	}
	
	
   /**
    * ʹ���ļ�ͨ���ķ�ʽ�����ļ�
    * 
    * @param s
    *            Դ�ļ�
    * @param d
    *            ���Ƶ������ļ�
 * @throws IOException 
    */
    public static void fileChannelCopy(File s, File d){
	    
	    try {
		    org.apache.commons.io.FileUtils.copyFile(s, d);
	    } catch (Exception e) {
		    logger.error("error in channel copy!", e);
	    } finally {
	    }
    	}
    
	/**
	 * ���ڱ���ȯ�̱��͹�����ԭʼ�ļ�
	 * ���ļ�File������backupEncryptFileDir
	 */
	public static  void CopyFile(File file, String backupEncryptFileDir, String zqgsdm, String sjrq) {
		
		String filename = file.getName();
		File   wf = null; 
		String dateDir = null;
		if(file.isDirectory()){
			CreateFile(backupEncryptFileDir + File.separator + zqgsdm);
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
		     	  	String filePath = fileList[i].getPath();
		     	  	CopyFile(new File(filePath),backupEncryptFileDir,zqgsdm,sjrq);
		  	}
		}
		else {
			dateDir =backupEncryptFileDir + File.separator + zqgsdm + File.separator + sjrq;
			CreateFile(dateDir);
			wf = new File(dateDir+ File.separator +filename);
			if(wf.exists()) {
				wf = null;
				wf = new File( dateDir + File.separator +filename.split("\\.")[0]+"_"+new Date().getTime()+"."+filename.split("\\.")[1]);
			}
			fileChannelCopy(file, wf);
		}
	}
	
	/**
	 * ����������ȯ�̵Ľ����ļ����÷���ֻ������������µĽ����ļ� ��һ�֣�ȯ�������ļ���ͨ������У�飬���ɳɹ��ļ���
	 * �ڶ��֣�ȯ�������ݵ��˱������ݿ⣬���ڲ��������ݵ�У��������ɴ����excel�ļ�
	 **/
	public static void createResultFile(DataCheckProperty dcp, String zqgsdm, String sjrq, String milltime, boolean flag) {

		DbOper dao = (DbOper)ObjectFactory.instance().getBean("dao");
		String reportfile = dcp.getReportFileDir();
		CreateFile(reportfile + zqgsdm + "/");

		Date report_file_date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		File report_file = null;

		try{
			String sql = "select t1.check_name,t.check_rs_mou from t_Krms_Check_Result t,t_krms_check_dict t1 where t.check_id=t1.check_id and t.check_rtn ='"
					+ milltime + "' and t.zqgsdm ='" + zqgsdm + "' and t.check_rs ='02'";
			List<Map<String, String>> result = dao.queryList(sql);
			logger.info("***********��ʼ���ɱ����ļ�***********");
			/*
			 * ��ǰ��ϵͳ��ǰʱ��Ϊ�����ļ�����
			 */
			if (flag) {
				report_file = new File(reportfile + zqgsdm + "/" + zqgsdm + "_" + sjrq + "_SUCCESS.txt");
				if (report_file.exists()) {
					report_file = null;
					report_file = new File(reportfile + zqgsdm + "/" + zqgsdm + "_" + sjrq + "_SUCCESS_"
							+ report_file_date.getTime() + ".txt");
				}
			} else {
				report_file = new File(reportfile + zqgsdm + "/" + zqgsdm + "_" + sjrq + "_FAILURE.xls");
				if (report_file.exists()) {
					report_file = null;
					report_file = new File(reportfile + zqgsdm + "/" + zqgsdm + "_" + sjrq + "_FAILURE_"
							+ report_file_date.getTime() + ".xls");
				}
			}

			if (flag) {
				FileOutputStream fos = null;
				BufferedWriter bw = null;
				try{
					fos = new FileOutputStream(report_file);
					bw = new BufferedWriter(new OutputStreamWriter(fos));
					String s = zqgsdm + "  " + sjrq + "�ϱ������Ѿ�ͨ������ɹ���";
					bw.write(s + "\n");
				}catch(Exception e){
					logger.error("", e);
				}finally{
					if(bw != null){ try { bw.close(); } catch (IOException e) { } }
					if(fos != null){ try { fos.close(); } catch (IOException e) { } }
				}
				try {
					// ��������־��Ϣ
					String sqlsuccess = dcp.getJhjgSql();
					Object[] params = {sjrq, zqgsdm, sdf.format(report_file_date),  Long.parseLong(milltime), "01", "01", "01"};
					DataTye[] type = {DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.LONG, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR};				
					dao.insert(sqlsuccess, params, type);
				} catch (DaoException e) {
					logger.error("��������־��Ϣ�쳣", e);
				}
			} 
			else {
				WritableWorkbook book = null;
				try{
					// excelҳ��
					int excel_count = 0;
					book = Workbook.createWorkbook(report_file);
					// ������Ϊ"���󱨸�"�Ĺ���������0��ʾ���ǵ�һҳ
					WritableSheet sheet = book.createSheet("���󱨸�", 0);
					WritableFont font = new WritableFont(WritableFont.TAHOMA, 9, WritableFont.BOLD);// ��������
					font.setColour(Colour.BLACK);// ������ɫ
					WritableCellFormat wc = new WritableCellFormat(font);
					wc.setAlignment(Alignment.CENTRE); // ���þ���
					wc.setBorder(Border.ALL, BorderLineStyle.THIN); // ���ñ߿���
					
					int rows = 1;
					Label lable = null;
					
					// ���ñ�ͷ
					lable = new Label(0, 0, "��������", wc);
					sheet.addCell(lable);
					
					lable = new Label(1, 0, "����", wc);
					sheet.addCell(lable);
					
					if(result != null && result.size() > 0){
						for(Map<String, String> row : result){
							int index = 0;
							for (Entry<String, String> entry : row.entrySet()) {
								if (entry!= null) {
									sheet.setColumnView(index, entry.getValue().length() * 2);
									lable = new Label(index++, rows % 50000, entry.getValue(), wc);
								}
								// ������õĵ�Ԫ����ӵ���������
								sheet.addCell(lable);
							}
							if (++rows % 50000 == 0) {
								excel_count++;
								sheet = book.createSheet("���󱨸�" + excel_count, excel_count);
							}
						}
					}
					book.write();
				}catch(Exception e){
					logger.error("��������ļ��쳣", e);
				}
				finally{
					if(book != null){
						try {book.close(); } catch (Exception e) { } 
					}
				}	
				try {
					// ��������־��Ϣ
					String sqlfailure =dcp.getJhjgSql();
					Object[] params = {sjrq, zqgsdm, sdf.format(report_file_date),  Long.parseLong(milltime), "02", "01", "01"};
					DataTye[] type = {DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.LONG, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR};				
					dao.insert(sqlfailure, params, type);
				} catch (Exception e) {
					logger.error("��������־��Ϣ�쳣", e);
				}
			}
			// ���ɼ�����󱨸���fdepƽ̨�ϵ�ok��־
			if (report_file != null) {
				File okfile = new File(report_file.getPath() + ".ok");
				try {
					FileOutputStream fis = new FileOutputStream(okfile);
					fis.close();
				} catch (IOException e2) {
					logger.error(e2);
				}
			}
		}catch(Exception e){
			logger.error("��ȡ�������쳣���޷����ɽ���ļ�", e);
		}
	}
	// �������ű�һ������ϸ����罨�ģ���һ���ǽ���У���
	public static void createResultFile(DataCheckProperty dcp, String zqgsdm, String sjrq, String milltime, String information, boolean flag) {

//		DataCheckProperty dcp = (DataCheckProperty) ObjectFactory.instance().getBean("dataCheckProperty");

		String reportfile = dcp.getReportFileDir();
		CreateFile(reportfile + File.separator + zqgsdm);
		Date report_file_date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		File report_file = null;
		try {

			if(logger.isDebugEnabled()){
				logger.debug("***********��ʼ���ɱ����ļ�***********");
			}
			/*
			 * ��ǰ��ϵͳ��ǰʱ��Ϊ�����ļ�����
			 */
			if (flag) {
				report_file = new File(reportfile + zqgsdm + "/" + zqgsdm + "_" + sjrq + "_SUCCESS.txt");
				if (report_file.exists()) {
					report_file = null;
					report_file = new File(reportfile + zqgsdm + "/" + zqgsdm + "_" + sjrq + "_SUCCESS_"
							+ report_file_date.getTime() + ".txt");
				}
			} else {
				report_file = new File(reportfile + zqgsdm + "/" + zqgsdm + "_" + sjrq + "_FAILURE.xls");
				if (report_file.exists()) {
					report_file = null;
					report_file = new File(reportfile + zqgsdm + "/" + zqgsdm + "_" + sjrq + "_FAILURE_"
							+ report_file_date.getTime() + ".xls");
				}
			}
			if (flag) {
				FileOutputStream fos = null;
				BufferedWriter bw = null;
				try{
					fos = new FileOutputStream(report_file);
					bw = new BufferedWriter(new OutputStreamWriter(fos));
					String s = zqgsdm + "  " + sjrq + "�ϱ������Ѿ�ͨ������ɹ���";
					bw.write(s + "\n");
				}catch(Exception e){
					logger.error("", e);
				}finally{
					if(fos != null){ fos.close(); }
					if(bw != null){ bw.close(); }
				}
			} else {
				WritableWorkbook book = Workbook.createWorkbook(report_file);
				// ������Ϊ"���󱨸�"�Ĺ���������0��ʾ���ǵ�һҳ
				WritableSheet sheet = book.createSheet("���󱨸�", 0);
				WritableFont font = new WritableFont(WritableFont.TAHOMA, 9, WritableFont.BOLD);// ��������
				font.setColour(jxl.format.Colour.BLACK);
				// ������ɫ
				WritableCellFormat wc = new WritableCellFormat(font);
				wc.setAlignment(jxl.format.Alignment.CENTRE); // ���þ���
				wc.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN); // ���ñ߿���
				// ���õ�Ԫ��ı�����ɫ
				Label lable = null;
				// ���ñ�ͷ
				lable = new Label(0, 0, "��������", wc);
				sheet.addCell(lable);
				lable = new Label(1, 0, "����", wc);
				sheet.addCell(lable);
				// ��д������Ϣ
				lable = new Label(0, 1, "000000", wc);
				sheet.addCell(lable);

				sheet.setColumnView(1, information.length() * 2);
				lable = new Label(1, 1, information, wc);
				sheet.addCell(lable);
				book.write();
				book.close();
			}

			try{
				// ��������־��Ϣ
				DbOper dao = (DbOper)ObjectFactory.instance().getBean("dao");
				String sql1 = dcp.getJhjgSql();//"insert into rzrq.T_SJZX_RZRQ_JKSJ_JHJG(SJRQ,ZQGSDM,JYRQ,CHECK_RTN,JYJG,TZBZ,sfsb) values(?,?,?,?,?,?,?)";
				Object[] params1 = {sjrq, zqgsdm,  sdf.format(report_file_date), Long.parseLong(milltime), "02",
						"01", "01" };
				DataTye[] type1 = {DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.LONG,
						DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR};
				dao.insert(sql1, params1, type1);
	
				// ������ؼ�����Ϣ����ϸ��
				String sql2 = "insert into krcs.t_krms_check_jksj_jksbcwxx(SJRQ,ZQGSDM,JCRQ,CHECK_RTNX,CWXX) "
						+ "values(?,?,?,?,?)";
				
				Object[] params2 = {sjrq, zqgsdm, sdf.format(report_file_date), Long.parseLong(milltime), information};
				DataTye[] type2 = {DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR,  DataTye.LONG, DataTye.VARCHAR,};
				dao.insert(sql2, params2, type2);
			} catch (Exception e) {
				logger.error("����У����Ϣ�쳣", e);
			}
			// ���ɼ�����󱨸���fdepƽ̨�ϵ�ok��־
			if (report_file != null) {
				File okfile = new File(report_file.getPath() + ".ok");
				try {
					FileOutputStream fis = new FileOutputStream(okfile);
					fis.close();
				} catch (IOException e) {
					logger.error("������OK�ļ�ʱ�������ļ�ʧ��!", e);
				}
			}
		} catch (Exception e) {
			logger.error("��������ļ�ʧ��", e);
		}
	}
	
	
	/**
	 * ���ڴ����ļ��У���������ڣ��򴴽����ļ���
	 * @param path 
	 */
	static  void   CreateFile(String   path){ 
		File   file   =   new   File(path);    
		if(file.exists() && file.isDirectory()){  
	   	}else{
	   		file.mkdirs();
			if(logger.isDebugEnabled()){
				logger.debug("mkdirs:"+path);
			}
	   	}
	}    
	
	/**
	 * ���ڲ���*.list�ļ�,ע�⣺ȯ��ֻ�ܴ�һ��list�ļ�
	 * @param file      ָ��ǰ�����ȯ���ļ���
	 */
	public static String Find_ListFile(File file)  {
		File checkfile[] = file.listFiles();
		if (checkfile == null) {
			return null;
		}
		for(int i=0;i<checkfile.length;i++) {
			String filename = checkfile[i].getName();
			if(filename.toUpperCase().indexOf(".LIST")>-1) {
				return  filename;
			}
		}
		return null;
	}
	
	/**
	 * ���ڲ���*.list�ļ�,ע�⣺ȯ��ֻ�ܴ�һ��list�ļ�
	 * @param file      ָ��ǰ�����ȯ���ļ���
	 */
	public static File Find_ListFile_And_Return(File file)  {
		File checkfile[] = file.listFiles();
		for(int i=0;i<checkfile.length;i++) {
			String filename = checkfile[i].getName();		
			if(filename.toUpperCase().indexOf(".LIST")>-1) {
				return  checkfile[i];
			}
		}
	    return null;
	}
	
	/**
	 * ���ڵõ��ϱ��Ľӿ������ļ���׼ȷ����
	 */
	public static String getInterfaceDate(File file){
		FileFilter filter = new FileFilter(".7z");
		File[] fileList = file.listFiles(filter);
		
		for (int i = 0; i < fileList.length; i++) {
			if (!fileList[i].isDirectory()) {
				Matcher matcher = namePattern.matcher
						(fileList[i].getName().toUpperCase());
				if (matcher.find()) {
					return fileList[i].getName().split("_")[2];
				}
			}
		}

		return null;
	}
	
	/*
	 *���ڼ���*.list�����¼���ļ�������������Ƿ�ͱ��͹���������һ�£�����ɾ��*.list�ļ�
	 * ���磺���͵���*.TXT����list�ļ������Ƿ�Ҳ��*.TXT��
	 * @param listfile  ָ��list�ļ�
	 * @param file      ָ��ǰ�����ȯ���ļ���
	 * ע�⣺*.TXT ��*.txtͬ������Ϊ�ļ�����һ��
	 */
	public static boolean checkListFile(File listfile, File dir) {
		if (listfile.isFile()) {
			BufferedReader br = null;
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(listfile);
				br = new BufferedReader(new InputStreamReader(fis));
				File[] allFiles = dir.listFiles();
				String l = null;
				boolean matchName = false;
				while ((l = br.readLine()) != null) {
					for(File f : allFiles){
						String[] s = l.split("\\|");
						if(f.getName().toUpperCase().equals(s[0].trim().toUpperCase())){
							matchName = true;
							break;
						}
					}
					if(!matchName){
						return false;
					}
					matchName = false;
				}
			} catch (IOException e) {
				logger.error(e);
				return false;
			}finally{
				if(br != null){
					try {
						br.close();
					} catch (IOException e) {}
				}
			}
			listfile.delete();
			return true;
		} else {
			return false;
		}
	}
	
	@SuppressWarnings("rawtypes")
	static boolean CheckFile(File[] fileList, String check_rtn) {
		boolean checkFileSucc = true;
		try {
			for (int i = 0; i < fileList.length; i++) {
				/**
				 * �����汸�ÿ����ݼ�������ֵ�ã�zqgsdm Ϊ���̼߳����ȯ�̵Ĺ�˾���룬 
				 * sjrqΪ��ȯ���������ݵ��������� 
				 * ע�������������Ա����ļ��ϵ���ϢΪ׼
				 */
				Node node1 = DataCheckRunner.orderConfig.selectSingleNode("//order/type[@id='file']");

				if (node1 == null) {
					if(logger.isDebugEnabled()){
						logger.debug("�����ļ�û�������ļ����,���������ļ��Ƿ���Ҫ���!");
					}
					return false;
				} else {
					List nodes = node1.selectNodes("dict");
					if (nodes.size() == 0) {
						if(logger.isDebugEnabled()){
							logger.debug("�ļ������,û�����ò���ڵ�!");
						}
						return false;
					} else {
						boolean flag = true;
						String filename = fileList[i].getName();
						if(logger.isDebugEnabled()){
							logger.debug("�ļ�����" + filename);
						}

						int j = 0;// ���ڼ�¼���ɹ��˵Ĳ�����
						for (; j < nodes.size(); j++) {
							if (nodes.get(j) != null) {
								Element element = (Element) nodes.get(j);

								/* ���洢���̵ļ���,û�в��� */
								List pros = element.selectNodes("pro");

								/* ���⴦��ֻ���ڼ����ļ���¼�����Ƿ���ȷ */
								List params = element.selectNodes("params");

								if (params.size() != 0) {
									if(logger.isDebugEnabled()){
										logger.debug("���ڼ���" + element.attributeValue("order") + "����,���Ժ�!");
									}
									flag &= CheckTxtHeadNRecordCount(fileList[i], params, check_rtn);
									if (!flag) {
										if(logger.isDebugEnabled()){
											logger.debug("����" + element.attributeValue("order") + "����ʧ��!");
										}
										break;
									}
									if (flag) {
										if(logger.isDebugEnabled()){
											logger.debug("����" + element.attributeValue("order") + "����ɹ�!");
										}
									}
								} else {
									if (pros.size() == 0) {
										if(logger.isDebugEnabled()){
											logger.debug("�ļ������,��" + element.attributeValue("order") + "�����´洢���̽ڵ�û������,���������ļ��Ƿ���Ҫ����!");
										}
									} else {
										if(logger.isDebugEnabled()){
											logger.debug("���ڼ���" + element.attributeValue("order") + "����,���Ժ�!");
										}
										flag &= CheckData(fileList[i], pros, check_rtn);
										if (!flag) {
											if(logger.isDebugEnabled()){
												logger.debug("����" + element.attributeValue("order") + "����ʧ��!");
											}
											break;
										}
										if (flag) {
											if(logger.isDebugEnabled()){
												logger.debug("����" + element.attributeValue("order") + "����ɹ�!");
											}
										}
									}
								}
							}
						}
						checkFileSucc &= flag;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return checkFileSucc;
	}
	
	/**
	 * ����txt�ļ�ͷ�����������Ƿ�һ��
	 * @param file
	 * @param conn
	 * @param prams
	 * @param check_rtn
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	static boolean CheckTxtHeadNRecordCount(File file, List prams, String check_rtn) {
		String filename = file.getName();

		RandomAccessFile br = null;
		LineNumberReader lnr = null;
		try {
			// ������ļ��Ĵ洢���������˲���,ִ������if�µĴ���
			br = new RandomAccessFile(file.getPath(), "r");
			lnr = new LineNumberReader(new FileReader(file));;
			String s = "";
			int recordcount = 0;// �ļ�ͷ��¼�ļ�¼��
			int count = -1;// ʵ�ʼ�¼��
			boolean fileheadFormatRight = false;// ��¼���ļ����ļ�ͷ�Ƿ���ȷ��־
			
			// �����һ�и�ʽ�Ƿ���ȷ
			s = br.readLine();
			Matcher matcher = headPattern.matcher(s);
			if (count == -1 && matcher.find()) {
				count++;
				recordcount = Integer.parseInt(matcher.group(1));
				fileheadFormatRight = true;
			}
			if (fileheadFormatRight)
			{
				lnr.skip(file.length());
				count = lnr.getLineNumber() - 1;
			}
			if(count != recordcount){
				count = 0;
				while((s = br.readLine())!=null){
					count++;
				}
			}
			/* �����ļ���¼���� */
			if (count == recordcount) {
				if(logger.isDebugEnabled()){
					logger.debug("���ļ�" + file.getName() + "��,�ļ�ͷ�ͼ�¼����ȷ!");
				}
				return true;
			} else {
				if (fileheadFormatRight) {
					// ������©����Ҫ�����ļ�ͷ��¼�����ļ��������ʵ��¼��ƥ�䣬��Ȼ�������û������
					// ����û�м�¼������Ϣ�������ļ������Ϊ�գ����������Ϊ��һ����¼����������
					if(logger.isDebugEnabled()){
						logger.debug("���ļ�" + file.getName() + "��,�ļ�ͷ��¼����ƥ��,��¼��Ϊ" + recordcount + ",ʵ��Ϊ" + count + "!");
					}

					String sql = "insert into t_krms_check_result(zqgsdm,check_rtn,check_id,check_son_id,check_son_name,check_rs,check_rs_mou,back_value,check_date,data_code,sjrq) values (?,?,?,?,?,?,?,?,?,?,?)";

					Object[] params = {filename.split("_")[2], check_rtn, 99999, "02", recordcount + "|" + count + "|" + check_rtn, 
							"02", "���ļ�" + filename + "��,�ļ�ͷ��¼����ƥ��,��¼��Ϊ" + recordcount + ",ʵ��Ϊ" + count + "!",
							"2", new SimpleDateFormat("yyyyMMdd").format(new Date()), filename.split("_")[5], filename.split("_")[3]};
					DataTye[] types = {DataTye.VARCHAR, DataTye.VARCHAR, DataTye.NUMBER, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR,
							DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR,};
					
					DbOper dao = (DbOper)ObjectFactory.instance().getBean("dao");
					dao.insert(sql, params, types);
					return false;
				} else {
					// ������©����Ҫ�����ļ�ͷ��¼ͷ�����⣬��Ȼ�������û������
					// ����û�м�¼������Ϣ�������ļ������Ϊ�գ����������Ϊ��һ����¼����������
					logger.error("���ļ�" + filename + "��,�ļ�ͷ��ʽ����ȷ!");

					String sql = "insert into t_krms_check_result(zqgsdm,check_rtn,check_id,check_son_id,check_son_name,check_rs,check_rs_mou,back_value,check_date,data_code,sjrq) values (?,?,?,?,?,?,?,?,?,?,?)";

					Object[] params = {filename.split("_")[2], check_rtn, 99999, "02", recordcount + "|" + count + "|" + check_rtn, 
							"02", "���ļ�" + filename + "��,�ļ�ͷ��ʽ����ȷ!",
							"2", new SimpleDateFormat("yyyyMMdd").format(new Date()), filename.split("_")[5], filename.split("_")[3]};
					DataTye[] types = {DataTye.VARCHAR, DataTye.VARCHAR, DataTye.NUMBER, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR,
							DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR,};
					
					DbOper dao = (DbOper)ObjectFactory.instance().getBean("dao");
					dao.insert(sql, params, types);
					return false;
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("�ļ�" + file.getName() + "������!", e);
			return false;
		} catch (IOException e) {
			logger.error("��ȡ�ļ�" + file.getName() + "����", e);
			return false;
		} catch (DaoException e) {
			logger.error("", e);
			return false;
		}finally{
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
				}
			}
			try
			{
				if (lnr != null)
					lnr.close();
			}
			catch (IOException ioexception1) { }
		}
	}

	@SuppressWarnings("rawtypes")
	static boolean CheckData(File file, List pros, String check_rtn) {
		String filename = file.getName();
		boolean checkFileSucc = true;
		ComboPooledDataSource source = (ComboPooledDataSource) ObjectFactory.instance().getBean("dataSource");
		String userid = source.getUser();
		for (int m = 0; m < pros.size(); m++) {
			if (pros.get(m) != null) {
				Element elementpros = (Element) (pros.get(m));
				String proname = elementpros.attributeValue("name");
				List params = elementpros.selectNodes("param");
				String sql = "{call " + userid + "." + proname + "(";

				for (int f = 0; f < params.size(); f++) {
					sql += "?,";
				}
				sql += "?,?,?)}";
				try {
					DbOper dao = (DbOper)ObjectFactory.instance().getBean("dao");
					Object[] inParams = new Object[params.size() + 2];
					DataTye[] inType = new DataTye[params.size() + 2];
					int[] inOrder = new int[params.size() + 3]; 
					DataTye[] outType =  new DataTye[1];
					int[] outOrder = new int[1]; 
					
					inParams[params.size()] = filename.substring(2, filename.length() - 4);
					inParams[params.size() + 1] =  check_rtn;
					
					inType[params.size()] = DataTye.VARCHAR;
					inType[params.size() + 1] = DataTye.VARCHAR;
					
					inOrder[params.size()] = params.size() + 1;
					inOrder[params.size() + 1] = params.size() + 2;
					
					outType[0] = DataTye.VARCHAR;
					outOrder[0] = params.size() + 3;

					if (params.size() == 0) {
						Map<Integer, Object> result =
								dao.exeProcedure(sql, inParams, inType, inOrder, outType, outOrder);
						String proflag = (String)result.get(params.size() + 3);
						if (proflag.equals("0")) {
							checkFileSucc &= false;
							logger.info("���ļ�" + file.getName() + "��,ִ�д洢����" + proname + "�쳣!");
						}
						if (proflag.equals("1")) {
							checkFileSucc &= true;
							if(logger.isDebugEnabled()){
								logger.debug("���ļ�" + file.getName() + "��,ִ�д洢����" + proname + "�ɹ�!");
							}
						}
						if (proflag.equals("2")) {
							checkFileSucc &= false;
							logger.info("���ļ�" + file.getName() + "��,ִ�д洢����" + proname + "ʧ��!");
						}
					} 
					else {
						// ������ļ��Ĵ洢���������˲���
						RandomAccessFile br = new RandomAccessFile(file.getPath(), "r");
						String s = "";
						int recordcount = 0;
						int count = -1;
						int x = 0;
						while ((s = br.readLine()) != null) {
							// �����һ�и�ʽ�Ƿ���ȷ���Ƿ���"0x0A"����
							if (count == -1 && s.indexOf("0x0A") > 0) {
								count++;
							}
							String[] str = s.split("0x0A");
							String str_1 = str[0];
							if (str_1.indexOf("RecordCount=") > 0) {
								int w = str_1.indexOf("RecordCount=");
								String str_2 = str_1.substring(w + 12);
								recordcount = Integer.parseInt(str_2);
							} else {
								count++;
							}
						}
						if (count == recordcount) {
							
							if(logger.isDebugEnabled()){
								logger.debug("���ļ�" + file.getName() + "��,�ļ�ͷ��¼����ȷ!");
							}
							br.seek(0);
							while ((s = br.readLine()) != null) {
								if (s.indexOf("RecordCount=") > 0) {
									continue;
								} else {
									x++;
									int colNums = (s.split("0x0A")[0]).split("\\|").length;
									for (int f = 0; f < params.size(); f++) {
										Element elementparam = (Element) (params.get(f));
										String paramvalue = elementparam.getText();

										if ("cols".equals(paramvalue)) {
											inParams[f] =  String.valueOf(colNums);
											inType[f] = DataTye.VARCHAR;
											inOrder[f] = f + 1;
											if(logger.isDebugEnabled()){
												logger.debug("���ò���ֵ��Ϣ:" + (f + 1) + ","+ String.valueOf(colNums));
											}

										}
										if ("rows".equals(paramvalue)) {
											inParams[f] =  String.valueOf(colNums);
											inType[f] = DataTye.VARCHAR;
											inOrder[f] = f + 1;
											if(logger.isDebugEnabled()){
												logger.debug("���ò���ֵ��Ϣ:" + (f + 1) + ","+ String.valueOf(x));
											}
										}
									}
									Map<Integer, Object> result = dao.exeProcedure(sql, inParams, inType, inOrder, outType, outOrder);
									String proflag = (String)result.get(params.size() + 3);// ִ�д洢���̷��صı�־
									if (proflag.equals("0")) {
										checkFileSucc &= false;
										if(logger.isDebugEnabled()){
											logger.debug("���ļ�" + file.getName() + "��,����" + x + "������ִ�д洢����" + proname + "�쳣!");
										}
									}

									if (proflag.equals("1")) {
										// ����һ���ļ����м����д��������һ��û�д�
										checkFileSucc &= true;
										if(logger.isDebugEnabled()){
											logger.debug("���ļ�" + file.getName() + "��,����" + x + "������ִ�д洢����" + proname + "�ɹ�!");
										}
									}

									if (proflag.equals("2")) {
										checkFileSucc &= false;
										if(logger.isDebugEnabled()){
											logger.debug("���ļ�" + file.getName() + "��,����" + x + "������ִ�д洢����" + proname + "ʧ��!");
										}
									}
								}
							}

						} 
						else {
							checkFileSucc &= false;
							if(logger.isDebugEnabled()){
								logger.debug("���ļ�" + file.getName() + "��,�ļ�ͷ��¼����ƥ��,��¼��Ϊ" + recordcount + ",ʵ��Ϊ" + count + "!");
							}
						}
						br.close();
					}
				}
				catch (DaoException e) {
					logger.error("���ô洢����" + proname + "����", e);
				} catch (FileNotFoundException e) {
					logger.error("�ļ�" + file.getName() + "������!", e);
				} catch (IOException e) {
					logger.error("��ȡ�ļ�" + file.getName() + "����", e);
				}
			}
		}
		return checkFileSucc;
	}
	
	static boolean PMFile(File file, String zqgsdm){
		boolean decryptSucc = true;
		if(file.isDirectory()) {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				String filePath = fileList[i].getPath();
				decryptSucc  &= PMFile(new File(filePath), zqgsdm);
				if(!decryptSucc){
					return decryptSucc;
				}
			}
		}
		if(file.isFile()) {
			DataCheckProperty dcp = (DataCheckProperty)ObjectFactory.instance().getBean("dataCheckProperty");
			//����list�ļ�
			String type =file.getName().split("\\.")[1];
			if(!"LIST".equals(type.toUpperCase())) {

				String key =dcp.getDecryptKeyMap().get(zqgsdm);
			
				decryptSucc  &=  decrypt(file,key);
				if(!decryptSucc){
					return decryptSucc;
				}
			}
		}
		return true;
	}

	/**���� 
	  * @param content  ���������� 
	  * @param keyWord ������Կ 
	  */  
	public static boolean decrypt(File content, String keyWord) {
		FileInputStream fis = null;
		BufferedReader br = null;
		FileOutputStream fos = null;

		boolean decrySuccess = true;
		try {
			if((keyWord !=null)&&(!"null".equals(keyWord))) {
				KeyGenerator kgen = KeyGenerator.getInstance("AES");
				SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
				secureRandom.setSeed(keyWord.getBytes());
				kgen.init(128,secureRandom);
				SecretKey secretKey = kgen.generateKey();
				byte[] enCodeFormat = secretKey.getEncoded();
				SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
				Cipher cipher = Cipher.getInstance("AES");// ����������
				cipher.init(Cipher.DECRYPT_MODE, key);// ��ʼ��

				fis = new FileInputStream(content);
				br   = new BufferedReader(new InputStreamReader(fis));
				/*
				 * ���ܺ���ļ���ԭ�ļ���ǰ��"D_"
				 * ��������н���֮����ļ�������δ�����ļ�ͬĿ¼��
				 */				
				String newname = "D_" + content.getName();
				String newpath = content.getAbsolutePath().substring(0, content.getAbsolutePath().length() - content.getName().length()) + newname;				
				fos = new FileOutputStream(newpath);
				String byteContent = "";

				while((byteContent=br.readLine())!=null){
					byte[] result = cipher.doFinal(parseHexStr2Byte(byteContent));
					fos.write(result);
				}
				decrySuccess &=true;
			}
			else{
				decrySuccess &=false;
			}
		} catch (Exception e) {
			if(logger.isDebugEnabled()){
				logger.debug("�ڽ����ļ�ʱ,��ȡ"+content.getName()+"�ļ�����!", e);
			}
			decrySuccess &=false;
		}
		finally {
			try {
				if(fos != null) {
					fos.close();
				}
			} catch (IOException e1) {}
			if(br != null){
				try {
					br.close();
					fis.close();
				} catch (IOException e) {}
			}
	       	 /*
	       	  * ɾ�������ļ��µ�ԭ�����ļ�
	       	  */
			if(decrySuccess){
				content.delete();
			}
		}
		return decrySuccess;
	}
	 
	/**
	 * ���ݺϷ��ļ������ݲ��뱸�����ݿ�
	 * 
	 * @param file
	 */
	public static void insertData(File file, String encoding, int colNums) {

		DbOper dao = (DbOper)ObjectFactory.instance().getBean("dao");
		FileInputStream fis1 = null;
		BufferedReader br1 = null;
		try {
			DataCheckProperty dcp = (DataCheckProperty)ObjectFactory.instance().getBean("dataCheckProperty");
			//��������ÿ�������Ŀ
			int maxNumPerInsert = dcp.getDataNumPerInsert();
			String filename = file.getName();
			String  tablename = filename.split("_")[5];
			String  zqgsdm = filename.split("_")[2];
			/*
			 * �Ƿ�����������ȫ��������ΪZ��ȫ��ΪQ��
			 */
			String dataflag = filename.split("_")[6].substring(0, filename.split("_")[6].length() - 4);
			if(logger.isDebugEnabled()){
				logger.debug("save basic data for:"+tablename + "_" + zqgsdm +" with quantity flag:"+dataflag);
			}
			StringBuffer sql = new StringBuffer(150);
			sql.append("insert into rzrq.sc_").append(tablename).append('_').append(zqgsdm).append('_').append(dataflag).append("  values(");
			for (int j = 0; j < colNums; j++) {
				sql.append('?');
				if (j < colNums - 1) {
					sql.append(',');
				}
			}
			sql.append(')');

			// ��������,ÿ��DATA_NUMS_PER_INSERT��
			fis1 = new FileInputStream(file);
			br1 = new BufferedReader(new InputStreamReader(fis1, encoding));

			List<String[]> paramsList = new ArrayList<String[]>();
			DataTye[] type = new DataTye[colNums];
			for(int typeIndex = 0; typeIndex < colNums; typeIndex++){
				type[typeIndex] = DataTye.VARCHAR;
			}
			int i = 0; // ���ڼ�¼��ȡ������,ÿ�α���DATA_NUMS_PER_INSERT��
			int flag = 0;// ���ڴ洢�ռ�¼��־
			String s = null;
			String[] colVals = null;
			while ((s = br1.readLine()) != null) {
				if (s.indexOf("RecordCount=") > 0) {
					int w = s.indexOf("RecordCount=");
					String str_2 = s.substring(w + 12);
					flag = Integer.parseInt(str_2);
					if (flag == 0) {
						break;
					} else {
						continue;
					}
				} 
				else {
					String[] params = new String[colNums];
					colVals = s.split("\\|");
					int minParamLength = colVals.length >= colNums ? colNums : colVals.length;
					for (int m = 0; m < minParamLength; m++) {
						params[m] = colVals[m];
					}
					paramsList.add(params);
					i++;
				}
				if (i % maxNumPerInsert == 0) {
					dao.batchInsert(sql.toString(), paramsList, type);
					paramsList.clear();
				}
			}
			if ((i % maxNumPerInsert != 0) && (flag != 0)) {
				dao.batchInsert(sql.toString(), paramsList, type);
			}
//			
//			String countSql = "select count(*)  from rzrq.sc_" + tablename + "_" + zqgsdm + "_" + dataflag;
//			List<Map<String, String>> counts = dao.queryList(countSql);
//			Map<String, String> cnt = counts.get(0);
//			System.out.println("table:rzrq.sc_" + tablename + "_" + zqgsdm + "_" + dataflag+", totalnum:"+cnt);
//			int count = 0;
//			for(String key : cnt.keySet()){
//				count = Integer.parseInt(cnt.get(key));
//			}
//			if(count != i){
//				logger.error("======================number wrong!!!");
//			}
		} catch (Exception e) {
			logger.error("���뱸�����ݿ����!", e);
		}finally{
			if (br1 != null) {
				try {
					br1.close();
				} catch (Exception e1) {}
			}
			if(fis1 != null){
				try {
					fis1.close();
				} catch (IOException e) {}
			}
		}
	}

	 /**��16����ת��Ϊ������ 
	  * @param hexStr 
	  * @return  byte[]
	  */  
	 public static byte[] parseHexStr2Byte(String hexStr) {  
		 if (hexStr.length() < 1)  
			 return null;  
		 byte[] result = new byte[hexStr.length()/2];  
		 for (int i = 0;i< hexStr.length()/2; i++) {  
			 int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  
			 int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  
			 result[i] = (byte) (high * 16 + low);  
		 }  
		 return result;  
	 
	 }  
	/**
	 * ���ڵõ��ϱ��Ľӿ������ļ��Ĺ�˾����
	 */
	static String getInterfaceZqgsdm(File file){
		String zqgsdm = null;
		File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			//����list�ļ�
			if(fileList[i].isFile()) {
				String type =fileList[i].getName().split("\\.")[1];
			    	if((!"LIST".equals(type.toUpperCase()))) {
			    		zqgsdm = (fileList[i].getName()).split("_")[1];
			    		return zqgsdm;
			    	}
			}
	  	}
		return zqgsdm;
	}
	
	/**
	 * �Ƿ���ڳ�ͻ�Ĵ洢������ִ��
	 * @param procName
	 * @return
	 */
	public static boolean hasConflictProcRunning(String procName){
		
		return false;
		
	} 
	
	public static boolean init(DataCheckProperty dcp, String zqgsdm, String sjrq, String check_rtn) {

		logger.info("init data");
		int count_pro = 0;// ���ڼ�¼��ⱸ�ÿ����ݴ洢����˳��
		boolean flag = true;// ���ڼ�¼���鱸�����ݿ��Ƿ��д�"1"��ʾ�ɹ���"0"��ʾ���ɹ�

		try {
			/**
			 * ��ŷ��صĽ��
			 */
			List<Future<String>> resultFutureList = new ArrayList<Future<String>>();
			
			DbOper dao = (DbOper)ObjectFactory.instance().getBean("procDao");
			String sql = "SELECT t2.run_orders, t1.check_type,t1.check_detail_type, "
					+ "t3.name,t3.param1,t3.param2,t3.param3,t3.param4,t3.param5,t3.param6,t3.param7,t3.param8,t3.param9,t3.param10,t3.param11,t3.param12,t3.param13"
					+ " from T_KRMS_CHECK_DICT t1,T_KRMS_CHECK_EXECUTE t2,T_KRMS_CHECK_DETAIL_CONFIG t3 "
					+ "WHERE t1.CHECK_ID = t2.CHECK_ID and t2.NAME = t3.NAME and t1.CHECK_ID!=90000 order by t1.check_type,t1.check_detail_type,t2.run_orders";
			List<Map<String, String>> result = dao.queryProcList(sql);

			if(logger.isDebugEnabled()){
				logger.debug("***********��ʼ��ⱸ�ÿ�����***********");
			}

			StringBuffer sSql = new StringBuffer(120);
			// ���鱸�����ݿ�����
			int i = 0;
			String pro_flag = null;
			String CurrentRunOrder = null;
			for (; i < result.size(); i++) {
				sSql.delete(0, 120);
				if(logger.isDebugEnabled()){
					logger.debug("***************" + (++count_pro) + "****************");
				}
				Map<String, String> pro = result.get(i);
				String runOrder = pro.get(RUN_ORDER) + pro.get(CHECK_TYPE) + pro.get(CHECK_DETAIL_TYPE);
				pro.remove(RUN_ORDER);
				pro.remove(CHECK_TYPE);
				pro.remove(CHECK_DETAIL_TYPE);
				
				if(logger.isDebugEnabled()){
					logger.debug("�洢�������ƣ�" + pro.get("name") + ";������" + pro.get("param1"));
				}
				sSql.append("{call  krcs.").append(pro.get("name")).append('(');
				for (int j = 0; j < pro.size() + 4; j++) {
					sSql.append('?');
					if (j < pro.size() + 3) {
						sSql.append(',');
					}
				}
				sSql.append(")}");

				// �����ж��Ƿ���Ҫ������������Ĵӱ��ÿ⵽���Ŀ�Ĵ洢����
				if (("PROC_CHECK_COLS_DATE".equals((String) pro.get("name")))  
						|| ("PROC_CHECK_PRIMARY_KEY".equals((String) pro.get("name")))   ) {
					Callable<String> initRunner = new DataInitRunner(zqgsdm, sjrq, check_rtn, sSql.toString(), pro, i);
					pro_flag = initRunner.call();
					if(!("1".equals(pro_flag))){
						logger.info("data check failed at proc "+ pro.get("name")+"��has executed:"+(i));
						flag = false;
						break;
					}
				}
				else if(("PROC_CHECK_INSERT_BEFORE".equals((String) pro.get("name")))){
					if(resultFutureList.size() > 0){
						for(Future<String> future : resultFutureList){
							while(!future.isDone()){
								Thread.sleep(0);//���·���CPU����
							}
						}
					}
					logger.info("finally start");
					Callable<String> initRunner = new DataInitRunner(zqgsdm, sjrq, check_rtn, sSql.toString(), pro, i);
					pro_flag = initRunner.call();
					if(!("1".equals(pro_flag))){
						logger.info(zqgsdm + " data check failed at proc "+ pro.get("name")+"��has executed:"+(i));
						flag = false;
						break;
					}
				}
				else{
					//1498��֮ǰ�Ŀ��Է��鲢�������Ǻ���ļ����洢����Ҫ���У���ֹ�޸���������ͻ
					if(i < 1498){
						if(CurrentRunOrder == null){
						}
						else{
							if(runOrder.equals(CurrentRunOrder)){
							}else{
								if(resultFutureList.size() > 0){
									int len = resultFutureList.size();
									for(int count = len  - 1; count > -1; count--){
										Future<String> future  = resultFutureList.get(count);
										while(!future.isDone()){
											Thread.sleep(0);
										}
										resultFutureList.remove(count);
									}
								}
							}
						}
						Callable<String> initRunner = new DataInitRunner(zqgsdm, sjrq, check_rtn, sSql.toString(), pro, i);
						Future<String> resultFuture = ObjectFactory.procServicePool.submit(initRunner) ;
						resultFutureList.add(resultFuture);
						CurrentRunOrder = runOrder;
					}else{
						if(resultFutureList.size() > 0){
							for(Future<String> future : resultFutureList){
								while(!future.isDone()){
									Thread.sleep(0);
								}
							}
						}
						Callable<String> initRunner = new DataInitRunner(zqgsdm, sjrq, check_rtn, sSql.toString(), pro, i);
						initRunner.call();
					}
				}
			}
			logger.info(zqgsdm+": total procedure:"+result.size()+", acutal execute:"+(i)+", result:"+flag);
			// �������󱨸��ļ�
			createResultFile(dcp, zqgsdm, sjrq, check_rtn, flag);
		} catch (Exception e) {
			flag = false;
			logger.error("error", e);
		}
		return flag;
	}
	
	/**
     * ���ڼ��ȯ�̱��͹�������ز��Ϸ��ļ����Ա�ȯ����Ϊ���Ϸ��ļ����·������������ļ��У���7z�ļ��ȣ�
     * �����Ҫ�����ļ����µ��ļ�Ϊ�գ��������±��͡�
     */
    public static String checkRubbishFileAndMove(File folderFile,String rubbishFileDir) {
        File[] tempList = folderFile.listFiles();
        String errMsg = new String();
        for (int i = 0; i < tempList.length; i++) {

            String encoding = System.getProperty("file.encoding");
            String filename=null;
            try {
                //��ϵͳ����encodingת��Ϊutf-8����
                filename=new String(tempList[i].getName().getBytes(encoding),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (tempList[i].isDirectory()) {
                errMsg += "�����ļ��У�" +  filename+ "���Ƿ�����Ӧ�����ļ���;";

                moveFileToRubbishDir(rubbishFileDir, tempList[i]);

            } else {

                Matcher matcher = namePatternTmp.matcher(filename.toUpperCase());
                if (!matcher.find()) {
                    try {
                        errMsg += "�����ļ�" + filename + "�Ƿ�;";

                        moveFileToRubbishDir(rubbishFileDir, tempList[i]);

                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            }
        }
        if(!errMsg.isEmpty()){
            errMsg+="��ȷ�ı����ļ�Ϊ7zѹ���ļ����ļ�����ʽΪ SC_\\w{8}_\\d{8}_\\d{3}_(Y|N)\\w\\.7Z";

        }

        return errMsg;

}

    private static void moveFileToRubbishDir(String backupFileDir, File file) {

        CreateFile(backupFileDir);
        Path source = Paths.get(file.getAbsolutePath());
        Path destination = Paths.get(backupFileDir+ File.separator + file.getName());
        try {
            Files.move(source, destination, ATOMIC_MOVE);
        } catch (Exception e) {

            logger.error("The rubbish file can't be moved!", e);
        }
    }
    
	public static void Move7zFile(File file, String dateDir) {

		String filename = file.getName().toLowerCase();
		
		if (file.isDirectory()){
			return;
		}
		
		int p = filename.indexOf(".7z");
		if (p == -1) {
			return;
		} else if (filename.indexOf(".szt!") != -1) {
			return;
		} else {
			filename = filename.substring(0, p);
			long time = System.currentTimeMillis();
			filename = filename + "_" + Long.toString(time) + ".7z";
		}
				
		//CreateFile(dateDir);
		
		Path source = Paths.get(file.getAbsolutePath());		
		Path destination = Paths.get(dateDir+ File.separator +filename);
		try {
			Files.move(source, destination, ATOMIC_MOVE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("The 7z file can't be moved!", e);			
		}
	}
	
	public static void convertDName(File file) {
		if(file.isDirectory()) {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				convertDName(fileList[i]);
			}
		}
		
		if(file.isFile()) {
			String type =file.getName().split("\\.")[1];
			if(!"LIST".equals(type.toUpperCase())) {
				String newname = "D_" + file.getName();
				String newpath = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - file.getName().length()) + newname;
				file.renameTo(new File(newpath));
			}
		}
	}
	
	static boolean PMFile_sm4(File file, String zqgsdm, String root){
		boolean decryptSucc = true;
		if(file.isDirectory()) {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				String filePath = fileList[i].getPath();
				decryptSucc  &= PMFile_sm4(new File(filePath), zqgsdm, root);
				if(!decryptSucc){
					return decryptSucc;
				}
			}
		}
		if(file.isFile()) {
			DataCheckProperty dcp = (DataCheckProperty)ObjectFactory.instance().getBean("dataCheckProperty");
			//����list�ļ�
			String type =file.getName().split("\\.")[1];
			if(!"LIST".equals(type.toUpperCase())) {

				String key =dcp.getDecryptKeyMap().get(zqgsdm);
			
				decryptSucc  &=  decrypt_sm4(file,key,root);
				if(!decryptSucc){
					return decryptSucc;
				}
			}
		}
		return true;
	}

	/**���� 
	  * @param content  ���������� 
	  * @param keyWord ������Կ 
	  */  
	public static boolean decrypt_sm4(File content, String keyWord, String root) {
		FileInputStream fis = null;
		BufferedReader br = null;
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		
		SM4Utils sm4 = new SM4Utils();
		sm4.setHexString(false);
		sm4.setSecretKey(keyWord);

		boolean decrySuccess = true;
		try {
			if((keyWord !=null)&&(!"null".equals(keyWord))) {
				fis = new FileInputStream(content);
				br   = new BufferedReader(new InputStreamReader(fis));
				/*
				 * ���ܺ���ļ���ԭ�ļ���ǰ��"D_"
				 * ��������н���֮����ļ�������δ�����ļ�ͬĿ¼��
				 */				
				String newname = "D_" + content.getName();
				String newpath = root + File.separator + newname;
				fos = new FileOutputStream(newpath);
				bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
				String plainText = null; 
				String cipherText = null;

				while((cipherText=br.readLine())!=null){
					plainText = sm4.decryptData_ECB(cipherText);
					if(plainText == null) {
						decrySuccess &= false;
						break;
					}
					bw.write(plainText + "\n");
				}
				decrySuccess &=true;
			}
			else{
				decrySuccess &=false;
			}
		} catch (Exception e) {
			if(logger.isDebugEnabled()){
				logger.debug("�ڽ����ļ�ʱ,��ȡ"+content.getName()+"�ļ�����!", e);
			}
			decrySuccess &=false;
		}
		finally {
			try {
				br.close();
				bw.close();
				fos.close();
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	 				
	       	 /*
	       	  * ɾ�������ļ��µ�ԭ�����ļ�
	       	  */
			if(decrySuccess){
				content.delete();
			}
		}
		return decrySuccess;
	}
}
