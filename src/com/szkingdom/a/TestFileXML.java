package com.szkingdom.a;


import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;

import com.szkingdom.LogUtils;
import com.szkingdom.XMLUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/** Index all text files under a directory. */
public class TestFileXML extends Thread
{
	private static Logger logger = Logger.getLogger(TestFileXML.class);
    private static String zqgsdm ="";
    private static String sjrq ="";
	
    public TestFileXML()
	{
	}

	public void run()
	{
		Date start = new Date();
		try
		{
			init();
			//zip("D:\\file\\file");
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LogUtils.debugStackTrace(e.getStackTrace()); 
		}
		finally
		{

			Date end = new Date();
			logger.debug("start at "+start.getTime());
			logger.debug("end   at "+end.getTime());
			logger.debug(end.getTime() - start.getTime() + " total milliseconds");			
		}
	}

	public static void main(String[] args)
	{
		
		logger.debug("");
		logger.debug("*********************************");
		logger.debug("***********��ʼ***********");
		logger.debug("*********************************");
		logger.debug("");
	
		Date start = new Date();
		try
		{
			init();
			//zip("D:\\file\\file");
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{

			Date end = new Date();
			logger.debug("start at "+start.getTime());
			logger.debug("end   at "+end.getTime());
			logger.debug(end.getTime() - start.getTime() + " total milliseconds");			
		}
	}

	private static org.dom4j.Document luceneConfig;
	private static org.dom4j.Document luceneConfig1;
	private static org.dom4j.Document luceneConfig2;
	private static String   driverclass = null;
	private static String   url = null;
	private static String   userid = null;
	private static String   password = null;
	private static String   path_mulu = null; //�������ļ���Ŀ¼
	private static String   check_rtn  = "0";
	private static int      count_file = 0 ;//���ڼ�¼����ļ�˳��
	private static String  copyjmfile = null; //���ڴ洢��ѹ��ı���ȯ���ļ���ŵĸ�Ŀ¼
	private static String  interfacecount = null; //���ڴ洢��Ҫ���͵Ľӿ��ļ�����
	
	@SuppressWarnings("rawtypes")
	static void init()
	{	
		try
		{
			luceneConfig = XMLUtil.loadFile("a/FileConfig.xml");//�����������ݿ������
			luceneConfig1 = XMLUtil.loadFile("xml/FileOrder.xml");//���������ļ�У����÷�ʽ�Ĳ�����
			luceneConfig2 = XMLUtil.loadFile("DecryptFileConfig.xml");//�������ý��ܳ�����ز���	
			List nodelist = null;
			Iterator it = null;
			
			nodelist = luceneConfig2.selectNodes("//root/copyjmfile");
			it = nodelist.iterator();
			while(it.hasNext())
			{
				Element pathelement = (Element)it.next();
				copyjmfile = (String)pathelement.getText();
			}
			
			//����ȯ��һ��ʼ�������ݵ�copyjmfile�ļ����� ---caolei
			nodelist = luceneConfig.selectNodes("//root/path");
			it = nodelist.iterator();
			while(it.hasNext())
			{
				Element pathelement = (Element)it.next();
				path_mulu = (String)pathelement.getText();
			}
			//����ȯ������ �ļ���d:/tjjc/receive---caolei
			nodelist = luceneConfig.selectNodes("//root/driverclass");
			it = nodelist.iterator();
			while(it.hasNext())
			{
				Element driverclasselement = (Element)it.next();
				driverclass = (String)driverclasselement.getText();
			}
			
			//jdbc����oracle ----caolei
			nodelist = luceneConfig.selectNodes("//root/url");
			it = nodelist.iterator();
			while(it.hasNext())
			{
				Element urlelement = (Element)it.next();
				url = (String)urlelement.getText();
			}
			
			nodelist = luceneConfig.selectNodes("//root/userid");
			it = nodelist.iterator();
			while(it.hasNext())
			{
				Element useridelement = (Element)it.next();
				userid = (String)useridelement.getText();
			}
			
			nodelist = luceneConfig.selectNodes("//root/password");
			it = nodelist.iterator();
			while(it.hasNext())
			{
				Element passwordelement = (Element)it.next();
				password = (String)passwordelement.getText();
			}
			
			//�����������ݿ� ---caolei
			nodelist = luceneConfig.selectNodes("//root/count");
			it = nodelist.iterator();
			while(it.hasNext())
			{
				Element passwordelement = (Element)it.next();
				interfacecount = (String)passwordelement.getText();
			}//ͨ��confileconfig��ȡȯ��Ӧ�ñ��͵��ļ����ݣ�������list��---caolei
		}catch (Exception e)
		{
			logger.debug("��ȡ�����ļ�����");
		}
		logger.debug("copyjmfile="+copyjmfile);
		logger.debug("interfacecount="+interfacecount);
		logger.debug("----------------------------------�ļ�·��="+path_mulu+"-------------------------------");
		logger.debug("���ݿ�������������:");
		logger.debug("driverclass="+driverclass);
		logger.debug("url="+url);
		logger.debug("userid="+userid);
		logger.debug("password="+password);
		Connection conn = null;
		try
		{
			Class.forName(driverclass);
			conn = DriverManager.getConnection(url, userid, password);
		} catch (ClassNotFoundException e) {
			logger.debug("�������ݿ����������");
		}
		catch (SQLException e)
		{
			logger.debug("�������ݿ����");
			logger.debug("conn="+conn);
		}
		
		while(true)
		{
			List filenodes = null;
			filenodes = luceneConfig.selectNodes("//root/zqgs/filepath");
			while(filenodes !=null && filenodes.size()!=0)
			{
				for(int i=0;i<filenodes.size();i++) 
				{
				    //�õ�֤ȯ��˾�ϴ��ļ��ĸ�Ŀ¼
					Element filenode = (Element)filenodes.get(i);
					String  directory_name = filenode.getText();
					//ѭ���ļ����µ��ļ�
					File file = new File(path_mulu+directory_name+"/");//ƥ�����·����-caolei
					if(file.exists())
					{
						 /*ÿ����һ�����¸�check_rtn��ֵ,����ȯ���ϱ��˼���,
					     * ���º����Ľӿڴ����д��󣬰��ϴμ������־�����ִ�����һ��
					    */
						check_rtn  = String.valueOf(new Date().getTime());
						//�����ļ���������ļ��Ƿ��Ѿ��������							
						/*
						File okfile = new File(file.getPath()+"\\ok");
						/*
						try {
							FileOutputStream fis = new FileOutputStream(okfile);
							fis.close();
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						
						*/
						//��������ļ������ļ��У��ǺϷ��ļ���
						com.szkingdom.File_Transmission.delFolderorFile(file);
					    //�����ļ��Ƿ������
						String ok_flag = com.szkingdom.File_Transmission.check_Transmission(file, interfacecount);
						if("1".equals(ok_flag))
						{
							System.out.println("�ļ�������ϣ�");
						}
						else
						{
							System.out.println("�ļ���û�д�����ϣ�");
							filenodes.remove(i);
							i--;
							continue;
						}
						
						CreateFile(copyjmfile);
						
						String interface_sjrq = getInterfaceDate(file);
						//����ȯ�̱��͹�����ԭʼ�������ļ�
						if(!"0".equals(interface_sjrq))
						{
							com.szkingdom.File_AES_Decrypt.CopyFile(file,copyjmfile,path_mulu,interface_sjrq);    	    	
						}
						else
						{
							logger.debug("ȯ��û�б����κ��ļ�,ֻ��ftdp���ɵ�ok�ļ�");
							filenodes.remove(i);
					        	i--;
					        	continue;
						}
						int listfile_count = 0 ;//��¼Ŀ¼���ļ�����
						//�ж�ȯ���Ƿ��ڱ�����32���ļ�������û�еȵ��������ֱ������ļ�
						if((file.listFiles().length-1)>Integer.parseInt(interfacecount))
						{
							listfile_count = file.listFiles().length;
							filenodes.remove(i);
					        	i--;
					        	deleteAllFile(file);
					        	
					        	if(zqgsdm !=null && zqgsdm !="")
							{
					        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"���͹����������ļ���������,����Ϊ:"+listfile_count,false);
							}
					        	/*
							 * ������zqgsdm,sjrq���¸�ֵΪ""�����⾲̬��������
							 * �´μ����֤ȯ��˾�����ݣ��������ı���ֵ
							 * ����ԭ����ֵ�����¼�������
							 */
					        	zqgsdm ="";
							sjrq ="";
							continue;
						}
						else
						{
							//�ж�ȯ�̱��͵�32���ļ����Ƿ������ļ���
							File[] fileList = file.listFiles();
							String directoryflag ="1";
							for (int f = 0; f < fileList.length; f++) 
							{  	
							    	if(fileList[i].isDirectory())
							    	{
							    		directoryflag = "0";
							    		break;
							    	}
							}
							if("0".equals(directoryflag))
							{
								filenodes.remove(i);
						        	i--;
						        	deleteAllFile(file);
					        	
						        	if(zqgsdm !=null && zqgsdm !="")
								{
						        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"��ÿ�δ��͵��ļ����������ȷ,��û���յ�����֮ǰ�����ĵȴ�,���͹����������ļ��������Ի���ѹ���ļ�������ļ���ʽ����TXT!",false);
								}
						        	/*
									 * ������zqgsdm,sjrq���¸�ֵΪ""�����⾲̬��������
									 * �´μ����֤ȯ��˾�����ݣ��������ı���ֵ
									 * ����ԭ����ֵ�����¼�������
									 */
						        	zqgsdm ="";
								sjrq ="";
								continue;
							}
						}
						//����*.list �ļ��Ƿ����
						String listfilename = com.szkingdom.File_AES_Decrypt.Find_ListFile(file);
						if(listfilename==null)
						{
						    	logger.debug("list�ļ������ڣ�");	
						    	filenodes.remove(i);
					        	i--;
					        	deleteAllFile(file);
					        	
					        	if(zqgsdm !=null && zqgsdm !="")
							{
							  com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"*.list�ļ�������",false);
							}
					        	/*
								 * ������zqgsdm,sjrq���¸�ֵΪ""�����⾲̬��������
								 * �´μ����֤ȯ��˾�����ݣ��������ı���ֵ
								 * ����ԭ����ֵ�����¼�������
								 */
					        	zqgsdm ="";
							sjrq ="";
							continue;
						}
						else
						{
							//����*.list�����¼���ļ������������ڱ�־�Ƿ�һ��
						    	String  list_sjrq_flag = com.szkingdom.File_AES_Decrypt.check_List_Sjrq(new File(file.getPath()+"\\"+listfilename));
							if(!"1".equals(list_sjrq_flag))
							{
						    	    		filenodes.remove(i);
							        	i--;							        	
							        	deleteAllFile(file);
							        	if(zqgsdm !=null && zqgsdm !="")
									{
							        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"*.list�ļ��м�¼�Ľӿ��ļ������������ڱ�־��һ�£�",false);
									}
							        	/*
										 * ������zqgsdm,sjrq���¸�ֵΪ""�����⾲̬��������
										 * �´μ����֤ȯ��˾�����ݣ��������ı���ֵ
										 * ����ԭ����ֵ�����¼�������
										 */
							        	zqgsdm ="";
							        	sjrq ="";
							        	continue;
						    	    }
							/*
							 * ��ѹȯ�̱����ļ����µ�"*.7z"�ļ�����ɺ�ɾ��"ok"�ļ�;
							 * ע�⣺��Ϊ�ò�����dos������Ե�ǰ�����ϱ��밲װ7z�����
							 */
						    try {    
								String directory_path = file.getPath();
								String line="";
								String[] cmdstring ={"cmd","/c","1.bat",directory_path};
							    //Process p = Runtime.getRuntime().exec("cmd /c  7z x F:/̷��/test/10330000/*.7z  -aoa -oF:/̷��/test/10330000");
								Process p = Runtime.getRuntime().exec(cmdstring);
								BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
								BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
								while ((line = bri.readLine()) != null) {
									System.out.println(line);
								}
								bri.close();
								while ((line = bre.readLine()) != null) {
									System.out.println(line);
								}
								bre.close();
								p.waitFor();
								System.out.println("Done."+p.exitValue());
							} catch (IOException e1) {
								e1.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
					    				    	
					    	//�����ļ�������*.list�ļ����Ƿ�������Ƿ��Ҫ��Ľӿ��ļ�����һ��
					    	int  listfilecount = com.szkingdom.File_AES_Decrypt.count(new File(file.getPath()+"\\"+listfilename));
					    	int  filecount = file.listFiles().length -1;
					    	
					    	//�ж�.list�ļ������Ƿ�ΪӦ����,�ϱ����ļ����Ƿ�ҲΪӦ����
					    	if((listfilecount!=Integer.parseInt(interfacecount))
					    			||(filecount!=Integer.parseInt(interfacecount)))
					    	 {
						    		filenodes.remove(i);
						        	i--;							        	
						        	deleteAllFile(file);
						        	if(zqgsdm !=null && zqgsdm !="")
								{
						        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"*.list�ļ��м�¼���ļ��������߱��͹����Ľӿ��ļ�������ΪӦ������",false);
								}
						        	/*
									 * ������zqgsdm,sjrq���¸�ֵΪ""�����⾲̬��������
									 * �´μ����֤ȯ��˾�����ݣ��������ı���ֵ
									 * ����ԭ����ֵ�����¼�������
									 */
						        	zqgsdm ="";
								sjrq ="";
						        	continue;
					    	    }
					    	PMFile(file);//�����ļ�
					    	logger.debug("�����Ƿ�ɹ���־��"+aes_flag);
					    	if("1".equals(aes_flag))
					    	{
						    		//���ڱȽ�*.list�ļ�������ļ��Ƿ���ȷ
						    		String compare_flag = com.szkingdom.File_AES_Decrypt.Check_ListFile(new File(file.getPath()+"\\"+listfilename),file);
						    		logger.debug("*.list�ļ��Ƿ���ȷ��"+compare_flag);
						    		if(!"1".equals(compare_flag))
							    	{
							    			filenodes.remove(i);
								        	i--;							        	
								        	deleteAllFile(file);
								        	if(zqgsdm !=null && zqgsdm !="")
											{
								        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"*.list�ļ��м�¼���ļ����ƺͱ��͹������ļ����Ʋ���",false);
											}
								        	/*
											 * ������zqgsdm,sjrq���¸�ֵΪ""�����⾲̬��������
											 * �´μ����֤ȯ��˾�����ݣ��������ı���ֵ
											 * ����ԭ����ֵ�����¼�������
											 */
								        	zqgsdm ="";
										sjrq ="";
								        	continue;
							    	  }
						}
					    	else
					    	{
						    		filenodes.remove(i);
						        	i--;  	
						        	//�ѽ����������ļ����󽫽��ܼ����־���¸�ֵΪ"1"
						        	aes_flag ="1";				   
						        	deleteAllFile(file);
						        	logger.debug(zqgsdm);
						        	if(zqgsdm !=null && zqgsdm !="")
								{
						        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"���ܻ�������Ϊ"+zqgsdm+"��ȯ�̱��͹������ļ�ʧ��",false);
								}
						        	else
						        	{
						        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"ȯ�̱��͹��������������",false);
						        	}
						        	/*
									 * ������zqgsdm,sjrq���¸�ֵΪ""�����⾲̬��������
									 * �´μ����֤ȯ��˾�����ݣ��������ı���ֵ
									 * ����ԭ����ֵ�����¼�������
									 */
						        	zqgsdm ="";
						        	sjrq ="";
								continue;
						    	}
					    }
					    /*
						if(true)
						{
							filenodes.remove(i);
				        	i--;
							continue;
						}*/
						//��ʼ�����ļ�
						logger.debug("*********************************");
						logger.debug("***********��ʼ����ļ���***********");
						logger.debug("*********************************");											   
						
					    //�ڼ���31��*.txt�����ļ�֮ǰ��ȷ�����ڼ�������ȯ��û���ظ������ļ����ߵ�һ�ξʹ��˳���32���ļ�
						if((file.listFiles().length)>Integer.parseInt(interfacecount))
						{
							
							listfile_count = file.listFiles().length+1;
							filenodes.remove(i);
					        	i--;
					        	deleteAllFile(file);
					        	if(zqgsdm !=null && zqgsdm !="")
							{
					        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"���͹����������ļ���������,����Ϊ:"+listfile_count,false);
							}
				        	/*
							 * ������zqgsdm,sjrq���¸�ֵΪ""�����⾲̬��������
							 * �´μ����֤ȯ��˾�����ݣ��������ı���ֵ
							 * ����ԭ����ֵ�����¼�������
							 */
					        	zqgsdm ="";
							sjrq ="";
							continue;
						}
						else
						{
							//ȷ��.txt�ļ�����ÿ���Ĵ�С������200M
							File[] fileList = file.listFiles();
							String typeflag ="1";
							for (int f = 0; f < fileList.length; f++) 
							{  	
								String type =fileList[f].getName().split("\\.")[1];
							    	if(!"TXT".equals(type.toUpperCase()))
							    	{
							    		typeflag = "0";
							    		break;
							    	}
							    	logger.debug(fileList[f].getName());
							    	if(fileList[f].getName().split("_").length!=6)
							    	{
							    		typeflag = "0";
							    		break;
							    	}
							}
							if("0".equals(typeflag))
							{
								filenodes.remove(i);
						        	i--;
						        	deleteAllFile(file);
						        	
						        	if(zqgsdm !=null && zqgsdm !="")
								{
						        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"��ÿ�δ��͵��ļ����������ȷ,��û���յ�����֮ǰ�����ĵȴ�,���͹����������ļ��������Ի���ѹ���ļ�������ļ���ʽ����TXT�����ļ��ĸ�ʽ����ȷ!",false);
										
								}
						        	/*
									 * ������zqgsdm,sjrq���¸�ֵΪ""�����⾲̬��������
									 * �´μ����֤ȯ��˾�����ݣ��������ı���ֵ
									 * ����ԭ����ֵ�����¼�������
									 */
						        	zqgsdm ="";
								sjrq ="";
								continue;
							}
						}
						
					    CheckFile(file,conn);
					    logger.debug("��ǰȯ�̱���Ϊ��"+zqgsdm);
						
					   //ɾ�����ÿ���ʷ����
					    com.szkingdom.Insert_Data.delete_data(conn,file,zqgsdm);
					    //�Ѽ����ļ���ǰ������ֵΪ��
					    count_file = 0;
					    
					    if(file_flag.equals("1"))
					    {
			    				CopyFile(conn,file,true);
			    				logger.debug(zqgsdm);
							
							//�Ѽ����������ļ������ļ������־���¸�ֵΪ"1"
			    				file_flag ="1";
			    				//ɾ����ǰδ������Ľ�ѹ���ܺ��ļ�
			    				deleteAllFile(file);
			    				if(zqgsdm !=null && zqgsdm !="")
							{
								System.out.println(zqgsdm);
								com.szkingdom.Test.init(zqgsdm,sjrq,check_rtn);
							} 
							filenodes.remove(i);
							/*
							 * ������zqgsdm,sjrq���¸�ֵΪ""�����⾲̬��������
							 * �´μ����֤ȯ��˾�����ݵ����ÿ�󣬴������ı���ֵ
							 * ����ԭ����ֵ�����¼�������
							 */
							zqgsdm ="";
							sjrq ="";
							
							i--;
							logger.debug("�ļ�Ŀ¼ "+path_mulu+directory_name+"/ �µ��ļ��������");
			    		}
			    		else
			    		{
			    			CopyFile(conn,file,false);
			    			//�Ѽ����������ļ������ļ������־���¸�ֵΪ"1"
			    			file_flag ="1";
			    			//ɾ����ǰδ������Ľ�ѹ���ܺ��ļ�
			    			deleteAllFile(file);
			    			if(zqgsdm !=null && zqgsdm !="")
							{
								System.out.println(zqgsdm);
								com.szkingdom.Test.createResultFile(zqgsdm,sjrq,check_rtn,false);
							} 
			    			filenodes.remove(i);
			    			/*
							 * ������zqgsdm,sjrq���¸�ֵΪ""�����⾲̬��������
							 * �´μ����֤ȯ��˾�����ݵ����ÿ�󣬴������ı���ֵ
							 * ����ԭ����ֵ�����¼�������
							 */
							zqgsdm ="";
							sjrq ="";
							
							i--;
							logger.debug("�ļ�Ŀ¼ "+path_mulu+directory_name+"/ �µ��ļ��������");
			    		}
						
					}
				       else
				        {
				        	filenodes.remove(i);
				        	i--;
				        	logger.debug("û���ҵ��ļ�Ŀ¼��"+path_mulu+directory_name+"/");
				        }
				}
			}	
			/*if(conn!=null)
			{
				try {
					conn.close();
				} catch (SQLException e) {
					logger.debug("�ر����ݿ�����ʧ�ܣ�");
				}
				conn=null;
			
			}*/
				
		}
	}
	/*
	 * ���ڵõ��ϱ��Ľӿ������ļ���׼ȷ����
	 */
	static String getInterfaceDate(File file)
	{
		
		File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
     	  	
			/*�����汸��ԭʼ�����ļ��ã� sjrq Ϊ��ȯ���������ݵ���������
		    	 * ע��1�����*.list�ļ������ڣ���ò����Ա����ļ��ϵ���ϢΪ׼
		    	 * 2�����*.list�ļ����ڣ�����*.list�ļ��ϵ�����Ϊ׼
		    	 * */
			//ɾ��OK�ļ�
			if((fileList[i].getName()).toUpperCase().indexOf("OK")>-1)
			{
				fileList[i].delete();
			}
			else
			{
			    	String type =fileList[i].getName().split("\\.")[1];
			    	if("LIST".equals(type.toUpperCase()))
			    	{
				    	return (fileList[i].getName()).split("_")[2].substring(0, 8);
			    	}
			}
	  	}
		//û��*.list�ļ�
		if(file.listFiles().length>=1)
		{
			return file.listFiles()[0].getName().split("_")[2];
		}
		else
		{
			return "0";
		}
	}
	
	
	/*
	 * �������ȯ�̱��͹���������ļ����Ա�ȯ�̿��Լ������ͣ�
	 * �����Ҫ�����ļ����µ��ļ�Ϊ�գ��������±��͡�
	 */	
	static void deleteAllFile(File file)
	{
		File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
     	  	
			/*�����汸�ÿ����ݼ�������ֵ�ã�zqgsdm Ϊ���̼߳����ȯ�̵Ĺ�˾���룬
			 * sjrq Ϊ��ȯ���������ݵ���������
			 * ע�������������Ա����ļ��ϵ���ϢΪ׼*/
			//����list�ļ�
			File deletefile = fileList[i];
			if(deletefile.isDirectory())
			{
				delFolder(deletefile);
			}
			else
			{
			    	String type =deletefile.getName().split("\\.")[1];
			    	if((!"LIST".equals(type.toUpperCase())))
			    	{
				    	if(zqgsdm == null ||zqgsdm == "")
				    	{
				    		zqgsdm = (fileList[i].getName()).split("_")[1];
				    		sjrq = (fileList[i].getName()).split("_")[2];
				    	}
			    	}
			    	try
			    	{
			    		fileList[i].delete();
			    	}
			    	catch(Exception e)
			    	{
			    		e.printStackTrace();
			    	}
			}
     	  	
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
	
	/**
	 * ����ȯ�̵ı��͹����Ľ�ѹ֮������ļ�
	 * @param file ָ��ȯ�̱����ļ�����Ŀ¼
	 * ע�⣺��־aes_flag��ȷ���ļ��Ƿ���ܳɹ���flagΪ1��ʾ�����ļ��ѽ��ܳɹ���
	 * flagΪ0��ʾ�������ļ��д��� 
	 */
	private static String aes_flag ="1";
	@SuppressWarnings("rawtypes")
	static void PMFile(File file){
		
		if(file.isDirectory())
		{
	            File[] fileList = file.listFiles();
	            for (int i = 0; i < fileList.length; i++) {
	        	  	String filePath = fileList[i].getPath();
	        	  	PMFile(new File(filePath));
		  	 }
	    }
	    if(file.isFile())
	    {
		    	//String key ="a02fcbb8fe6cbc452803db415b8740aa";
		    	//����list�ļ�
		    	String type =file.getName().split("\\.")[1];
		    	
		    	if(!"LIST".equals(type.toUpperCase()))
		    	{
			    	String  filename =file.getName().substring(0, file.getName().length()-4);
			       String[] params = filename.split("_");
			    	
			    	//�ϱ�ȯ�̻�������
			    	String  zqgsdm =params[1];
			    	//�ж��ϱ��ļ��Ƿ����
			    	int  flag = params[params.length-1].indexOf("A");
			    	//���ڴ�ŵ�ǰ��Կ
			    	String key =null;
			    	
			    	List nodelist = null;
				Iterator it = null;
				nodelist = luceneConfig2.selectNodes("//root/zqgs/key[@id='"+zqgsdm+"']");
				it = nodelist.iterator();
				while(it.hasNext())
				{
					Element urlelement = (Element)it.next();
					key = (String)urlelement.getText();
				}
				logger.debug("key="+key);
			    	
				if(flag>-1)
			    	{
			    		decrypt(file,key);
			    	}
		    	}
	    }
	}
	
	
	 /**���� 
	  * @param content  ���������� 
	  * @param keyWord ������Կ 
	  */  
	 public static void decrypt(File content, String keyWord) {
         
		 FileInputStream fis = null;
    	 BufferedReader br = null;
    	 FileOutputStream fos = null   ;
		 
		 try {  
        	
        	 
        	 if((keyWord !=null)&&(!"null".equals(keyWord)))
        	 {
        		 
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
				 * ����֮����ļ����Ƹ�֮ǰ������ֻ�����ĺ�׺_A,��˽���֮����ļ�����Ϊȥ��_A��׺��
				 * ��������н���֮����ļ������ڱ����ļ����£���δ�����ļ�ͬĿ¼��
				 */
				
			fos = new FileOutputStream(content.getPath().substring(0, content.getPath().length()-6)+"."+content.getName().split("\\.")[1]);
				// BufferedWriter bw   = new BufferedWriter(new OutputStreamWriter(fos,fileencoding));
				    
	            String byteContent = "";
	 	           
 			while((byteContent=br.readLine())!=null)
 			{
 				//System.out.println(s);
 				byte[] result = cipher.doFinal(parseHexStr2Byte(byteContent)); 
	                
 				//bw.write(new String(result,"utf-8"));
 				fos.write(result);
 					
 			}
 			//bw.close();
 			fos.close();
 			br.close();
 			fis.close();
 			if("1".equals(aes_flag))
 			{
 				aes_flag = "1";
 			}
        	 }
        	 else
        	 {
        		 aes_flag = "0"; 
        	 }
		 } catch (IOException e) {
				e.printStackTrace();
				LogUtils.debugStackTrace(e.getStackTrace());
				logger.debug("�ڽ����ļ�ʱ,��ȡ"+content.getName()+"�ļ�����!");
				aes_flag = "0";
         } catch (NoSuchAlgorithmException e) {  
                 e.printStackTrace(); 
                 LogUtils.debugStackTrace(e.getStackTrace());
                 aes_flag = "0";
         } catch (NoSuchPaddingException e) {  
                 e.printStackTrace(); 
                 LogUtils.debugStackTrace(e.getStackTrace());
                 aes_flag = "0";
         } catch (InvalidKeyException e) {  
                 e.printStackTrace();
                 LogUtils.debugStackTrace(e.getStackTrace());
                 aes_flag = "0";
         } catch (IllegalBlockSizeException e) {  
                 e.printStackTrace();
                 LogUtils.debugStackTrace(e.getStackTrace());
                 aes_flag = "0";
         } catch (BadPaddingException e) {  
                 e.printStackTrace();
                 LogUtils.debugStackTrace(e.getStackTrace());
                 aes_flag = "0";
         }catch (NullPointerException e){ 
		         
		    	 e.printStackTrace();
		         LogUtils.debugStackTrace(e.getStackTrace());
		         aes_flag = "0";
		 }
         finally
         {
        	 /*
        	  * ɾ�������ļ��µ�ԭ�����ļ�
        	  */
        	 try {
        		 if(fos != null)
        		 {
	        		 fos.close();
	 	 			 br.close();
	 	 			 fis.close();
        		 }
 			} catch (IOException e1) {
 				logger.debug("�رյ�ǰ��ȡ���ļ��ɹ�!");
 			}
        	 content.delete();
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
	 * ���ȯ�̵������ļ����Լ��ļ���������Ƿ�Ϸ�
	 * @param file
	 * @param conn
	 * ע�⣺��־file_flag��ȷ�������Ƿ��뱸�����ݿ⣬flagΪ1��ʾ�����ļ���ͨ���ļ���飻
	 * flagΪ0��ʾ�������ļ��д���
	 */
	private static String file_flag ="1";
	@SuppressWarnings("rawtypes")
	static void CheckFile(File file,Connection conn){
		
		try
		{
			if(file.isDirectory()){
		              File[] fileList = file.listFiles();
		              for (int i = 0; i < fileList.length; i++) {
		            	  	String filePath = fileList[i].getPath();
		            	  	CheckFile(new File(filePath),conn);
		    	  	  }
		    	  	
		    }
		    if(file.isFile())
		    {
		    	/*
		    	String[] params = (filename.substring(0, filename.length()-4)).split("_");
		    	if(params.length!=7)
		    	{
		    		logger.debug("�ļ�"+filename+"���Ϸ������ݲ���������7!");
		    	}
		    	else
		    	{
		    	*/
		    	/*�����汸�ÿ����ݼ�������ֵ�ã�zqgsdm Ϊ���̼߳����ȯ�̵Ĺ�˾���룬
		    	 * sjrq Ϊ��ȯ���������ݵ���������
		    	 * ע�������������Ա����ļ��ϵ���ϢΪ׼*/
		    	if(zqgsdm == null ||zqgsdm == "")
		    	{
		    		zqgsdm = (file.getName()).split("_")[1];
		    		sjrq = (file.getName()).split("_")[2];
		    	}
		    	
		    	Node node1 = luceneConfig1.selectSingleNode("//order/type[@id='file']");
		    	
		    	if(node1 == null)
		    	{
		    		logger.debug("�����ļ�û�������ļ����,���������ļ��Ƿ���Ҫ���!");
		    	}
		    	else
		    	{
		    		List nodes = node1.selectNodes("dict");
		    		if(nodes.size()==0)
		    		{
		    			logger.debug("�ļ������,û�����ò���ڵ�!");
			    	}
			    	else
			    	{
			    		String  flag ="";	
					logger.debug("***************"+(++count_file)+"****************");
				    	String  filename = file.getName();
				    	logger.debug("�ļ�����"+filename);
				    	
				    	int j = 0;//���ڼ�¼���ɹ��˵Ĳ�����
			    		
				    	for(;j<nodes.size();j++)
				    	{
					        if(nodes.get(j)!=null)
					        {
					    		Element element = (Element) nodes.get(j);
						       
					    		/*���洢���̵ļ���,û�в���*/
					    		List pros = element.selectNodes("pro");
					    		
					    		/*���⴦��ֻ���ڼ����ļ���¼�����Ƿ���ȷ*/
					    		List params = element.selectNodes("params");
					    	
					    		if(params.size()!=0)
					    		{
					    			logger.debug("���ڼ���"+element.attributeValue("order")+"����,���Ժ�!");
					    			flag = CheckDataColumn(file,conn,params);
						    		if(flag.equals("0"))
						    		{
						    			logger.debug("����"+element.attributeValue("order")+"����ʧ��!");
						    			break;
						    		}
						    		
						    		if(flag.equals("1"))
						    		{
						    			logger.debug("����"+element.attributeValue("order")+"����ɹ�!");
						    		}
					    		}
					    		else
					    		{
					    			if(pros.size()==0)
						    		{
						    			logger.debug("�ļ������,��"+element.attributeValue("order")+"�����´洢���̽ڵ�û������,���������ļ��Ƿ���Ҫ����!");
						    		}
						    		else
						    		{
						    			logger.debug("���ڼ���"+element.attributeValue("order")+"����,���Ժ�!");
							    		
							    		flag = CheckData(file,conn,pros);
							    		if(flag.equals("0"))
							    		{
							    			logger.debug("����"+element.attributeValue("order")+"����ʧ��!");
							    			break;
							    		}
							    		
							    		if(flag.equals("1"))
							    		{
							    			logger.debug("����"+element.attributeValue("order")+"����ɹ�!");
							    		}
							    		
						    		}
					    		
					    		}
					    		
					        }
					    	
				    	}
			    		//�����ⶼ�ɹ��ˣ���ônodes.size()==j,���ļ�����ͨ���������ļ�����û��ͨ��
			    		if(nodes.size()==j)
			    		{
			    			//CopyFile(conn,file,true);
			    			if("1".equals(file_flag))
			    			{
			    				file_flag ="1";
			    			}
			    		}
			    		else
			    		{
			    			//CopyFile(conn,file,false);
			    			file_flag ="0";
			    		}
			    	}
		    	}
			    	
		    }
		}catch(Exception e)
		{
			e.printStackTrace();
			LogUtils.debugStackTrace(e.getStackTrace());
		}
    }
	/**
	 * ���ڴ������˵��ļ����ļ����ɹ�����copy����Ҫ����ĳɹ��ļ�Ŀ¼�£����
	 * �ļ����ʧ�ܣ�ͬ����copy����������ļ�Ŀ¼���Է���ά����Ա���ҡ�copy��ɺ�ɾ��
	 * ���Ŀ¼�µ�����ļ��������´��ظ����
	 * @param file
	 */
   @SuppressWarnings({ "rawtypes", "unchecked" })
static  void CopyFile(Connection conn,File file,boolean flag)
   {
	    //System.out.println(file.length());
	    
	    List nodelist = null;
	    Iterator it = null;
	    String successfile ="";
	    String failfile ="";
	    String encoding ="";
	    String filename = file.getName();
		
		nodelist = luceneConfig.selectNodes("//root/successfile");
		it = nodelist.iterator();
		while(it.hasNext())
		{
			Element pathelement = (Element)it.next();
			successfile = (String)pathelement.getText();
		}
		
		nodelist = luceneConfig.selectNodes("//root/failfile");
		it = nodelist.iterator();
		while(it.hasNext())
		{
			Element pathelement = (Element)it.next();
			failfile = (String)pathelement.getText();
		}
		
		nodelist = luceneConfig.selectNodes("//root/fileencoding");
		it = nodelist.iterator();
		while(it.hasNext())
		{
			Element pathelement = (Element)it.next();
			encoding = (String)pathelement.getText();
		}
		
		File   wf = null; 
		/*�洢Ҫ���ݵ�������ļ�Ŀ¼·��*/
		String path ="";
		
		/*�洢��Ҫ���ݵ��ļ���Է�������Ŀ·�����·��*/
		String sub_path ="";
		
		if(flag)
		{
			path = successfile;
		}
		else
		{
			path = failfile;
			
		}
		
		sub_path = (file.getPath()).substring(path_mulu.length());
		
		if(file.isDirectory())
		{
			CreateFile(path+sub_path+"/");
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
		     	  	String filePath = fileList[i].getPath();
		     	  	CopyFile(conn,new File(filePath),flag);
		  	}
		}
		else
		{
			try {
				String sjrq = null;
				// ����list�ļ�
				String type = file.getName().split("\\.")[1];
				if ("LIST".equals(type.toUpperCase())) {
					sjrq = filename.split("_")[2].substring(0, 8);
				} else {
					sjrq = filename.split("_")[2];
				}
				String sub_directory = sub_path.substring(0, sub_path.length() - filename.length());
				CreateFile(path + sub_directory + sjrq + "/");
				wf = new File(path + sub_directory + sjrq + "/" + filename);
				if (wf.exists()) {
					wf = null;
					wf = new File(path + sub_path.substring(0, sub_path.length() - filename.length()) + sjrq
							+ "/" + filename.split("\\.")[0] + "_" + new Date().getTime() + "."
							+ filename.split("\\.")[1]);
				}

				FileInputStream fis = new FileInputStream(file);
				logger.debug(file.getPath());
				logger.debug(wf.getPath());

				FileOutputStream fos = new FileOutputStream(wf);

				BufferedReader br = new BufferedReader(new InputStreamReader(fis, encoding));

				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, encoding));
				String s = "";

				while ((s = br.readLine()) != null) {

					// System.out.println(s);
					bw.write(s + "\n");
					/*
					 * if(flag) { if(s.indexOf("RecordCount=")>0) {
					 * continue;
					 * 
					 * } else { String[] params = filename.split("_");
					 * 
					 * insert_data(conn,(s.split("0x0A")[0]).split("\\|"
					 * ),params[4]); } }
					 */
				}

				br.close();
				bw.close();
				fos.close();
				fis.close();
				/* �����õĽӿ���������HashMap���� */
				HashMap row = new HashMap();
				List params = luceneConfig1.selectNodes("//order/type/dict/params/param");
				// logger.debug(String.valueOf(params.size()));
				for (int f = 0; f < params.size(); f++) {

					if (params.get(f) != null) {
						Element elementparam = (Element) (params.get(f));

						String param_value = elementparam.getText();

						row.put(param_value.split(",")[0], param_value.split(",")[1]);
						// logger.debug(param_value.split(",")[1]);
					}
				}
				/* ʵ�ʱ������,��xml/FileOrder.xml�����õ� */
				int cols_table = Integer.parseInt((String) row.get(filename.split("_")[4]));
				logger.debug((String) row.get(filename.split("_")[4]));
				// ����ͬ���������������̲߳���ͬһ����������ݿ��������
				if (flag) {
					com.szkingdom.Insert_Data.insert_data(conn, file, encoding, cols_table);
				}

			} catch (FileNotFoundException e) {
				logger.debug("�ڱ����ļ�ʱ," + file.getName() + "û���ҵ�!");
			} catch (UnsupportedEncodingException e) {
				logger.debug("�ڱ����ļ�ʱ,���ö�ȡ" + file.getName() + "�ļ������ʽΪ" + encoding + "�쳣!");
			} catch (IOException e) {
				e.printStackTrace();
				logger.debug("�ڱ����ļ�ʱ,��ȡ" + file.getName() + "�ļ�����!");
			} catch (NumberFormatException e) {
				e.printStackTrace();
				logger.debug("�ڱ����ļ�ʱ,��ȡ" + file.getName() + "�ļ��ı�ų���!");
			} finally {
				file.delete();
			}
		}
	
		
   }
   /**
    * ���ڴ����ļ��У���������ڣ��򴴽����ļ���
    * @param path 
    */
   static  void   CreateFile(String   path){ 
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
			       file.mkdirs();       
			       System.out.println("have   made   a   dir   ��"   );       
		   }
		   s = s.replaceFirst("/", ".");
		   j = s.indexOf('/');
	   }
   }    



	/**
	 * ����ļ�ͷ�ͼ�¼���Ƿ�Ϸ����ɹ������ļ����������Ƿ�Ϸ�
	 * @param file  
	 * @param conn
	 */
	@SuppressWarnings("rawtypes")
	static String CheckData(File file,Connection conn,List pros)
	{
		String filename = file.getName();
		String flag  ="1";
		for(int m = 0;m<pros.size();m++)
	    	{
			   if(pros.get(m)!=null)
			   {
		    		
				   Element elementpros = (Element)(pros.get(m));
				   String  proname = elementpros.attributeValue("name");
				   List params = elementpros.selectNodes("param");
				   String sql = "{call " + userid + "."+proname+"(";
			    		
				   for(int f=0;f<params.size();f++)
				   {
			    			sql += "?,";
				   }
				   sql += "?,?,?)}";
				   try
				   {
			    		CallableStatement cstm = conn.prepareCall(sql);
			    		logger.debug(sql);
			    		cstm.setString(params.size()+1,filename.substring(0, filename.length()-4));
			    		cstm.setString(params.size()+2,check_rtn);
			    		cstm.registerOutParameter(params.size()+3, Types.VARCHAR);
			    		
			    		if(params.size() == 0)
			    		{
				    		cstm.execute();
				    		String proflag = cstm.getString(params.size()+3);
				    		if(proflag.equals("0"))
				    		{
				    			flag ="0";
			  			    	logger.debug("���ļ�"+file.getName()+"��,ִ�д洢����"+proname+"�쳣!");
				    		}
				    		if(proflag.equals("1"))
				    		{
				    			flag = "1";
				    			logger.debug("���ļ�"+file.getName()+"��,ִ�д洢����"+proname+"�ɹ�!");
				    		}
				    		if(proflag.equals("2"))
				    		{
				    			flag ="0";
				    			logger.debug("���ļ�"+file.getName()+"��,ִ�д洢����"+proname+"ʧ��!");
				    		}
			    		}
			    		else
			    		{
			    			//������ļ��Ĵ洢���������˲���,ִ������if�µĴ���
				    		RandomAccessFile br = new RandomAccessFile(file.getPath(),"r");
				    		//FileInputStream fis = new FileInputStream(file);
				    		//BufferedReader br   = new BufferedReader(new InputStreamReader(RandomAccessFile,"utf-8"));
				    		String s ="";
				    		int recordcount = 0;
				    		int count = -1;
				    		int x = 0;
				    	  	while((s = br.readLine())!=null)
				    	  	{
				    	  			//s =new String(s.getBytes(),"utf-8");
				    	  	    //�����һ�и�ʽ�Ƿ���ȷ���Ƿ���"0x0A"����
			    	  			if(count==-1&&s.indexOf("0x0A")>0)
			    	  			{
			    	  				count++;
			    	  			}
				    	  		String[] str = s.split("0x0A");
			    	  			String str_1=str[0];
			    	  			if(str_1.indexOf("RecordCount=")>0)
			    				{
			    					int w=str_1.indexOf("RecordCount=");
			    					String str_2 = str_1.substring(w+12);
			    					recordcount = Integer.parseInt(str_2); ;
			    				}
			    				else
			    				{
			    					count++;
			    				}
				    	  	}
				    	  	if(count==recordcount)
				    	  	{
				    	  		logger.debug("���ļ�"+file.getName()+"��,�ļ�ͷ��¼����ȷ!");
				    	  		br.seek(0);
			    	  			while((s = br.readLine())!=null)
			    		  		{
			    		  			if(s.indexOf("RecordCount=")>0)
			    					{
			    						continue;
			    					}
			    					else
			    					{
				    		  			    x++;
				    		  			    int cols = (s.split("0x0A")[0]).split("\\|").length;
				    		  			    for(int f=0;f<params.size();f++)
									    {
									    		Element elementparam =(Element)(params.get(f));
									    		String paramvalue = elementparam.getText();
									    		
									    		if("cols".equals(paramvalue))
									    		{
									    			cstm.setString(f+1, String.valueOf(cols));
									    			logger.debug("���ò���ֵ��Ϣ:"+(f+1)+","+String.valueOf(cols));
									    			
									    		}
									    		if("rows".equals(paramvalue))
									    		{
									    			cstm.setString(f+1, String.valueOf(x));
									    			logger.debug("���ò���ֵ��Ϣ:"+(f+1)+","+String.valueOf(x));
									    		}
									    }
		
				    		  			    cstm.execute();
				    		  			    String proflag = cstm.getString(params.size()+3);//ִ�д洢���̷��صı�־
				    		  			    if(proflag.equals("0"))
				    		  			    {
					    			    			flag ="0";
					    		  			    	logger.debug("���ļ�"+file.getName()+"��,����"+x+"������ִ�д洢����"+proname+"�쳣!");
				    		  			    }
				    			    		
					    			    		if(proflag.equals("1"))
					    			    		{
					    			    			//����һ���ļ����м����д��������һ��û�д�
					    			    			if("1".equals(flag))
					    			    			{
					    			    				flag = "1";
					    			    			}
					    			    			logger.debug("���ļ�"+file.getName()+"��,����"+x+"������ִ�д洢����"+proname+"�ɹ�!");
					    			    		}
					    			    		
					    			    		if(proflag.equals("2"))
					    			    		{
					    			    			flag ="0";
					    			    			logger.debug("���ļ�"+file.getName()+"��,����"+x+"������ִ�д洢����"+proname+"ʧ��!");
					    			    		}
			    					}
			    		  		}
			    	  			
			    	  		}
				    	  	else
		    	  			{
				    	  		flag ="0";
				    	  		logger.debug("���ļ�"+file.getName()+"��,�ļ�ͷ��¼����ƥ��,��¼��Ϊ"+recordcount+",ʵ��Ϊ"+count+"!");
		    	  			}
				    	  	br.close();
			    		}
	
			    		
			    		
			    		cstm.close();
			    		cstm = null;
			    		
		    		}
		    			
		    		catch(SQLException e)
		    		{
		    			
		    			logger.debug("���ô洢����"+proname+"����");
		    			return "0";	
		    			
		    		}
		    		catch(FileNotFoundException e)
	    			{
		    			logger.debug("�ļ�"+file.getName()+"������!");
		    			return "0";
		    			
	    			}
		    		catch(IOException e)
		    		{
		    			logger.debug("��ȡ�ļ�"+file.getName()+"����");
		    			return "0";
		    			
		    		}
		          
			    		
			   }
	    	}
	    return flag;
		    		
		    	

	}

	/**
	 * ����ļ�ͷ�ͼ�¼���Ƿ�Ϸ����ɹ������ļ����������Ƿ�Ϸ�
	 * @param file  
	 * @param conn
	 */
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	static String CheckDataColumn(File file,Connection conn,List prams)
	{
		
		
		String filename = file.getName();
		String filename_sjrq = filename.split("\\_")[2];
		String flag  ="1";
		/*�����õĽӿ� ����������HashMap����*///row �ǡ��С�����˼��
		HashMap row = new HashMap();
		Element elementparams = (Element)(prams.get(0));
		List params = elementparams.selectNodes("param");
		   
   		
   		for(int f=0;f<params.size();f++)
   		{	
   			if(params.get(f)!=null)
   			{
   				Element elementparam = (Element)(params.get(f));  
   				String  param_value = elementparam.getText();
   				row.put(param_value.split(",")[0], param_value.split(",")[1]);
   			}	
		}    				
		
   		try
   		{
	   		//������ļ��Ĵ洢���������˲���,ִ������if�µĴ���
			RandomAccessFile br = new RandomAccessFile(file.getPath(),"r");
			//FileInputStream fis = new FileInputStream(file);
			//BufferedReader br   = new BufferedReader(new InputStreamReader(RandomAccessFile,"utf-8"));
			String s ="";
			int recordcount = 0;//�ļ�ͷ��¼�ļ�¼��
			int count = -1;//ʵ�ʼ�¼��
			int x = 0;
			String filehead_flag ="0";//��¼���ļ����ļ�ͷ�Ƿ���ȷ��־,"1"��ʾ��ȷ,"0"��ʾ����ȷ
		  	while((s = br.readLine())!=null)
		  	{
		  		//�����һ�и�ʽ�Ƿ���ȷ
		  		//logger.debug(s.substring(s.indexOf("Version=")));
	  			if(count==-1&&s.indexOf("Version=")>-1&&s.substring(s.indexOf("Version=")).matches("Version=\\d+\\.\\d+\\|RecordCount=\\d+"))
	  			{
	  				count++;
	  				filehead_flag ="1";	  				
	  			}
	  			if(!"1".equals(filehead_flag))
	  			{
	  				logger.debug(s);
	  				break;
	  			}
		  		//String[] str = s.split("0x0A");
	  			//String str_1=str[0];
				
	  			String str_1=s;
	  			
	  			if(str_1.indexOf("RecordCount=")>0)
				{
					int w=str_1.indexOf("RecordCount=");
					String str_2 = str_1.substring(w+12);
					recordcount = Integer.parseInt(str_2);
				}
				else
				{
					/*
					if(s.indexOf("0x0A")>0)
					{
						count++;
					}*/
					count++;
				}
		  	}
	    	  	
		  	
		  	/*�����ļ���¼����*/
		  	if(count==recordcount)
		  	{
		  		logger.debug("���ļ�"+file.getName()+"��,�ļ�ͷ��¼����ȷ!");
		  	    //����һ���ļ����м����д��������һ��û�д�
	    			if("1".equals(flag))
	    			{
	    				flag = "1";
	    			}
		  	}
		  	else
			{
		  		flag ="0";
		  		if("1".equals(filehead_flag))
		  		{
			  		//������©����Ҫ�����ļ�ͷ��¼�����ļ��������ʵ��¼��ƥ�䣬��Ȼ�������û������
			  		//����û�м�¼������Ϣ�������ļ������Ϊ�գ����������Ϊ��һ����¼����������
			  		logger.debug("���ļ�"+file.getName()+"��,�ļ�ͷ��¼����ƥ��,��¼��Ϊ"+recordcount+",ʵ��Ϊ"+count+"!");
			  		
			  		String sql ="insert into t_krms_check_result(zqgsdm,check_rtn,check_id,check_son_id,check_son_name,check_rs,check_rs_mou,back_value,check_date,data_code,sjrq) values (?,?,?,?,?,?,?,?,?,?,?)";
			  		
			  	     PreparedStatement pst = conn.prepareStatement(sql); 
			  		
			  		String vzqgsdm = filename.split("_")[1]; 
			  		String vcheck_rtn =  check_rtn   ;
			  		int check_idx = 99999    ;
			  		String check_son_idx =  "02";
			  		String vcheck_son_name = recordcount+"|"+count+"|"+ vcheck_rtn;
		  			String vcheck_rs = "02"   ;
		  			
		  			String vcheck_rs_mou = "���ļ�"+ filename+"��,�ļ�ͷ��¼����ƥ��,��¼��Ϊ" + recordcount +",ʵ��Ϊ"+count +"!" ;
		  			String vback_value =  "2"   ;
		  			String vcheck_date =   new java.text.SimpleDateFormat("yyyyMMdd").format(new Date())  ;
		  			String v_sjjk =  filename.split("_")[4];
		  			String sbrq =   filename.split("_")[2];
		  			
		  			pst.setString(1, vzqgsdm);
		  			pst.setString(2,vcheck_rtn);
		  			pst.setInt(3,check_idx);
		  			pst.setString(4,check_son_idx);
		  			pst.setString(5,vcheck_son_name);
		  			pst.setString(6,vcheck_rs);
		  			pst.setString(7,vcheck_rs_mou);
		  			pst.setString(8,vback_value);
		  			pst.setString(9,vcheck_date);
		  			pst.setString(10,v_sjjk);
		  			pst.setString(11,sbrq);
		  			pst.execute();
		  			pst.close();
					pst = null;
		  		}
		  		else
		  		{
		  			//������©����Ҫ�����ļ�ͷ��¼ͷ�����⣬��Ȼ�������û������
		  			//����û�м�¼������Ϣ�������ļ������Ϊ�գ����������Ϊ��һ����¼����������
			  		logger.debug("���ļ�"+ filename+"��,�ļ�ͷ��ʽ����ȷ!" );
			  		
			  		String sql ="insert into t_krms_check_result(zqgsdm,check_rtn,check_id,check_son_id,check_son_name,check_rs,check_rs_mou,back_value,check_date,data_code,sjrq) values (?,?,?,?,?,?,?,?,?,?,?)";
			  		
			  		PreparedStatement pst = conn.prepareStatement(sql); 
			  		
			  		String vzqgsdm = filename.split("_")[1]; 
			  		String vcheck_rtn =  check_rtn   ;
			  		int check_idx = 99999    ;
			  		String check_son_idx =  "02";
			  		String vcheck_son_name = recordcount+"|"+count+"|"+ vcheck_rtn;
		  			String vcheck_rs = "02"   ;
		  			
		  			String vcheck_rs_mou = "���ļ�"+ filename+"��,�ļ�ͷ��ʽ����ȷ!" ;
		  			String vback_value =  "2"   ;
		  			String vcheck_date =   new java.text.SimpleDateFormat("yyyyMMdd").format(new Date())  ;
		  			String v_sjjk =  filename.split("_")[4];
		  			String sbrq =   filename.split("_")[2];
		  			
		  			pst.setString(1, vzqgsdm);
		  			pst.setString(2,vcheck_rtn);
		  			pst.setInt(3,check_idx);
		  			pst.setString(4,check_son_idx);
		  			pst.setString(5,vcheck_son_name);
		  			pst.setString(6,vcheck_rs);
		  			pst.setString(7,vcheck_rs_mou);
		  			pst.setString(8,vback_value);
		  			pst.setString(9,vcheck_date);
		  			pst.setString(10,v_sjjk);
		  			pst.setString(11,sbrq);
		  			pst.execute();
		  			pst.close();
					pst = null;
		  		}
			}
    	
		  	br.close();	 
   		}
	  	catch(SQLException e)
		{
			
			logger.debug("���ļ���¼����������,д��־����");
			return "0";	
			
		}
		catch(FileNotFoundException e)
		{
			logger.debug("�ļ�"+file.getName()+"������!");
			return "0";
			
		}
		catch(IOException e)
		{
			logger.debug("��ȡ�ļ�"+file.getName()+"����");
			return "0";
			
		}
	    return flag;
		    		
		    	

	}
	
	/*
	static void zip(String inputFileName) throws Exception 
    { 
            String zipFileName="D:\\file\\file.zip";//������ļ�����
            System.out.println(zipFileName);
            zip(zipFileName, new File(inputFileName)); 
    }
    
	static void zip(String zipFileName, File inputFile) throws Exception {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
            out.setEncoding("gbk"); 
            zip(out, inputFile, "");
            System.out.println("zip done");
            out.close();
    }
    
	static void zip(ZipOutputStream out, File f, String base) throws Exception 
    {
            if (f.isDirectory()) {
                    File[] fl = f.listFiles();
                    out.putNextEntry(new ZipEntry(base + "/"));
                    base = base.length() == 0 ? "" : base + "/";
                    for (int i = 0; i < fl.length; i++) 
                    {
                            zip(out, fl[i], base + fl[i].getName());
                    }
            }
            else {
                    out.putNextEntry(new ZipEntry(base));
                    FileInputStream in = new FileInputStream(f);
                    int b;
                    System.out.println(base);
                    while ( (b = in.read()) != -1) 
                    {
                            out.write(b);
                    }
                    in.close();
            }
    }
	//ɾ���ļ����µ������ļ�
   static void deleteAllFile(String folderFullPath){
        boolean ret = false;
        File file = new File(folderFullPath);
        if(file.exists()){
	          if(file.isDirectory()){
	              File[] fileList = file.listFiles();
	              for (int i = 0; i < fileList.length; i++) {
	            	  	String filePath = fileList[i].getPath();
	            	  	deleteAllFile(filePath);
	              }
	          }
	          if(file.isFile()){
	        	  file.delete();
             }
        }
        
    }

	
	
	//������exists()���ж��ļ��еĴ���,������isDirectory()       
	static  void   checkExist(String   path){ 
	
	      //ѭ�������ļ���
			String s = realPath;
			String m =""; 
			int j = realPath.indexOf("/");
		    for(;j>0;)
			{
				
				m = realPath.substring(0, j+1);
				checkExist(m);
				s = s.replaceFirst("/", ".");
				
				j = s.indexOf('/');
				
			}
	      File   file   =   new   File(path);    
	      if(file.isDirectory()){       
	          System.out.println("the   dir   is   exits");       
	      }else{       
	          file.mkdir();       
	          System.out.println("have   made   a   dir   ��"   );       
	      }       
	  }    
   */

}