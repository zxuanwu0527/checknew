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
//	SC_[���ͻ���]_[��������]_[�ڲ��汾��][�ر���ʶ]_[�ļ�����]_[����/ȫ����ʶ]
	
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
	 * �г����е��ļ�
	 * ��������֤ȯ��˾�ļ����б�
	 * @throws Exception 
	 */
	public static Map<String, List<File>> ListFile(File successFile) throws Exception{
		//�������key�����Ӧ�������ļ��б�value��
		Map<String, List<File>> insFiles = new HashMap<String, List<File>>();
		/**
		 * ��Ϊһ�������ļ����п��ܺ��г���31�������ļ���ֻѡ���������µ�31��
		 * keyΪ���ͣ�b01,b02....,c01...c18��
		 */
		Map<String, File> typeMap = new HashMap<String, File>();
		//�ļ����ļ��м���
		int directCount = 0;
		int fileCount = 0;
		int errorFileCount = 0;
		int zqgsCount = 0;
		if(successFile!=null){
	            if(successFile.isDirectory()){
	            	File[] fileArray=successFile.listFiles();
	            	if(fileArray!=null){
	            		//�ļ��������󻺴�
	            		StringBuffer errorDateSb = new StringBuffer();
		                    for (int i = 0; i < fileArray.length; i++) {
		                        //���������ļ�
		                    	if(fileArray[i].isDirectory()){
		                    		zqgsCount++;
		                    		File[] dateFiles = fileArray[i].listFiles();
		                    		//�ļ������󻺴�
		                    		StringBuffer errorFileSb = new StringBuffer();
		                    		for (int j = 0; j < dateFiles.length; j++){
		                    			if(dateFiles[j].isDirectory()){
		                    				directCount++;
		                    				if(matchYearMm(dateFiles[j].getName())){
		                    					//�ϸ��ļ�����
		                    					int matchFileCount = 0;
		                    					//����ļ����������ʽ
		                    					for(File date : dateFiles[j].listFiles()){
		                    						fileCount++;
		                    						if(matchDateFileName(fileArray[i].getName(), dateFiles[j].getName(), date.getName())
		                    								&& matchSuffix(date)){
		                    							matchFileCount++;
		                    							//���ڶԱȵ���ʱ����
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
		                        							logger.error("�ļ�����ƥ�䣺"+ errorFileSb.toString());
		                        							errorFileSb.delete(0, errorFileSb.length());
		                        						}
		                        					}
		                    					}
		                    					if(matchFileCount != FILE_COUNT_PER_DATE){
		                    						logger.error("�ļ���Ŀ���ԣ�"+matchFileCount+","+dateFiles[j]);
		                    					}
		                    					typeMap.clear();
		                    				}else{
		                    					errorDateSb.append(dateFiles[j].getAbsolutePath());
		                    					errorDateSb.append("; ");
		                    				}
		                    			}
		                    		}
		                    		if(errorFileSb.length() > 0){
              						logger.error("�ļ�����ƥ�䣺"+ errorFileSb.toString());
              						errorFileSb.delete(0, errorFileSb.length());
              					}
		                    	}else{
		                            throw new Exception("�ļ��ж�ȡʧ��");
		                        }
		                    }
		                    logger.error("�ļ����������б�"+errorDateSb);
	            	}else{
	            		throw new Exception("�ļ��ж�ȡʧ��");
	            	}
	            	logger.info("directCount:"+directCount+", fileCount:"+fileCount+", errorFileCount:"+errorFileCount+", zqgsCount:"+zqgsCount);
	            }else{
	            	//ֱ�Ӷ�ȡ�ļ�
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
					logger.error("ֱ�Ӷ�ȡ�ļ�ʧ�ܣ�"+successFile.getName());
				}
	            }
		}
		return insFiles;
	}

	public static CtlFilePo makeCtlFile(String logPath, List<File> dateFile, String tbName, boolean unrecoverable) throws IOException{
		//��¼��Ŀ
		int dateSum  = 0;
		//��������
		path = URLDecoder.decode(path, "UTF-8");
		CtlFilePo cp = null;
		if(dateFile != null && dateFile.size() > 0){
			cp = new CtlFilePo();
			//�����ļ�
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
	 * ��ȡ�����ļ���һ�У���ȡ��¼��
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
	 * ���õ�������Դ��cpdetector��ȡ�ļ������ʽ
	 * 
	 * @param path
	 *            Ҫ�ж��ļ������ʽ��Դ�ļ���·��
	 * @author huanglei
	 * @version 2012-7-12 14:05
	 */
//	public static String getFileEncode(String path) {
//		/*
//		 * detector��̽����������̽�����񽻸������̽��ʵ�����ʵ����ɡ�
//		 * cpDetector������һЩ���õ�̽��ʵ���࣬��Щ̽��ʵ�����ʵ������ͨ��add���� �ӽ�������ParsingDetector��
//		 * JChardetFacade��ASCIIDetector��UnicodeDetector��
//		 * detector���ա�˭���ȷ��طǿյ�̽���������Ըý��Ϊ׼����ԭ�򷵻�̽�⵽��
//		 * �ַ������롣ʹ����Ҫ�õ�����������JAR����antlr.jar��chardet.jar��cpdetector.jar
//		 * cpDetector�ǻ���ͳ��ѧԭ��ģ�����֤��ȫ��ȷ��
//		 */
//		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
//		/*
//		 * ParsingDetector�����ڼ��HTML��XML���ļ����ַ����ı���,���췽���еĲ�������
//		 * ָʾ�Ƿ���ʾ̽����̵���ϸ��Ϣ��Ϊfalse����ʾ��
//		 */
//		detector.add(new ParsingDetector(false));
//		/*
//		 * JChardetFacade��װ����Mozilla��֯�ṩ��JChardet����������ɴ�����ļ��ı���
//		 * �ⶨ�����ԣ�һ���������̽�����Ϳ�����������Ŀ��Ҫ������㻹�����ģ�����
//		 * �ٶ�Ӽ���̽���������������ASCIIDetector��UnicodeDetector�ȡ�
//		 */
//		detector.add(JChardetFacade.getInstance());// �õ�antlr.jar��chardet.jar
//		// ASCIIDetector����ASCII����ⶨ
//		detector.add(ASCIIDetector.getInstance());
//		// UnicodeDetector����Unicode�������Ĳⶨ
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
