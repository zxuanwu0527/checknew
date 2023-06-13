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
		logger.debug("***********开始***********");
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
	private static String   path_mulu = null; //服务器文件主目录
	private static String   check_rtn  = "0";
	private static int      count_file = 0 ;//用于记录检测文件顺序
	private static String  copyjmfile = null; //用于存储解压后的备份券商文件存放的根目录
	private static String  interfacecount = null; //用于存储需要报送的接口文件个数
	
	@SuppressWarnings("rawtypes")
	static void init()
	{	
		try
		{
			luceneConfig = XMLUtil.loadFile("a/FileConfig.xml");//用于配置数据库参数的
			luceneConfig1 = XMLUtil.loadFile("xml/FileOrder.xml");//用于配置文件校验调用方式的参数的
			luceneConfig2 = XMLUtil.loadFile("DecryptFileConfig.xml");//用于配置解密程序相关参数	
			List nodelist = null;
			Iterator it = null;
			
			nodelist = luceneConfig2.selectNodes("//root/copyjmfile");
			it = nodelist.iterator();
			while(it.hasNext())
			{
				Element pathelement = (Element)it.next();
				copyjmfile = (String)pathelement.getText();
			}
			
			//复制券商一开始报送数据到copyjmfile文件夹下 ---caolei
			nodelist = luceneConfig.selectNodes("//root/path");
			it = nodelist.iterator();
			while(it.hasNext())
			{
				Element pathelement = (Element)it.next();
				path_mulu = (String)pathelement.getText();
			}
			//接收券商数据 文件夹d:/tjjc/receive---caolei
			nodelist = luceneConfig.selectNodes("//root/driverclass");
			it = nodelist.iterator();
			while(it.hasNext())
			{
				Element driverclasselement = (Element)it.next();
				driverclass = (String)driverclasselement.getText();
			}
			
			//jdbc连接oracle ----caolei
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
			
			//以上连接数据库 ---caolei
			nodelist = luceneConfig.selectNodes("//root/count");
			it = nodelist.iterator();
			while(it.hasNext())
			{
				Element passwordelement = (Element)it.next();
				interfacecount = (String)passwordelement.getText();
			}//通过confileconfig获取券商应该报送的文件数据（不包括list）---caolei
		}catch (Exception e)
		{
			logger.debug("读取配置文件出错");
		}
		logger.debug("copyjmfile="+copyjmfile);
		logger.debug("interfacecount="+interfacecount);
		logger.debug("----------------------------------文件路径="+path_mulu+"-------------------------------");
		logger.debug("数据库连接配置如下:");
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
			logger.debug("创建数据库连接类出错！");
		}
		catch (SQLException e)
		{
			logger.debug("连接数据库出错！");
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
				    //得到证券公司上传文件的根目录
					Element filenode = (Element)filenodes.get(i);
					String  directory_name = filenode.getText();
					//循环文件夹下的文件
					File file = new File(path_mulu+directory_name+"/");//匹配完成路径。-caolei
					if(file.exists())
					{
						 /*每检验一轮重新给check_rtn赋值,避免券商上报了几次,
					     * 导致后续的接口处理有错误，把上次检验的日志数据又处理了一次
					    */
						check_rtn  = String.valueOf(new Date().getTime());
						//检验文件夹下面的文件是否已经传输完毕							
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
						//清除垃圾文件（如文件夹，非合法文件）
						com.szkingdom.File_Transmission.delFolderorFile(file);
					    //检验文件是否传输完毕
						String ok_flag = com.szkingdom.File_Transmission.check_Transmission(file, interfacecount);
						if("1".equals(ok_flag))
						{
							System.out.println("文件传输完毕！");
						}
						else
						{
							System.out.println("文件还没有传输完毕！");
							filenodes.remove(i);
							i--;
							continue;
						}
						
						CreateFile(copyjmfile);
						
						String interface_sjrq = getInterfaceDate(file);
						//备份券商报送过来的原始的所有文件
						if(!"0".equals(interface_sjrq))
						{
							com.szkingdom.File_AES_Decrypt.CopyFile(file,copyjmfile,path_mulu,interface_sjrq);    	    	
						}
						else
						{
							logger.debug("券商没有报送任何文件,只有ftdp生成的ok文件");
							filenodes.remove(i);
					        	i--;
					        	continue;
						}
						int listfile_count = 0 ;//记录目录下文件个数
						//判断券商是否在报送了32个文件过来后，没有等到反馈就又报送了文件
						if((file.listFiles().length-1)>Integer.parseInt(interfacecount))
						{
							listfile_count = file.listFiles().length;
							filenodes.remove(i);
					        	i--;
					        	deleteAllFile(file);
					        	
					        	if(zqgsdm !=null && zqgsdm !="")
							{
					        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"报送过来的数据文件个数不对,数量为:"+listfile_count,false);
							}
					        	/*
							 * 将变量zqgsdm,sjrq重新赋值为""，避免静态变量引起
							 * 下次检验的证券公司的数据，传过来的变量值
							 * 还是原来的值，导致检验有误。
							 */
					        	zqgsdm ="";
							sjrq ="";
							continue;
						}
						else
						{
							//判断券商报送的32个文件中是否传送了文件夹
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
						        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"请每次传送的文件个数务必正确,在没有收到反馈之前请耐心等待,报送过来的数据文件个数不对或者压缩文件里面的文件格式不是TXT!",false);
								}
						        	/*
									 * 将变量zqgsdm,sjrq重新赋值为""，避免静态变量引起
									 * 下次检验的证券公司的数据，传过来的变量值
									 * 还是原来的值，导致检验有误。
									 */
						        	zqgsdm ="";
								sjrq ="";
								continue;
							}
						}
						//检验*.list 文件是否存在
						String listfilename = com.szkingdom.File_AES_Decrypt.Find_ListFile(file);
						if(listfilename==null)
						{
						    	logger.debug("list文件不存在！");	
						    	filenodes.remove(i);
					        	i--;
					        	deleteAllFile(file);
					        	
					        	if(zqgsdm !=null && zqgsdm !="")
							{
							  com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"*.list文件不存在",false);
							}
					        	/*
								 * 将变量zqgsdm,sjrq重新赋值为""，避免静态变量引起
								 * 下次检验的证券公司的数据，传过来的变量值
								 * 还是原来的值，导致检验有误。
								 */
					        	zqgsdm ="";
							sjrq ="";
							continue;
						}
						else
						{
							//检验*.list里面记录的文件名的数据日期标志是否一致
						    	String  list_sjrq_flag = com.szkingdom.File_AES_Decrypt.check_List_Sjrq(new File(file.getPath()+"\\"+listfilename));
							if(!"1".equals(list_sjrq_flag))
							{
						    	    		filenodes.remove(i);
							        	i--;							        	
							        	deleteAllFile(file);
							        	if(zqgsdm !=null && zqgsdm !="")
									{
							        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"*.list文件中记录的接口文件名的数据日期标志不一致！",false);
									}
							        	/*
										 * 将变量zqgsdm,sjrq重新赋值为""，避免静态变量引起
										 * 下次检验的证券公司的数据，传过来的变量值
										 * 还是原来的值，导致检验有误。
										 */
							        	zqgsdm ="";
							        	sjrq ="";
							        	continue;
						    	    }
							/*
							 * 解压券商报送文件夹下的"*.7z"文件，完成后删除"ok"文件;
							 * 注意：因为该操作是dos命令，所以当前机器上必须安装7z软件。
							 */
						    try {    
								String directory_path = file.getPath();
								String line="";
								String[] cmdstring ={"cmd","/c","1.bat",directory_path};
							    //Process p = Runtime.getRuntime().exec("cmd /c  7z x F:/谭论/test/10330000/*.7z  -aoa -oF:/谭论/test/10330000");
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
					    				    	
					    	//计算文件个数和*.list文件中是否相符，是否和要求的接口文件个数一致
					    	int  listfilecount = com.szkingdom.File_AES_Decrypt.count(new File(file.getPath()+"\\"+listfilename));
					    	int  filecount = file.listFiles().length -1;
					    	
					    	//判断.list文件名数是否为应报数,上报的文件数是否也为应报数
					    	if((listfilecount!=Integer.parseInt(interfacecount))
					    			||(filecount!=Integer.parseInt(interfacecount)))
					    	 {
						    		filenodes.remove(i);
						        	i--;							        	
						        	deleteAllFile(file);
						        	if(zqgsdm !=null && zqgsdm !="")
								{
						        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"*.list文件中记录的文件数量或者报送过来的接口文件数量不为应报数！",false);
								}
						        	/*
									 * 将变量zqgsdm,sjrq重新赋值为""，避免静态变量引起
									 * 下次检验的证券公司的数据，传过来的变量值
									 * 还是原来的值，导致检验有误。
									 */
						        	zqgsdm ="";
								sjrq ="";
						        	continue;
					    	    }
					    	PMFile(file);//解密文件
					    	logger.debug("解密是否成功标志："+aes_flag);
					    	if("1".equals(aes_flag))
					    	{
						    		//用于比较*.list文件里面的文件是否正确
						    		String compare_flag = com.szkingdom.File_AES_Decrypt.Check_ListFile(new File(file.getPath()+"\\"+listfilename),file);
						    		logger.debug("*.list文件是否正确："+compare_flag);
						    		if(!"1".equals(compare_flag))
							    	{
							    			filenodes.remove(i);
								        	i--;							        	
								        	deleteAllFile(file);
								        	if(zqgsdm !=null && zqgsdm !="")
											{
								        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"*.list文件中记录的文件名称和报送过来的文件名称不符",false);
											}
								        	/*
											 * 将变量zqgsdm,sjrq重新赋值为""，避免静态变量引起
											 * 下次检验的证券公司的数据，传过来的变量值
											 * 还是原来的值，导致检验有误。
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
						        	//把解密完所有文件过后将解密检验标志重新赋值为"1"
						        	aes_flag ="1";				   
						        	deleteAllFile(file);
						        	logger.debug(zqgsdm);
						        	if(zqgsdm !=null && zqgsdm !="")
								{
						        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"解密机构编码为"+zqgsdm+"的券商报送过来的文件失败",false);
								}
						        	else
						        	{
						        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"券商报送过来机构编码错误！",false);
						        	}
						        	/*
									 * 将变量zqgsdm,sjrq重新赋值为""，避免静态变量引起
									 * 下次检验的证券公司的数据，传过来的变量值
									 * 还是原来的值，导致检验有误。
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
						//开始检验文件
						logger.debug("*********************************");
						logger.debug("***********开始检测文件名***********");
						logger.debug("*********************************");											   
						
					    //在检验31个*.txt数据文件之前，确保正在检验的这家券商没有重复传送文件或者第一次就传了超出32个文件
						if((file.listFiles().length)>Integer.parseInt(interfacecount))
						{
							
							listfile_count = file.listFiles().length+1;
							filenodes.remove(i);
					        	i--;
					        	deleteAllFile(file);
					        	if(zqgsdm !=null && zqgsdm !="")
							{
					        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"报送过来的数据文件个数不对,数量为:"+listfile_count,false);
							}
				        	/*
							 * 将变量zqgsdm,sjrq重新赋值为""，避免静态变量引起
							 * 下次检验的证券公司的数据，传过来的变量值
							 * 还是原来的值，导致检验有误。
							 */
					        	zqgsdm ="";
							sjrq ="";
							continue;
						}
						else
						{
							//确保.txt文件并且每个的大小不超过200M
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
						        		com.szkingdom.Report_ErrorFile.createResultFile(zqgsdm,sjrq,check_rtn,"请每次传送的文件个数务必正确,在没有收到反馈之前请耐心等待,报送过来的数据文件个数不对或者压缩文件里面的文件格式不是TXT或者文件的格式不正确!",false);
										
								}
						        	/*
									 * 将变量zqgsdm,sjrq重新赋值为""，避免静态变量引起
									 * 下次检验的证券公司的数据，传过来的变量值
									 * 还是原来的值，导致检验有误。
									 */
						        	zqgsdm ="";
								sjrq ="";
								continue;
							}
						}
						
					    CheckFile(file,conn);
					    logger.debug("当前券商编码为："+zqgsdm);
						
					   //删除备用库历史数据
					    com.szkingdom.Insert_Data.delete_data(conn,file,zqgsdm);
					    //把检验文件当前个数赋值为零
					    count_file = 0;
					    
					    if(file_flag.equals("1"))
					    {
			    				CopyFile(conn,file,true);
			    				logger.debug(zqgsdm);
							
							//把检验完所有文件过后将文件检验标志重新赋值为"1"
			    				file_flag ="1";
			    				//删除当前未处理掉的解压解密后文件
			    				deleteAllFile(file);
			    				if(zqgsdm !=null && zqgsdm !="")
							{
								System.out.println(zqgsdm);
								com.szkingdom.Test.init(zqgsdm,sjrq,check_rtn);
							} 
							filenodes.remove(i);
							/*
							 * 将变量zqgsdm,sjrq重新赋值为""，避免静态变量引起
							 * 下次检验的证券公司的数据到备用库后，传过来的变量值
							 * 还是原来的值，导致检验有误。
							 */
							zqgsdm ="";
							sjrq ="";
							
							i--;
							logger.debug("文件目录 "+path_mulu+directory_name+"/ 下的文件检验完毕");
			    		}
			    		else
			    		{
			    			CopyFile(conn,file,false);
			    			//把检验完所有文件过后将文件检验标志重新赋值为"1"
			    			file_flag ="1";
			    			//删除当前未处理掉的解压解密后文件
			    			deleteAllFile(file);
			    			if(zqgsdm !=null && zqgsdm !="")
							{
								System.out.println(zqgsdm);
								com.szkingdom.Test.createResultFile(zqgsdm,sjrq,check_rtn,false);
							} 
			    			filenodes.remove(i);
			    			/*
							 * 将变量zqgsdm,sjrq重新赋值为""，避免静态变量引起
							 * 下次检验的证券公司的数据到备用库后，传过来的变量值
							 * 还是原来的值，导致检验有误。
							 */
							zqgsdm ="";
							sjrq ="";
							
							i--;
							logger.debug("文件目录 "+path_mulu+directory_name+"/ 下的文件检验完毕");
			    		}
						
					}
				       else
				        {
				        	filenodes.remove(i);
				        	i--;
				        	logger.debug("没有找到文件目录："+path_mulu+directory_name+"/");
				        }
				}
			}	
			/*if(conn!=null)
			{
				try {
					conn.close();
				} catch (SQLException e) {
					logger.debug("关闭数据库连接失败！");
				}
				conn=null;
			
			}*/
				
		}
	}
	/*
	 * 用于得到上报的接口数据文件的准确日期
	 */
	static String getInterfaceDate(File file)
	{
		
		File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
     	  	
			/*给后面备份原始数据文件用， sjrq 为该券商所报数据的数据日期
		    	 * 注：1、如果*.list文件不存在，则该参数以报送文件上的信息为准
		    	 * 2、如果*.list文件存在，则以*.list文件上的日期为准
		    	 * */
			//删除OK文件
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
		//没有*.list文件
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
	 * 用于清空券商报送过来的相关文件，以便券商可以继续报送，
	 * 设计上要求报送文件夹下的文件为空，才能重新报送。
	 */	
	static void deleteAllFile(File file)
	{
		File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
     	  	
			/*给后面备用库数据检查参数赋值用，zqgsdm 为该线程检验的券商的公司代码，
			 * sjrq 为该券商所报数据的数据日期
			 * 注：该两个参数以报送文件上的信息为准*/
			//过滤list文件
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
	 * 解密券商的报送过来的解压之后加密文件
	 * @param file 指向券商报送文件所在目录
	 * 注意：标志aes_flag以确认文件是否解密成功，flag为1表示所有文件已解密成功；
	 * flag为0表示其中有文件有错误 
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
		    	//过滤list文件
		    	String type =file.getName().split("\\.")[1];
		    	
		    	if(!"LIST".equals(type.toUpperCase()))
		    	{
			    	String  filename =file.getName().substring(0, file.getName().length()-4);
			       String[] params = filename.split("_");
			    	
			    	//上报券商机构编码
			    	String  zqgsdm =params[1];
			    	//判断上报文件是否加密
			    	int  flag = params[params.length-1].indexOf("A");
			    	//用于存放当前密钥
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
	
	
	 /**解密 
	  * @param content  待解密内容 
	  * @param keyWord 解密密钥 
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
                Cipher cipher = Cipher.getInstance("AES");// 创建密码器  
                cipher.init(Cipher.DECRYPT_MODE, key);// 初始化  
                 
                fis = new FileInputStream(content);
			br   = new BufferedReader(new InputStreamReader(fis));
				/*
				 * 加密之后的文件名称跟之前的名称只差后面的后缀_A,因此解密之后的文件名称为去除_A后缀；
				 * 而且设计中解密之后的文件生成在报送文件夹下，与未解密文件同目录。
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
				logger.debug("在解密文件时,读取"+content.getName()+"文件出错!");
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
        	  * 删除报送文件下的原加密文件
        	  */
        	 try {
        		 if(fos != null)
        		 {
	        		 fos.close();
	 	 			 br.close();
	 	 			 fis.close();
        		 }
 			} catch (IOException e1) {
 				logger.debug("关闭当前读取的文件成功!");
 			}
        	 content.delete();
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
	 * 检测券商的所有文件名以及文件其它相关是否合法
	 * @param file
	 * @param conn
	 * 注意：标志file_flag以确认数据是否导入备用数据库，flag为1表示所有文件已通过文件检查；
	 * flag为0表示其中有文件有错误
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
		    		logger.debug("文件"+filename+"不合法，传递参数不等于7!");
		    	}
		    	else
		    	{
		    	*/
		    	/*给后面备用库数据检查参数赋值用，zqgsdm 为该线程检验的券商的公司代码，
		    	 * sjrq 为该券商所报数据的数据日期
		    	 * 注：该两个参数以报送文件上的信息为准*/
		    	if(zqgsdm == null ||zqgsdm == "")
		    	{
		    		zqgsdm = (file.getName()).split("_")[1];
		    		sjrq = (file.getName()).split("_")[2];
		    	}
		    	
		    	Node node1 = luceneConfig1.selectSingleNode("//order/type[@id='file']");
		    	
		    	if(node1 == null)
		    	{
		    		logger.debug("配置文件没有配置文件检测,请检查配置文件是否需要检测!");
		    	}
		    	else
		    	{
		    		List nodes = node1.selectNodes("dict");
		    		if(nodes.size()==0)
		    		{
		    			logger.debug("文件检测中,没有配置步骤节点!");
			    	}
			    	else
			    	{
			    		String  flag ="";	
					logger.debug("***************"+(++count_file)+"****************");
				    	String  filename = file.getName();
				    	logger.debug("文件名："+filename);
				    	
				    	int j = 0;//用于记录检测成功了的步骤数
			    		
				    	for(;j<nodes.size();j++)
				    	{
					        if(nodes.get(j)!=null)
					        {
					    		Element element = (Element) nodes.get(j);
						       
					    		/*调存储过程的检验,没有参数*/
					    		List pros = element.selectNodes("pro");
					    		
					    		/*特殊处理，只用于检验文件记录列数是否正确*/
					    		List params = element.selectNodes("params");
					    	
					    		if(params.size()!=0)
					    		{
					    			logger.debug("正在检测第"+element.attributeValue("order")+"步骤,请稍后!");
					    			flag = CheckDataColumn(file,conn,params);
						    		if(flag.equals("0"))
						    		{
						    			logger.debug("检测第"+element.attributeValue("order")+"步骤失败!");
						    			break;
						    		}
						    		
						    		if(flag.equals("1"))
						    		{
						    			logger.debug("检测第"+element.attributeValue("order")+"步骤成功!");
						    		}
					    		}
					    		else
					    		{
					    			if(pros.size()==0)
						    		{
						    			logger.debug("文件检测中,第"+element.attributeValue("order")+"步骤下存储过程节点没有配置,请检测配置文件是否需要配置!");
						    		}
						    		else
						    		{
						    			logger.debug("正在检测第"+element.attributeValue("order")+"步骤,请稍后!");
							    		
							    		flag = CheckData(file,conn,pros);
							    		if(flag.equals("0"))
							    		{
							    			logger.debug("检测第"+element.attributeValue("order")+"步骤失败!");
							    			break;
							    		}
							    		
							    		if(flag.equals("1"))
							    		{
							    			logger.debug("检测第"+element.attributeValue("order")+"步骤成功!");
							    		}
							    		
						    		}
					    		
					    		}
					    		
					        }
					    	
				    	}
			    		//步骤检测都成功了，那么nodes.size()==j,该文件检验通过，否则文件检验没有通过
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
	 * 用于处理检测了的文件，文件检测成功，则copy到需要处理的成功文件目录下；如果
	 * 文件检测失败，同样会copy到有问题的文件目录下以方便维护人员查找。copy完成后删除
	 * 检测目录下的相关文件，避免下次重复检测
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
		/*存储要备份到哪里的文件目录路径*/
		String path ="";
		
		/*存储需要备份的文件相对服务器总目路的相对路径*/
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
				// 过滤list文件
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
				/* 将配置的接口列数放入HashMap里面 */
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
				/* 实际表的列数,在xml/FileOrder.xml中配置的 */
				int cols_table = Integer.parseInt((String) row.get(filename.split("_")[4]));
				logger.debug((String) row.get(filename.split("_")[4]));
				// 调用同步方法，避免多个线程操作同一个表插入数据库出现死锁
				if (flag) {
					com.szkingdom.Insert_Data.insert_data(conn, file, encoding, cols_table);
				}

			} catch (FileNotFoundException e) {
				logger.debug("在备份文件时," + file.getName() + "没有找到!");
			} catch (UnsupportedEncodingException e) {
				logger.debug("在备份文件时,设置读取" + file.getName() + "文件编码格式为" + encoding + "异常!");
			} catch (IOException e) {
				e.printStackTrace();
				logger.debug("在备份文件时,读取" + file.getName() + "文件出错!");
			} catch (NumberFormatException e) {
				e.printStackTrace();
				logger.debug("在备份文件时,读取" + file.getName() + "文件的编号出错!");
			} finally {
				file.delete();
			}
		}
	
		
   }
   /**
    * 用于创建文件夹，如果不存在，则创建该文件夹
    * @param path 
    */
   static  void   CreateFile(String   path){ 
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
			       file.mkdirs();       
			       System.out.println("have   made   a   dir   ！"   );       
		   }
		   s = s.replaceFirst("/", ".");
		   j = s.indexOf('/');
	   }
   }    



	/**
	 * 检测文件头和记录数是否合法，成功后检测文件数据列数是否合法
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
			  			    	logger.debug("在文件"+file.getName()+"中,执行存储过程"+proname+"异常!");
				    		}
				    		if(proflag.equals("1"))
				    		{
				    			flag = "1";
				    			logger.debug("在文件"+file.getName()+"中,执行存储过程"+proname+"成功!");
				    		}
				    		if(proflag.equals("2"))
				    		{
				    			flag ="0";
				    			logger.debug("在文件"+file.getName()+"中,执行存储过程"+proname+"失败!");
				    		}
			    		}
			    		else
			    		{
			    			//当检测文件的存储过程配置了参数,执行下面if下的代码
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
				    	  	    //检验第一行格式是否正确，是否以"0x0A"结束
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
				    	  		logger.debug("在文件"+file.getName()+"中,文件头记录数正确!");
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
									    			logger.debug("配置参数值信息:"+(f+1)+","+String.valueOf(cols));
									    			
									    		}
									    		if("rows".equals(paramvalue))
									    		{
									    			cstm.setString(f+1, String.valueOf(x));
									    			logger.debug("配置参数值信息:"+(f+1)+","+String.valueOf(x));
									    		}
									    }
		
				    		  			    cstm.execute();
				    		  			    String proflag = cstm.getString(params.size()+3);//执行存储过程返回的标志
				    		  			    if(proflag.equals("0"))
				    		  			    {
					    			    			flag ="0";
					    		  			    	logger.debug("在文件"+file.getName()+"中,检测第"+x+"行数据执行存储过程"+proname+"异常!");
				    		  			    }
				    			    		
					    			    		if(proflag.equals("1"))
					    			    		{
					    			    			//避免一个文件中有几行有错，但是最后一行没有错
					    			    			if("1".equals(flag))
					    			    			{
					    			    				flag = "1";
					    			    			}
					    			    			logger.debug("在文件"+file.getName()+"中,检测第"+x+"行数据执行存储过程"+proname+"成功!");
					    			    		}
					    			    		
					    			    		if(proflag.equals("2"))
					    			    		{
					    			    			flag ="0";
					    			    			logger.debug("在文件"+file.getName()+"中,检测第"+x+"行数据执行存储过程"+proname+"失败!");
					    			    		}
			    					}
			    		  		}
			    	  			
			    	  		}
				    	  	else
		    	  			{
				    	  		flag ="0";
				    	  		logger.debug("在文件"+file.getName()+"中,文件头记录数不匹配,记录数为"+recordcount+",实际为"+count+"!");
		    	  			}
				    	  	br.close();
			    		}
	
			    		
			    		
			    		cstm.close();
			    		cstm = null;
			    		
		    		}
		    			
		    		catch(SQLException e)
		    		{
		    			
		    			logger.debug("调用存储过程"+proname+"出错");
		    			return "0";	
		    			
		    		}
		    		catch(FileNotFoundException e)
	    			{
		    			logger.debug("文件"+file.getName()+"不存在!");
		    			return "0";
		    			
	    			}
		    		catch(IOException e)
		    		{
		    			logger.debug("读取文件"+file.getName()+"出错");
		    			return "0";
		    			
		    		}
		          
			    		
			   }
	    	}
	    return flag;
		    		
		    	

	}

	/**
	 * 检测文件头和记录数是否合法，成功后检测文件数据列数是否合法
	 * @param file  
	 * @param conn
	 */
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	static String CheckDataColumn(File file,Connection conn,List prams)
	{
		
		
		String filename = file.getName();
		String filename_sjrq = filename.split("\\_")[2];
		String flag  ="1";
		/*将配置的接口 列数？放入HashMap里面*///row 是“行”的意思！
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
	   		//当检测文件的存储过程配置了参数,执行下面if下的代码
			RandomAccessFile br = new RandomAccessFile(file.getPath(),"r");
			//FileInputStream fis = new FileInputStream(file);
			//BufferedReader br   = new BufferedReader(new InputStreamReader(RandomAccessFile,"utf-8"));
			String s ="";
			int recordcount = 0;//文件头记录的记录数
			int count = -1;//实际记录数
			int x = 0;
			String filehead_flag ="0";//记录该文件的文件头是否正确标志,"1"表示正确,"0"表示不正确
		  	while((s = br.readLine())!=null)
		  	{
		  		//检验第一行格式是否正确
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
	    	  	
		  	
		  	/*检验文件记录列数*/
		  	if(count==recordcount)
		  	{
		  		logger.debug("在文件"+file.getName()+"中,文件头记录数正确!");
		  	    //避免一个文件中有几行有错，但是最后一行没有错
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
			  		//这里有漏洞，要是是文件头记录数和文件里面的真实记录不匹配，虽然检验程序没有问题
			  		//但是没有记录错误信息，交互文件里面就为空，所以最后人为插一条记录到检验结果表。
			  		logger.debug("在文件"+file.getName()+"中,文件头记录数不匹配,记录数为"+recordcount+",实际为"+count+"!");
			  		
			  		String sql ="insert into t_krms_check_result(zqgsdm,check_rtn,check_id,check_son_id,check_son_name,check_rs,check_rs_mou,back_value,check_date,data_code,sjrq) values (?,?,?,?,?,?,?,?,?,?,?)";
			  		
			  	     PreparedStatement pst = conn.prepareStatement(sql); 
			  		
			  		String vzqgsdm = filename.split("_")[1]; 
			  		String vcheck_rtn =  check_rtn   ;
			  		int check_idx = 99999    ;
			  		String check_son_idx =  "02";
			  		String vcheck_son_name = recordcount+"|"+count+"|"+ vcheck_rtn;
		  			String vcheck_rs = "02"   ;
		  			
		  			String vcheck_rs_mou = "在文件"+ filename+"中,文件头记录数不匹配,记录数为" + recordcount +",实际为"+count +"!" ;
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
		  			//这里有漏洞，要是是文件头记录头有问题，虽然检验程序没有问题
		  			//但是没有记录错误信息，交互文件里面就为空，所以最后人为插一条记录到检验结果表。
			  		logger.debug("在文件"+ filename+"中,文件头格式不正确!" );
			  		
			  		String sql ="insert into t_krms_check_result(zqgsdm,check_rtn,check_id,check_son_id,check_son_name,check_rs,check_rs_mou,back_value,check_date,data_code,sjrq) values (?,?,?,?,?,?,?,?,?,?,?)";
			  		
			  		PreparedStatement pst = conn.prepareStatement(sql); 
			  		
			  		String vzqgsdm = filename.split("_")[1]; 
			  		String vcheck_rtn =  check_rtn   ;
			  		int check_idx = 99999    ;
			  		String check_son_idx =  "02";
			  		String vcheck_son_name = recordcount+"|"+count+"|"+ vcheck_rtn;
		  			String vcheck_rs = "02"   ;
		  			
		  			String vcheck_rs_mou = "在文件"+ filename+"中,文件头格式不正确!" ;
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
			
			logger.debug("在文件记录列数检验中,写日志出错");
			return "0";	
			
		}
		catch(FileNotFoundException e)
		{
			logger.debug("文件"+file.getName()+"不存在!");
			return "0";
			
		}
		catch(IOException e)
		{
			logger.debug("读取文件"+file.getName()+"出错");
			return "0";
			
		}
	    return flag;
		    		
		    	

	}
	
	/*
	static void zip(String inputFileName) throws Exception 
    { 
            String zipFileName="D:\\file\\file.zip";//打包后文件名字
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
	//删除文件夹下的所有文件
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

	
	
	//不能用exists()来判断文件夹的存在,而是用isDirectory()       
	static  void   checkExist(String   path){ 
	
	      //循环创建文件夹
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
	          System.out.println("have   made   a   dir   ！"   );       
	      }       
	  }    
   */

}