package test.imp.date;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class FileOper {

	static Logger logger = Logger.getLogger(FileOper.class);
	static Pattern datePattern = Pattern.compile("^20(1[3-9]|2[0-9])[0,1][0-9][0,1,2,3][0-9]$");
	static String path = FileOper.class.getClassLoader().getResource(".").getPath();
	static String lineSeparator =	(String) java.security.AccessController.doPrivileged(
	               new sun.security.action.GetPropertyAction("line.separator"));
	static int FILE_COUNT_PER_DATE = 31;
//	SC_[报送机构]_[数据日期]_[内部版本号][重报标识]_[文件编码]_[增量/全量标识]
	
	static boolean matchYearMm(String date){
		Matcher m = datePattern.matcher(date);
		if(m.find()){
			return true;
		}
		return false;
	}
	static boolean matchDateFileName(String zqgsdm, String date, String fileName){
		Pattern p = Pattern.compile("^SC_"+zqgsdm+"_"+date+"_\\d{3}[Y,N]_[B,C][0,1][0-9]_[Z,Q].txt$", Pattern.CASE_INSENSITIVE );
		Matcher m = p.matcher(fileName);
		if(m.find()){
			return true;
		}
		return false;
	}
	
	static String getTbName(String fileName){
		String[] strs  = fileName.split("_");
		if(strs != null && strs.length == 6){
			return "rzrqhis.sc_"+strs[4].toLowerCase()+"_"+strs[2].substring(0	, 4)+"_"+strs[2].substring(4, 6);
		}
		return null;
	}
	static boolean matchSuffix(File file){
		if(file != null && file.getName().toLowerCase().endsWith(".txt")){
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) throws Exception{
//		System.out.println(matchYearMm("20121026"));
		ListFile(new File("E:\\successfile"));
//		System.out.println(matchSuffix(new File("E:\\successfile\\13370000\\20121019\\SC_13370000_20121019_001N_B01_Q.TXT")));
//		System.out.println(matchDateFileName("13370000","20121019","SC_13370000_20121019_001N_B01_Q.TXT"));
//		System.out.println(getTbName("SC_13370000_20121019_001N_B01_Q.TXT"));
//		System.out.println(getCountFromDateFile(new File("E:\\successfile\\13370000\\20121019\\SC_13370000_20121019_001N_B01_Q.TXT")));
//		System.out.println(getFileEncode("E:\\SC_10080000_20130514_001N_B01_Q.TXT"));
//		String a = "SC_10080000_20130514_001N_B01_Q.TXT".substring(26,29);
//		System.out.println(a);
	}
	/**
	 * 列出所有的文件
	 * 返回所有证券公司文件夹列表
	 * @throws Exception 
	 */
	public static Map<String, List<File>> ListFile(File successFile) throws Exception{
		//表格名（key）与对应的数据文件列表（value）
		Map<String, List<File>> insFiles = new HashMap<String, List<File>>();
		/**
		 * 因为一个日期文件夹中可能含有超过31个数据文件，只选择其中最新的31个
		 * key为类型：b01,b02....,c01...c18。
		 */
		Map<String, File> typeMap = new HashMap<String, File>();
		//文件及文件夹计数
		int directCount = 0;
		int fileCount = 0;
		int errorFileCount = 0;
		int zqgsCount = 0;
		if(successFile!=null){
	            if(successFile.isDirectory()){
	            	File[] fileArray=successFile.listFiles();
	            	if(fileArray!=null){
	            		//文件夹名错误缓存
	            		StringBuffer errorDateSb = new StringBuffer();
		                    for (int i = 0; i < fileArray.length; i++) {
		                        //设置日期文件
		                    	if(fileArray[i].isDirectory()){
		                    		zqgsCount++;
		                    		File[] dateFiles = fileArray[i].listFiles();
		                    		//文件名错误缓存
		                    		StringBuffer errorFileSb = new StringBuffer();
		                    		for (int j = 0; j < dateFiles.length; j++){
		                    			if(dateFiles[j].isDirectory()){
		                    				directCount++;
		                    				if(matchYearMm(dateFiles[j].getName())){
		                    					//合格文件计数
		                    					int matchFileCount = 0;
		                    					//检查文件名及编码格式
		                    					for(File date : dateFiles[j].listFiles()){
		                    						fileCount++;
		                    						if(matchDateFileName(fileArray[i].getName(), dateFiles[j].getName(), date.getName())
		                    								&& matchSuffix(date)){
		                    							matchFileCount++;
		                    							//用于对比的临时变量
		                    							String type = date.getName().substring(26,29);
		                    							
	                    								String tbName = getTbName(date.getName());
		                    							if(insFiles.get(tbName) != null){
		                    								if(matchFileCount > FILE_COUNT_PER_DATE &&  typeMap.get(type) != null){
		                    									long lastUpdateTimeStored = typeMap.get(type).lastModified();
		                    									long lastUpdateTimeThis = date.lastModified();
		                    									if(lastUpdateTimeThis > lastUpdateTimeStored){
		                    										insFiles.get(tbName).add(date);
		                    										insFiles.get(tbName).remove(typeMap.get(type));
		                    										typeMap.put(type, date);
		                    									}
		                    								}else{
		                    									typeMap.put(type, date);
		                    									insFiles.get(tbName) .add(date);
		                    								}
		                    							}else{
		                    								List<File> dateFile = new ArrayList<File>();
		                    								dateFile.add(date);
		                    								insFiles.put(tbName, dateFile);
		                    							}
		                        					}else{
		                        						errorFileCount++;
		                        						errorFileSb.append(date.getName());
		                        						if(errorFileCount % 400 == 0){
		                        							logger.error("文件名不匹配："+ errorFileSb.toString());
		                        							errorFileSb.delete(0, errorFileSb.length());
		                        						}
		                        					}
		                    					}
		                    					if(matchFileCount != FILE_COUNT_PER_DATE){
		                    						logger.error("文件数目不对："+matchFileCount+","+dateFiles[j]);
		                    					}
		                    					typeMap.clear();
		                    				}else{
		                    					errorDateSb.append(dateFiles[j].getAbsolutePath());
		                    					errorDateSb.append("; ");
		                    				}
		                    			}
		                    		}
		                    		if(errorFileSb.length() > 0){
              						logger.error("文件名不匹配："+ errorFileSb.toString());
              						errorFileSb.delete(0, errorFileSb.length());
              					}
		                    	}else{
		                            throw new Exception("文件夹读取失败");
		                        }
		                    }
		                    logger.error("文件夹名错误列表："+errorDateSb);
	            	}else{
	            		throw new Exception("文件夹读取失败");
	            	}
	            	logger.info("directCount:"+directCount+", fileCount:"+fileCount+", errorFileCount:"+errorFileCount+", zqgsCount:"+zqgsCount);
	            }else{
	            	//直接读取文件
	            	if(matchSuffix(successFile)){
					String tbName = getTbName(successFile.getName());
					if(insFiles.get(tbName) != null){
						insFiles.get(tbName) .add(successFile);
					}else{
						List<File> dateFile = new ArrayList<File>();
						dateFile.add(successFile);
						insFiles.put(tbName, dateFile);
					}
				}else{
					logger.error("直接读取文件失败："+successFile.getName());
				}
	            }
		}
		return insFiles;
	}

	public static CtlFilePo makeCtlFile(String logPath, List<File> dateFile, String tbName, boolean unrecoverable) throws IOException{
		//记录数目
		int dateSum  = 0;
		//处理中文
		path = URLDecoder.decode(path, "UTF-8");
		CtlFilePo cp = null;
		if(dateFile != null && dateFile.size() > 0){
			cp = new CtlFilePo();
			//控制文件
			File ctlFile = new File(path + tbName + ".ctl");
//		      ctlFile.deleteOnExit();
			ctlFile.createNewFile();
			BufferedWriter bw= null;
			try{
				bw = new BufferedWriter(new FileWriter(ctlFile));
				StringBuffer sb = new StringBuffer();
				if(unrecoverable){
					sb.append("UNRECOVERABLE");
					sb.append(lineSeparator);
				}
				sb.append("LOAD DATA");
				sb.append(lineSeparator);
				sb.append("CHARACTERSET 'UTF8'");
				sb.append(lineSeparator);
				for(File file : dateFile){
					dateSum += getCountFromDateFile(file);
					sb.append("INFILE '"+file.getPath()+"'");
					sb.append(lineSeparator);
					sb.append("BADFILE  '"+logPath + file.getName().substring(0,  file.getName().length() - 4)+".bad'");
					sb.append(lineSeparator);
					sb.append("DISCARDFILE  '"+ logPath + tbName +".dsc'");
					sb.append(lineSeparator);
				}
				if(dateSum == 0){
					return null;
				}
				sb.append("INTO TABLE "+tbName);
				sb.append(lineSeparator);
				sb.append("append");
				sb.append(lineSeparator);
				sb.append("WHEN(1-7)!='Version'");
				sb.append(lineSeparator);
				sb.append("FIELDS TERMINATED BY '|'");
				sb.append(lineSeparator);
				sb.append("("+DateFields.getFields(tbName.substring(11, 14))+")");
				bw.write(sb.toString());
				bw.flush();
			}catch(Exception e){
				logger.error("error in write ctl file:", e);
			}finally{
				if(bw != null){
					bw.close();
				}
			}
			cp.setFilePath(ctlFile.getPath());
			cp.setDataSum(dateSum);
			return cp;
		}
		return null;
	}

	/**
	 * 读取数据文件第一行，获取记录数
	 * @param dateFile
	 * @return
	 * @throws IOException 
	 */
	public static int getCountFromDateFile(File dateFile) throws IOException{
		if(dateFile != null && dateFile.canRead()){
			BufferedReader br = new BufferedReader(new FileReader(dateFile));
			try{
				String line = null;
				while((line = br.readLine()) != null){
					int countIndex = -1;
					if( (countIndex = line.indexOf("RecordCount=") ) != -1){
						String count = line.substring(countIndex+12);
						return Integer.parseInt(count);
					}
				}
			}finally{
				br.close();
			}
		}
		return 0;
	}

	/**
	 * 利用第三方开源包cpdetector获取文件编码格式
	 * 
	 * @param path
	 *            要判断文件编码格式的源文件的路径
	 * @author huanglei
	 * @version 2012-7-12 14:05
	 */
//	public static String getFileEncode(String path) {
//		/*
//		 * detector是探测器，它把探测任务交给具体的探测实现类的实例完成。
//		 * cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法 加进来，如ParsingDetector、
//		 * JChardetFacade、ASCIIDetector、UnicodeDetector。
//		 * detector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的
//		 * 字符集编码。使用需要用到三个第三方JAR包：antlr.jar、chardet.jar和cpdetector.jar
//		 * cpDetector是基于统计学原理的，不保证完全正确。
//		 */
//		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
//		/*
//		 * ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于
//		 * 指示是否显示探测过程的详细信息，为false不显示。
//		 */
//		detector.add(new ParsingDetector(false));
//		/*
//		 * JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码
//		 * 测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以
//		 * 再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。
//		 */
//		detector.add(JChardetFacade.getInstance());// 用到antlr.jar、chardet.jar
//		// ASCIIDetector用于ASCII编码测定
//		detector.add(ASCIIDetector.getInstance());
//		// UnicodeDetector用于Unicode家族编码的测定
//		detector.add(UnicodeDetector.getInstance());
//		java.nio.charset.Charset charset = null;
//		File f = new File(path);
//		try {
//			charset = detector.detectCodepage(f.toURI().toURL());
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		if (charset != null)
//			return charset.name();
//		else
//			return null;
//	}
}
