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
//			// 当检测文件的存储过程配置了参数,执行下面if下的代码
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
			/* 实际表的列数,在xml/FileOrder.xml中配置的 */
			int cols_table = DataCheckRunner.colNumMap.get(filename.split("_")[4]);
			FileUtils.insertData(file, encoding, cols_table);
		}
	}
	
	static void saveListFilesToKrcs(DataCheckProperty dcp,File[] fileList){
		for (int i = 0; i < fileList.length; i++) {

			String filename = fileList[i].getName();
			//只入库在list清单中的文件
			String encoding =dcp.getFileEncoding();
			/* 实际表的列数,在xml/FileOrder.xml中配置的 */
			int cols_table = DataCheckRunner.colNumMap.get(filename.split("_")[5]);
			FileUtils.insertData(fileList[i], encoding, cols_table);
		}
	}
	
	
	
	/**
	 * 分类存储数据文件，并且将成功的数据存入数据库
	 * @param file
	 * @param zqgsdm
	 * @param sjrq
	 * @param fileCheckSucc
	 */
	static  void classifySubmissionFiles(DataCheckProperty dcp, File file, String zqgsdm, String sjrq, boolean fileCheckSucc) {
		
		String filename = file.getName();
		/**
		 * 设置备份的目标路径
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
				logger.error("在备份文件时,读取" + file.getName() + "文件的编号出错!", e);
			} finally {
				file.delete();
			}
		}
	}
	
	
	/**
	 * 分类存储数据文件，并且将成功的数据存入数据库
	 * @param file
	 * @param zqgsdm
	 * @param fileLists   list清单中的文件名
	 * @param sjrq
	 */
	static  void classifySubmissionListFiles(DataCheckProperty dcp, File[] fileList, String zqgsdm, String sjrq) {		
		/**
		 * 设置备份的目标路径
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
				else logger.error("在备份文件时,源路径下的 " +fileList[i].getPath() +"不存在");
				
						
			}  catch (NumberFormatException e) {
				logger.error("在备份文件时,读取" + fileList[i].getName() + "文件的编号出错!", e);
			} finally {
//				srcFile.delete();
			}				
		}
	}
	
	/**
	 * 用于检验*.list文件名的数据日期标志和*.list里面记录的文件名的数据日期标志是否一致
	 * @param listfile  指向list文件5
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
	 * 返回 *.list 中的文件名
	 * @param listfile  指向list文件5
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
	 * 判读该数据日期是否为周五
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
	 * 用于检验*.list文件中的文件是否都是全量
	 * @param listfile  指向list文件
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
	 * 用于计算.list文件里面的记录条数，即文件名个数
	 */
	 public static int count(File listfile) {
		 int j = 0; //记录文件个数
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
	 * 用于限制券商报送数据的时间，以便别的程序可以有时间执行
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
				File notice = new File(reportfile_path+"/"+receivefile_name+"/温馨提示.txt");
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
							if(logger.isDebugEnabled()){
								logger.debug("在生成OK文件时，创建文件失败!",  e);
							}
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
						logger.error("在生成OK文件时，创建文件失败!", e);
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
	 * 用于清空券商报送过来的相关不合法文件，以便券商因为不合法文件导致反馈有误差，（如文件夹，非7z和list文件）
	 * 设计上要求报送文件夹下的文件为空，才能重新报送。
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
     * 用于清空券商报送过来的相关不合法文件，以便券商因为不合法文件导致反馈有误差，（如文件夹，非7z和list文件）
     * 设计上要求报送文件夹下的文件为空，才能重新报送。
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
			logger.debug("***********开始删除"+zqgsdm+"备用库数据***********");
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
			logger.error("删除备用库存储过程出错！", e);
		}
		return false;
	}
	
	/**
	 *
	 * 用于清空券商报送过来的相关文件，以便券商可以继续报送，
	 * 设计上要求报送文件夹下的文件为空，才能重新报送。
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
	 * 检验文件是否传输完毕
	 * @param file  指向券商报送过来的文件存放的文件夹
	 */
	public static boolean check_Transmission(File file,int interfacecount){
		try{
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				//检验fdep平台标志，未传送完成的文件会有.szt!
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
    * 使用文件通道的方式复制文件
    * 
    * @param s
    *            源文件
    * @param d
    *            复制到的新文件
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
	 * 用于备份券商报送过来的原始文件
	 * 将文件File拷贝至backupEncryptFileDir
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
	 * 用于生成与券商的交互文件，该方法只生成两个情况下的交互文件 第一种：券商所有文件都通过所有校验，生成成功文件。
	 * 第二种：券商在数据到了备用数据库，由于不满足数据的校验规则，生成错误的excel文件
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
			logger.info("***********开始生成报告文件***********");
			/*
			 * 以前用系统当前时间为交互文件命名
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
					String s = zqgsdm + "  " + sjrq + "上报数据已经通过检验成功。";
					bw.write(s + "\n");
				}catch(Exception e){
					logger.error("", e);
				}finally{
					if(bw != null){ try { bw.close(); } catch (IOException e) { } }
					if(fos != null){ try { fos.close(); } catch (IOException e) { } }
				}
				try {
					// 处理交互日志信息
					String sqlsuccess = dcp.getJhjgSql();
					Object[] params = {sjrq, zqgsdm, sdf.format(report_file_date),  Long.parseLong(milltime), "01", "01", "01"};
					DataTye[] type = {DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.LONG, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR};				
					dao.insert(sqlsuccess, params, type);
				} catch (DaoException e) {
					logger.error("处理交互日志信息异常", e);
				}
			} 
			else {
				WritableWorkbook book = null;
				try{
					// excel页数
					int excel_count = 0;
					book = Workbook.createWorkbook(report_file);
					// 生成名为"错误报告"的工作表，参数0表示这是第一页
					WritableSheet sheet = book.createSheet("错误报告", 0);
					WritableFont font = new WritableFont(WritableFont.TAHOMA, 9, WritableFont.BOLD);// 定义字体
					font.setColour(Colour.BLACK);// 字体颜色
					WritableCellFormat wc = new WritableCellFormat(font);
					wc.setAlignment(Alignment.CENTRE); // 设置居中
					wc.setBorder(Border.ALL, BorderLineStyle.THIN); // 设置边框线
					
					int rows = 1;
					Label lable = null;
					
					// 设置表头
					lable = new Label(0, 0, "检验名称", wc);
					sheet.addCell(lable);
					
					lable = new Label(1, 0, "描述", wc);
					sheet.addCell(lable);
					
					if(result != null && result.size() > 0){
						for(Map<String, String> row : result){
							int index = 0;
							for (Entry<String, String> entry : row.entrySet()) {
								if (entry!= null) {
									sheet.setColumnView(index, entry.getValue().length() * 2);
									lable = new Label(index++, rows % 50000, entry.getValue(), wc);
								}
								// 将定义好的单元格添加到工作表中
								sheet.addCell(lable);
							}
							if (++rows % 50000 == 0) {
								excel_count++;
								sheet = book.createSheet("错误报告" + excel_count, excel_count);
							}
						}
					}
					book.write();
				}catch(Exception e){
					logger.error("创建结果文件异常", e);
				}
				finally{
					if(book != null){
						try {book.close(); } catch (Exception e) { } 
					}
				}	
				try {
					// 处理交互日志信息
					String sqlfailure =dcp.getJhjgSql();
					Object[] params = {sjrq, zqgsdm, sdf.format(report_file_date),  Long.parseLong(milltime), "02", "01", "01"};
					DataTye[] type = {DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.LONG, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR};				
					dao.insert(sqlfailure, params, type);
				} catch (Exception e) {
					logger.error("处理交互日志信息异常", e);
				}
			}
			// 生成检验错误报告在fdep平台上的ok标志
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
			logger.error("获取处理结果异常，无法生成结果文件", e);
		}
	}
	// 插人两张表，一个是明细表（彪哥建的），一个是交互校验表。
	public static void createResultFile(DataCheckProperty dcp, String zqgsdm, String sjrq, String milltime, String information, boolean flag) {

//		DataCheckProperty dcp = (DataCheckProperty) ObjectFactory.instance().getBean("dataCheckProperty");

		String reportfile = dcp.getReportFileDir();
		CreateFile(reportfile + File.separator + zqgsdm);
		Date report_file_date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		File report_file = null;
		try {

			if(logger.isDebugEnabled()){
				logger.debug("***********开始生成报告文件***********");
			}
			/*
			 * 以前用系统当前时间为交互文件命名
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
					String s = zqgsdm + "  " + sjrq + "上报数据已经通过检验成功。";
					bw.write(s + "\n");
				}catch(Exception e){
					logger.error("", e);
				}finally{
					if(fos != null){ fos.close(); }
					if(bw != null){ bw.close(); }
				}
			} else {
				WritableWorkbook book = Workbook.createWorkbook(report_file);
				// 生成名为"错误报告"的工作表，参数0表示这是第一页
				WritableSheet sheet = book.createSheet("错误报告", 0);
				WritableFont font = new WritableFont(WritableFont.TAHOMA, 9, WritableFont.BOLD);// 定义字体
				font.setColour(jxl.format.Colour.BLACK);
				// 字体颜色
				WritableCellFormat wc = new WritableCellFormat(font);
				wc.setAlignment(jxl.format.Alignment.CENTRE); // 设置居中
				wc.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN); // 设置边框线
				// 设置单元格的背景颜色
				Label lable = null;
				// 设置表头
				lable = new Label(0, 0, "检验名称", wc);
				sheet.addCell(lable);
				lable = new Label(1, 0, "描述", wc);
				sheet.addCell(lable);
				// 编写错误信息
				lable = new Label(0, 1, "000000", wc);
				sheet.addCell(lable);

				sheet.setColumnView(1, information.length() * 2);
				lable = new Label(1, 1, information, wc);
				sheet.addCell(lable);
				book.write();
				book.close();
			}

			try{
				// 处理交互日志信息
				DbOper dao = (DbOper)ObjectFactory.instance().getBean("dao");
				String sql1 = dcp.getJhjgSql();//"insert into rzrq.T_SJZX_RZRQ_JKSJ_JHJG(SJRQ,ZQGSDM,JYRQ,CHECK_RTN,JYJG,TZBZ,sfsb) values(?,?,?,?,?,?,?)";
				Object[] params1 = {sjrq, zqgsdm,  sdf.format(report_file_date), Long.parseLong(milltime), "02",
						"01", "01" };
				DataTye[] type1 = {DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.LONG,
						DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR};
				dao.insert(sql1, params1, type1);
	
				// 插入相关检验信息到明细表
				String sql2 = "insert into krcs.t_krms_check_jksj_jksbcwxx(SJRQ,ZQGSDM,JCRQ,CHECK_RTNX,CWXX) "
						+ "values(?,?,?,?,?)";
				
				Object[] params2 = {sjrq, zqgsdm, sdf.format(report_file_date), Long.parseLong(milltime), information};
				DataTye[] type2 = {DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR,  DataTye.LONG, DataTye.VARCHAR,};
				dao.insert(sql2, params2, type2);
			} catch (Exception e) {
				logger.error("插入校验信息异常", e);
			}
			// 生成检验错误报告在fdep平台上的ok标志
			if (report_file != null) {
				File okfile = new File(report_file.getPath() + ".ok");
				try {
					FileOutputStream fis = new FileOutputStream(okfile);
					fis.close();
				} catch (IOException e) {
					logger.error("在生成OK文件时，创建文件失败!", e);
				}
			}
		} catch (Exception e) {
			logger.error("创建结果文件失败", e);
		}
	}
	
	
	/**
	 * 用于创建文件夹，如果不存在，则创建该文件夹
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
	 * 用于查找*.list文件,注意：券商只能传一个list文件
	 * @param file      指向当前检验的券商文件夹
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
	 * 用于查找*.list文件,注意：券商只能传一个list文件
	 * @param file      指向当前检验的券商文件夹
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
	 * 用于得到上报的接口数据文件的准确日期
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
	 *用于检验*.list里面记录的文件名里面的类型是否和报送过来的类型一致，并且删除*.list文件
	 * 例如：报送的是*.TXT，而list文件里面是否也是*.TXT。
	 * @param listfile  指向list文件
	 * @param file      指向当前检验的券商文件夹
	 * 注意：*.TXT 和*.txt同样被视为文件名不一致
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
				 * 给后面备用库数据检查参数赋值用，zqgsdm 为该线程检验的券商的公司代码， 
				 * sjrq为该券商所报数据的数据日期 
				 * 注：该两个参数以报送文件上的信息为准
				 */
				Node node1 = DataCheckRunner.orderConfig.selectSingleNode("//order/type[@id='file']");

				if (node1 == null) {
					if(logger.isDebugEnabled()){
						logger.debug("配置文件没有配置文件检测,请检查配置文件是否需要检测!");
					}
					return false;
				} else {
					List nodes = node1.selectNodes("dict");
					if (nodes.size() == 0) {
						if(logger.isDebugEnabled()){
							logger.debug("文件检测中,没有配置步骤节点!");
						}
						return false;
					} else {
						boolean flag = true;
						String filename = fileList[i].getName();
						if(logger.isDebugEnabled()){
							logger.debug("文件名：" + filename);
						}

						int j = 0;// 用于记录检测成功了的步骤数
						for (; j < nodes.size(); j++) {
							if (nodes.get(j) != null) {
								Element element = (Element) nodes.get(j);

								/* 调存储过程的检验,没有参数 */
								List pros = element.selectNodes("pro");

								/* 特殊处理，只用于检验文件记录列数是否正确 */
								List params = element.selectNodes("params");

								if (params.size() != 0) {
									if(logger.isDebugEnabled()){
										logger.debug("正在检测第" + element.attributeValue("order") + "步骤,请稍后!");
									}
									flag &= CheckTxtHeadNRecordCount(fileList[i], params, check_rtn);
									if (!flag) {
										if(logger.isDebugEnabled()){
											logger.debug("检测第" + element.attributeValue("order") + "步骤失败!");
										}
										break;
									}
									if (flag) {
										if(logger.isDebugEnabled()){
											logger.debug("检测第" + element.attributeValue("order") + "步骤成功!");
										}
									}
								} else {
									if (pros.size() == 0) {
										if(logger.isDebugEnabled()){
											logger.debug("文件检测中,第" + element.attributeValue("order") + "步骤下存储过程节点没有配置,请检测配置文件是否需要配置!");
										}
									} else {
										if(logger.isDebugEnabled()){
											logger.debug("正在检测第" + element.attributeValue("order") + "步骤,请稍后!");
										}
										flag &= CheckData(fileList[i], pros, check_rtn);
										if (!flag) {
											if(logger.isDebugEnabled()){
												logger.debug("检测第" + element.attributeValue("order") + "步骤失败!");
											}
											break;
										}
										if (flag) {
											if(logger.isDebugEnabled()){
												logger.debug("检测第" + element.attributeValue("order") + "步骤成功!");
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
	 * 检验txt文件头和数据数量是否一致
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
			// 当检测文件的存储过程配置了参数,执行下面if下的代码
			br = new RandomAccessFile(file.getPath(), "r");
			lnr = new LineNumberReader(new FileReader(file));;
			String s = "";
			int recordcount = 0;// 文件头记录的记录数
			int count = -1;// 实际记录数
			boolean fileheadFormatRight = false;// 记录该文件的文件头是否正确标志
			
			// 检验第一行格式是否正确
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
			/* 检验文件记录列数 */
			if (count == recordcount) {
				if(logger.isDebugEnabled()){
					logger.debug("在文件" + file.getName() + "中,文件头和记录数正确!");
				}
				return true;
			} else {
				if (fileheadFormatRight) {
					// 这里有漏洞，要是是文件头记录数和文件里面的真实记录不匹配，虽然检验程序没有问题
					// 但是没有记录错误信息，交互文件里面就为空，所以最后人为插一条记录到检验结果表。
					if(logger.isDebugEnabled()){
						logger.debug("在文件" + file.getName() + "中,文件头记录数不匹配,记录数为" + recordcount + ",实际为" + count + "!");
					}

					String sql = "insert into t_krms_check_result(zqgsdm,check_rtn,check_id,check_son_id,check_son_name,check_rs,check_rs_mou,back_value,check_date,data_code,sjrq) values (?,?,?,?,?,?,?,?,?,?,?)";

					Object[] params = {filename.split("_")[2], check_rtn, 99999, "02", recordcount + "|" + count + "|" + check_rtn, 
							"02", "在文件" + filename + "中,文件头记录数不匹配,记录数为" + recordcount + ",实际为" + count + "!",
							"2", new SimpleDateFormat("yyyyMMdd").format(new Date()), filename.split("_")[5], filename.split("_")[3]};
					DataTye[] types = {DataTye.VARCHAR, DataTye.VARCHAR, DataTye.NUMBER, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR,
							DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR,};
					
					DbOper dao = (DbOper)ObjectFactory.instance().getBean("dao");
					dao.insert(sql, params, types);
					return false;
				} else {
					// 这里有漏洞，要是是文件头记录头有问题，虽然检验程序没有问题
					// 但是没有记录错误信息，交互文件里面就为空，所以最后人为插一条记录到检验结果表。
					logger.error("在文件" + filename + "中,文件头格式不正确!");

					String sql = "insert into t_krms_check_result(zqgsdm,check_rtn,check_id,check_son_id,check_son_name,check_rs,check_rs_mou,back_value,check_date,data_code,sjrq) values (?,?,?,?,?,?,?,?,?,?,?)";

					Object[] params = {filename.split("_")[2], check_rtn, 99999, "02", recordcount + "|" + count + "|" + check_rtn, 
							"02", "在文件" + filename + "中,文件头格式不正确!",
							"2", new SimpleDateFormat("yyyyMMdd").format(new Date()), filename.split("_")[5], filename.split("_")[3]};
					DataTye[] types = {DataTye.VARCHAR, DataTye.VARCHAR, DataTye.NUMBER, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR,
							DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR, DataTye.VARCHAR,};
					
					DbOper dao = (DbOper)ObjectFactory.instance().getBean("dao");
					dao.insert(sql, params, types);
					return false;
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("文件" + file.getName() + "不存在!", e);
			return false;
		} catch (IOException e) {
			logger.error("读取文件" + file.getName() + "出错", e);
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
							logger.info("在文件" + file.getName() + "中,执行存储过程" + proname + "异常!");
						}
						if (proflag.equals("1")) {
							checkFileSucc &= true;
							if(logger.isDebugEnabled()){
								logger.debug("在文件" + file.getName() + "中,执行存储过程" + proname + "成功!");
							}
						}
						if (proflag.equals("2")) {
							checkFileSucc &= false;
							logger.info("在文件" + file.getName() + "中,执行存储过程" + proname + "失败!");
						}
					} 
					else {
						// 当检测文件的存储过程配置了参数
						RandomAccessFile br = new RandomAccessFile(file.getPath(), "r");
						String s = "";
						int recordcount = 0;
						int count = -1;
						int x = 0;
						while ((s = br.readLine()) != null) {
							// 检验第一行格式是否正确，是否以"0x0A"结束
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
								logger.debug("在文件" + file.getName() + "中,文件头记录数正确!");
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
												logger.debug("配置参数值信息:" + (f + 1) + ","+ String.valueOf(colNums));
											}

										}
										if ("rows".equals(paramvalue)) {
											inParams[f] =  String.valueOf(colNums);
											inType[f] = DataTye.VARCHAR;
											inOrder[f] = f + 1;
											if(logger.isDebugEnabled()){
												logger.debug("配置参数值信息:" + (f + 1) + ","+ String.valueOf(x));
											}
										}
									}
									Map<Integer, Object> result = dao.exeProcedure(sql, inParams, inType, inOrder, outType, outOrder);
									String proflag = (String)result.get(params.size() + 3);// 执行存储过程返回的标志
									if (proflag.equals("0")) {
										checkFileSucc &= false;
										if(logger.isDebugEnabled()){
											logger.debug("在文件" + file.getName() + "中,检测第" + x + "行数据执行存储过程" + proname + "异常!");
										}
									}

									if (proflag.equals("1")) {
										// 避免一个文件中有几行有错，但是最后一行没有错
										checkFileSucc &= true;
										if(logger.isDebugEnabled()){
											logger.debug("在文件" + file.getName() + "中,检测第" + x + "行数据执行存储过程" + proname + "成功!");
										}
									}

									if (proflag.equals("2")) {
										checkFileSucc &= false;
										if(logger.isDebugEnabled()){
											logger.debug("在文件" + file.getName() + "中,检测第" + x + "行数据执行存储过程" + proname + "失败!");
										}
									}
								}
							}

						} 
						else {
							checkFileSucc &= false;
							if(logger.isDebugEnabled()){
								logger.debug("在文件" + file.getName() + "中,文件头记录数不匹配,记录数为" + recordcount + ",实际为" + count + "!");
							}
						}
						br.close();
					}
				}
				catch (DaoException e) {
					logger.error("调用存储过程" + proname + "出错", e);
				} catch (FileNotFoundException e) {
					logger.error("文件" + file.getName() + "不存在!", e);
				} catch (IOException e) {
					logger.error("读取文件" + file.getName() + "出错", e);
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
			//过滤list文件
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

	/**解密 
	  * @param content  待解密内容 
	  * @param keyWord 解密密钥 
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
				Cipher cipher = Cipher.getInstance("AES");// 创建密码器
				cipher.init(Cipher.DECRYPT_MODE, key);// 初始化

				fis = new FileInputStream(content);
				br   = new BufferedReader(new InputStreamReader(fis));
				/*
				 * 解密后的文件在原文件名前加"D_"
				 * 而且设计中解密之后的文件生成与未解密文件同目录。
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
				logger.debug("在解密文件时,读取"+content.getName()+"文件出错!", e);
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
	       	  * 删除报送文件下的原加密文件
	       	  */
			if(decrySuccess){
				content.delete();
			}
		}
		return decrySuccess;
	}
	 
	/**
	 * 根据合法文件将数据插入备用数据库
	 * 
	 * @param file
	 */
	public static void insertData(File file, String encoding, int colNums) {

		DbOper dao = (DbOper)ObjectFactory.instance().getBean("dao");
		FileInputStream fis1 = null;
		BufferedReader br1 = null;
		try {
			DataCheckProperty dcp = (DataCheckProperty)ObjectFactory.instance().getBean("dataCheckProperty");
			//批量插入每次最大数目
			int maxNumPerInsert = dcp.getDataNumPerInsert();
			String filename = file.getName();
			String  tablename = filename.split("_")[5];
			String  zqgsdm = filename.split("_")[2];
			/*
			 * 是否是增量还是全量，增量为Z，全量为Q。
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

			// 插入数据,每次DATA_NUMS_PER_INSERT条
			fis1 = new FileInputStream(file);
			br1 = new BufferedReader(new InputStreamReader(fis1, encoding));

			List<String[]> paramsList = new ArrayList<String[]>();
			DataTye[] type = new DataTye[colNums];
			for(int typeIndex = 0; typeIndex < colNums; typeIndex++){
				type[typeIndex] = DataTye.VARCHAR;
			}
			int i = 0; // 用于记录读取的条数,每次保存DATA_NUMS_PER_INSERT条
			int flag = 0;// 用于存储空记录标志
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
			logger.error("插入备用数据库出错!", e);
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

	 /**将16进制转换为二进制 
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
	 * 用于得到上报的接口数据文件的公司代码
	 */
	static String getInterfaceZqgsdm(File file){
		String zqgsdm = null;
		File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			//过滤list文件
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
	 * 是否存在冲突的存储过程在执行
	 * @param procName
	 * @return
	 */
	public static boolean hasConflictProcRunning(String procName){
		
		return false;
		
	} 
	
	public static boolean init(DataCheckProperty dcp, String zqgsdm, String sjrq, String check_rtn) {

		logger.info("init data");
		int count_pro = 0;// 用于记录检测备用库数据存储过程顺序
		boolean flag = true;// 用于记录检验备用数据库是否有错，"1"表示成功，"0"表示不成功

		try {
			/**
			 * 存放返回的结果
			 */
			List<Future<String>> resultFutureList = new ArrayList<Future<String>>();
			
			DbOper dao = (DbOper)ObjectFactory.instance().getBean("procDao");
			String sql = "SELECT t2.run_orders, t1.check_type,t1.check_detail_type, "
					+ "t3.name,t3.param1,t3.param2,t3.param3,t3.param4,t3.param5,t3.param6,t3.param7,t3.param8,t3.param9,t3.param10,t3.param11,t3.param12,t3.param13"
					+ " from T_KRMS_CHECK_DICT t1,T_KRMS_CHECK_EXECUTE t2,T_KRMS_CHECK_DETAIL_CONFIG t3 "
					+ "WHERE t1.CHECK_ID = t2.CHECK_ID and t2.NAME = t3.NAME and t1.CHECK_ID!=90000 order by t1.check_type,t1.check_detail_type,t2.run_orders";
			List<Map<String, String>> result = dao.queryProcList(sql);

			if(logger.isDebugEnabled()){
				logger.debug("***********开始检测备用库数据***********");
			}

			StringBuffer sSql = new StringBuffer(120);
			// 检验备用数据库数据
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
					logger.debug("存储过程名称：" + pro.get("name") + ";表名：" + pro.get("param1"));
				}
				sSql.append("{call  krcs.").append(pro.get("name")).append('(');
				for (int j = 0; j < pro.size() + 4; j++) {
					sSql.append('?');
					if (j < pro.size() + 3) {
						sSql.append(',');
					}
				}
				sSql.append(")}");

				// 用于判断是否需要继续处理下面的从备用库到中心库的存储过程
				if (("PROC_CHECK_COLS_DATE".equals((String) pro.get("name")))  
						|| ("PROC_CHECK_PRIMARY_KEY".equals((String) pro.get("name")))   ) {
					Callable<String> initRunner = new DataInitRunner(zqgsdm, sjrq, check_rtn, sSql.toString(), pro, i);
					pro_flag = initRunner.call();
					if(!("1".equals(pro_flag))){
						logger.info("data check failed at proc "+ pro.get("name")+"，has executed:"+(i));
						flag = false;
						break;
					}
				}
				else if(("PROC_CHECK_INSERT_BEFORE".equals((String) pro.get("name")))){
					if(resultFutureList.size() > 0){
						for(Future<String> future : resultFutureList){
							while(!future.isDone()){
								Thread.sleep(0);//重新发起CPU竞争
							}
						}
					}
					logger.info("finally start");
					Callable<String> initRunner = new DataInitRunner(zqgsdm, sjrq, check_rtn, sSql.toString(), pro, i);
					pro_flag = initRunner.call();
					if(!("1".equals(pro_flag))){
						logger.info(zqgsdm + " data check failed at proc "+ pro.get("name")+"，has executed:"+(i));
						flag = false;
						break;
					}
				}
				else{
					//1498个之前的可以分组并发，但是后面的几个存储过程要串行，防止修改事务发生冲突
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
			// 创建错误报告文件
			createResultFile(dcp, zqgsdm, sjrq, check_rtn, flag);
		} catch (Exception e) {
			flag = false;
			logger.error("error", e);
		}
		return flag;
	}
	
	/**
     * 用于检查券商报送过来的相关不合法文件，以便券商因为不合法文件导致反馈有误差，（如文件夹，非7z文件等）
     * 设计上要求报送文件夹下的文件为空，才能重新报送。
     */
    public static String checkRubbishFileAndMove(File folderFile,String rubbishFileDir) {
        File[] tempList = folderFile.listFiles();
        String errMsg = new String();
        for (int i = 0; i < tempList.length; i++) {

            String encoding = System.getProperty("file.encoding");
            String filename=null;
            try {
                //将系统编码encoding转换为utf-8编码
                filename=new String(tempList[i].getName().getBytes(encoding),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (tempList[i].isDirectory()) {
                errMsg += "报送文件夹（" +  filename+ "）非法，不应报送文件夹;";

                moveFileToRubbishDir(rubbishFileDir, tempList[i]);

            } else {

                Matcher matcher = namePatternTmp.matcher(filename.toUpperCase());
                if (!matcher.find()) {
                    try {
                        errMsg += "报送文件" + filename + "非法;";

                        moveFileToRubbishDir(rubbishFileDir, tempList[i]);

                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            }
        }
        if(!errMsg.isEmpty()){
            errMsg+="正确的报送文件为7z压缩文件，文件名格式为 SC_\\w{8}_\\d{8}_\\d{3}_(Y|N)\\w\\.7Z";

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
			//过滤list文件
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

	/**解密 
	  * @param content  待解密内容 
	  * @param keyWord 解密密钥 
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
				 * 解密后的文件在原文件名前加"D_"
				 * 而且设计中解密之后的文件生成与未解密文件同目录。
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
				logger.debug("在解密文件时,读取"+content.getName()+"文件出错!", e);
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
	       	  * 删除报送文件下的原加密文件
	       	  */
			if(decrySuccess){
				content.delete();
			}
		}
		return decrySuccess;
	}
}
