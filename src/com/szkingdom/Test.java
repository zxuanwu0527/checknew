package com.szkingdom;


import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;

import com.szkingdom.a.XMLUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

//生成Excel的类 
import jxl.*; 
import jxl.write.*; 
/** Index all text files under a directory. */
public class Test
{
	private static Logger logger = Logger.getLogger(Test.class);
	
	private Test()
	{
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
			init("10330000","20120305",String.valueOf(new Date())); 
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

	private static org.dom4j.Document luceneConfig =null;
	private static String   driverclass = null;
	private static String   url = null;
	private static String   userid = null;
	private static String   password = null;
	private static String   path_mulu = null;
	//private static int      count_file = 0 ;//用于记录检测备用库数据顺序
	//private static Date    check_rtn = new Date();
	public static void init(String zqgsdm,String sjrq,String check_rtn)
	{	
		
		int      count_pro = 0 ;//用于记录检测备用库数据存储过程顺序
		boolean  flag = true ;//用于记录检验备用数据库是否有错，"1"表示成功，"0"表示不成功
		
		try
		{
			luceneConfig = XMLUtil.loadFile("FileConfig.xml");
			
			Node node = luceneConfig.selectSingleNode("//root/path");
			path_mulu = node.getText();
			
			node =  luceneConfig.selectSingleNode("//root/driverclass");
			driverclass = node.getText();
			
			node =  luceneConfig.selectSingleNode("//root/url");
			url  = node.getText();
			
			node = luceneConfig.selectSingleNode("//root/userid");
			userid = node.getText();
			
			node = luceneConfig.selectSingleNode("//root/password");
			password = node.getText();
			
			logger.debug("----------------------------------导入文件路径="+path_mulu+"-------------------------------");
			logger.debug("数据库连接配置如下:");
			logger.debug("driverclass="+driverclass);
			logger.debug("url="+url);
			logger.debug("userid="+userid);
			logger.debug("password="+password);
			
			Connection conn = null;
			CallableStatement sqlstmt = null;
			Statement stm = null;
			ResultSet rs = null;
			String sSql = "";
			String sql = "";
			
			try {
				Class.forName(driverclass);
			    conn = DriverManager.getConnection(url, userid, password);
			    sql = "select t3.* from T_KRMS_CHECK_DICT t1,T_KRMS_check_execute t2,T_KRMS_check_detail_config t3 where t1.check_id = t2.check_id and t2.name = t3.name and t1.check_id!=90000 order by t1.CHECK_TYPE,t1.CHECK_DETAIL_TYPE,t2.Run_orders";
				stm = conn.createStatement();
			    rs = stm.executeQuery(sql);
			    List result = getResult(rs);
			    
			    logger.debug("*********************************");
				logger.debug("***********开始检测备用库数据***********");
				logger.debug("*********************************");
			    
				//检验备用数据库数据
				for(int i=0;i<result.size();i++)
			    {
			    	logger.debug("***************"+(++count_pro)+"****************");
			    	HashMap pro = (HashMap)result.get(i);
			    	logger.debug("存储过程名称："+pro.get("name")+";表名："+pro.get("param1"));
			    	sSql ="{call " + userid + "." + pro.get("name") + "(";
			    	for(int j=0;j<pro.size()+4;j++)
			    	{
			    		sSql += "?";
						
						if (j < pro.size()+3)
						{
							sSql += ",";
						}
					}
					
					sSql += ")}";
					logger.debug(sSql);
					try{
						sqlstmt = conn.prepareCall(sSql);
				    
						for(int k=1;k<pro.size();k++)
				    	{
							sqlstmt.setString(k, (String) pro.get("param"+k));
							logger.debug((String) pro.get("param"+k));
						}
						
						sqlstmt.setString(pro.size(), check_rtn);
						sqlstmt.setString(pro.size()+1, String.valueOf(i+1));
						sqlstmt.setString(pro.size()+2, zqgsdm);
						sqlstmt.setString(pro.size()+3, sjrq);
				        sqlstmt.registerOutParameter(pro.size()+4, Types.VARCHAR);
				        sqlstmt.execute();
				        logger.debug(sqlstmt.getString(pro.size()+4));
				        String pro_flag = sqlstmt.getString(pro.size()+4);
				        
				        
				        
				        //用于判断是否需要继续处理下面的从备用库到中心库的存储过程
				        
				        if(("proc_check_cols_date".toUpperCase().equals((String)pro.get("name")))&& (!("1".equals(pro_flag)))) 
				        {
				        	if(sqlstmt!=null)
							{
								sqlstmt.close();
						        sqlstmt = null;
							}
				        	flag = false;
				        	break;
				        }
				        
				        
				        
				        
				        //用于判断是否需要继续处理下面的从备用库到中心库的存储过程
				        
				        if(("proc_check_primary_key".toUpperCase().equals((String)pro.get("name")))&& (!("1".equals(pro_flag)))) 
				        {
				        	if(sqlstmt!=null)
							{
								sqlstmt.close();
						        sqlstmt = null;
							}
				        	flag = false;
				        	break;
				        }
				        
				        
				        
				        
				        
				        //用于判断是否需要继续处理下面的从备用库到中心库的存储过程
				        
				        if(("proc_check_insert_before".toUpperCase().equals((String)pro.get("name")))&& (!("1".equals(pro_flag)))) 
				        {
				        	if(sqlstmt!=null)
							{
								sqlstmt.close();
						        sqlstmt = null;
							}
				        	flag = false;
				        	break;
				        }
				        
					}catch (SQLException e)
					{
						logger.debug("处理存储过程出错！");
					}
					finally
					{
						
						//避免打开游标数量超出数据库限制
						if(sqlstmt!=null)
						{
							sqlstmt.close();
					        sqlstmt = null;
						}
					}
			        
			    }
					
			    rs.close();
			    rs = null;
			    stm.close();
			    stm = null;
			    conn.close();
			    conn = null;
			    
			  //创建错误报告文件
			  createResultFile(zqgsdm,sjrq,check_rtn,flag);
			  
			  if(flag)
			  {
				  com.szkingdom.Import_Data.importDate(zqgsdm, sjrq, check_rtn);
			  }
		   
				 
			} catch (ClassNotFoundException e) {
				logger.debug("创建数据库连接类出错！");
				
			}catch (SQLException e)
			{
				logger.debug("连接数据库出错！");
			}
			
			
			
			
		}catch (Exception e)
		{
			logger.debug("读取配置文件出错");
		}
			
		
	}
	/**
	 * 将resultset对象变为list集合,避免resultset循环一次之后关闭,导致再次访问得不到数据
	 * 
	 * @param  查询的结果集
	 * @return 返回一个list 集合。
	 * */
	static List    getResult(ResultSet rs)
	{
		List result = new ArrayList();
		
		ResultSetMetaData rsmd = null;
		int cols = 0;
		try {
			while(rs.next()){
				HashMap row = new HashMap();
				rsmd = rs.getMetaData();
				cols = rsmd.getColumnCount();
				for(int i=1;i<=cols;i++)
				{
					if (rs.getObject(i) != null)
		        	{
						row.put(rsmd.getColumnName(i).toLowerCase(), rs.getObject(i).toString());
		        	}
				}				
				result.add(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.debug("处理结果集时，连接数据库出错！");
		}
		return result;
	}
	
	/**
	 *用于生成与券商的交互文件，该方法只生成两个情况下的交互文件
	 *第一种：券商所有文件都通过所有校验，生成成功文件。
	 *第二种：券商在数据到了备用数据库，由于不满足数据的校验规则，生成错误的excel文件
	 **/
	
	
	public static void createResultFile(String  zqgsdm,String sjrq,String milltime,boolean flag)
	{

		
		if(luceneConfig == null)
		{
			luceneConfig = XMLUtil.loadFile("FileConfig.xml");
			
			Node node = luceneConfig.selectSingleNode("//root/path");
			path_mulu = node.getText();
			
			node =  luceneConfig.selectSingleNode("//root/driverclass");
			driverclass = node.getText();
			
			node =  luceneConfig.selectSingleNode("//root/url");
			url  = node.getText();
			
			node = luceneConfig.selectSingleNode("//root/userid");
			userid = node.getText();
			
			node = luceneConfig.selectSingleNode("//root/password");
			password = node.getText();
		}
		
		
		Connection conn = null;
		Statement stm = null;
		ResultSet rs = null;

		String sql = "";
		List nodelist = null;
		Iterator it = null;
		String reportfile ="";
		nodelist = luceneConfig.selectNodes("//root/reportfile");
		it = nodelist.iterator();
		while(it.hasNext())
		{
			Element pathelement = (Element)it.next();
			reportfile = (String)pathelement.getText();
		}
		
		CreateFile(reportfile+zqgsdm+"/");
		
		Date   report_file_date = new Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		File  report_file = null;
    	
		try {
			
			Class.forName(driverclass);
		    conn = DriverManager.getConnection(url, userid, password);
		    sql = "select t1.check_name,t.check_rs_mou from t_Krms_Check_Result t,t_krms_check_dict t1 where t.check_id=t1.check_id and t.check_rtn ='"+milltime+"' and t.zqgsdm ='"+zqgsdm+"' and t.check_rs ='02'";
			stm = conn.createStatement();
		    rs = stm.executeQuery(sql);
		    logger.debug("*********************************");
			logger.debug("***********开始生成报告文件***********");
			logger.debug("*********************************");
			logger.debug(sql);
			/*以前用系统当前时间为交互文件命名
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
            Date   report_file_date = new Date();
			String mdate = sdf.format(report_file_date);
			*/
			
			
			logger.debug(String.valueOf(conn.getAutoCommit()));
			if(flag)
			{
				
				report_file = new File(reportfile+zqgsdm+"/"+zqgsdm+"_"+sjrq+"_SUCCESS.txt");
				if(report_file.exists())
				{
					report_file = null;
					report_file = new File( reportfile+zqgsdm+"/"+zqgsdm+"_"+sjrq+"_SUCCESS_"+report_file_date.getTime()+".txt");
				}
			}
			else 
			{
				report_file = new File(reportfile+zqgsdm+"/"+zqgsdm+"_"+sjrq+"_FAILURE.xls");
				if(report_file.exists())
				{
					report_file = null;
					report_file = new File( reportfile+zqgsdm+"/"+zqgsdm+"_"+sjrq+"_FAILURE_"+report_file_date.getTime()+".xls");
				}
			}
			
			
			if(flag)
			{
				FileOutputStream fos = new FileOutputStream(report_file);	
				BufferedWriter bw   = new BufferedWriter(new OutputStreamWriter(fos));
				String s =zqgsdm+"  "+sjrq+"上报数据已经通过检验成功。";
				bw.write(s+"\n");
				bw.close();
				fos.close();
				
				//处理交互日志信息
				String sqlsuccess = "insert into rzrq.T_SJZX_RZRQ_JKSJ_JHJG(SJRQ,ZQGSDM,JYRQ,CHECK_RTN,JYJG,TZBZ,sfsb) " +
	    		"values(?,?,?,?,?,?,?)";
				PreparedStatement stmsuccess = null;
				stmsuccess = conn.prepareStatement(sqlsuccess);
				stmsuccess.setString(1, sjrq);
				stmsuccess.setString(2, zqgsdm);
				stmsuccess.setString(3,sdf.format(report_file_date));
				stmsuccess.setLong(4,Long.parseLong(milltime));
				stmsuccess.setString(5, "01");
				stmsuccess.setString(6, "01");
				stmsuccess.setString(7, "01");
				stmsuccess.execute();
				logger.debug(String.valueOf(conn.getAutoCommit()));
				//conn.commit();
				stmsuccess.close();
				stmsuccess = null;
				
			}
			else
			{
				//excel页数
				int excel_count = 0;
				WritableWorkbook book= Workbook.createWorkbook(report_file); 
				//生成名为"错误报告"的工作表，参数0表示这是第一页 
	            WritableSheet sheet=book.createSheet("错误报告",0); 
	            WritableFont font = new WritableFont(WritableFont.TAHOMA, 9, WritableFont.BOLD);// 定义字体   
	            font.setColour(Colour.BLACK);// 字体颜色   
	            WritableCellFormat wc = new WritableCellFormat(font);   
	            wc.setAlignment(Alignment.CENTRE); // 设置居中  
	            wc.setBorder(Border.ALL, BorderLineStyle.THIN); // 设置边框线   
	            //wc.setBackground(jxl.format.Colour.BLUE_GREY); // 设置单元格的背景颜色   
	             
	            ResultSetMetaData rsmd = null;
	            int cols = 0;
	            int rows = 1;
	            Label lable  = null;
	            
	            //设置表头
	            
	            lable = new Label(0,0,"检验名称",wc);
	            sheet.addCell(lable); 
	            
	            lable = new Label(1,0,"描述",wc);
	            sheet.addCell(lable);
	              
	            
	            while(rs.next()){
					//s = rs.getString("check_rs_mou");
					//bw.write(s+"\n");
					
	            	HashMap row = new HashMap();
					rsmd = rs.getMetaData();
					cols = rsmd.getColumnCount();
					for(int i=1;i<=cols;i++)
					{
						if (rs.getObject(i) != null)
			        	{
							sheet.setColumnView(i-1, rs.getObject(i).toString().length()*2);
							lable =new Label(i-1,rows%50000,rs.getObject(i).toString(),wc);
							//row.put(rsmd.getColumnName(i).toLowerCase(), rs.getObject(i).toString());
			        	}
						 //将定义好的单元格添加到工作表中 
						sheet.addCell(lable);  
					}		
					        
				   
					rows++;
					
					if(rows%50000==0)
					{
						excel_count++;
						sheet = book.createSheet("错误报告"+excel_count,excel_count); 
					}
					
				}
	            book.write();
	            book.close();
	            
	            rs.close();
				rs =null;
				stm.close();
				stm = null;
				
				//处理交互日志信息
				String sqlfailure = "insert into rzrq.T_SJZX_RZRQ_JKSJ_JHJG(SJRQ,ZQGSDM,JYRQ,CHECK_RTN,JYJG,TZBZ,sfsb) " +
	    		"values(?,?,?,?,?,?,?)";
				PreparedStatement stmfailure = null;
				stmfailure = conn.prepareStatement(sqlfailure);
				stmfailure.setString(1, sjrq);
				stmfailure.setString(2, zqgsdm);
				stmfailure.setString(3,sdf.format(report_file_date));
				stmfailure.setLong(4,Long.parseLong(milltime));
				stmfailure.setString(5, "02");
				stmfailure.setString(6, "01");
				stmfailure.setString(7, "01");
				stmfailure.execute();	    
				stmfailure.close();
				stmfailure = null;
			}
			
			
			
			conn.close();
			conn = null;
			//生成检验错误报告在fdep平台上的ok标志
			if(report_file!=null)
			{
	            File okfile = new File(report_file.getPath()+".ok");
				
				
				
				try {
					FileOutputStream fis = new FileOutputStream(okfile);
					fis.close();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.debug("在生成检验报告时,文件"+report_file.getName()+"没有找到!");
			LogUtils.debugStackTrace(e.getStackTrace());
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug("在生成检验报告时,写"+report_file.getName()+"文件出错!");
			LogUtils.debugStackTrace(e.getStackTrace());
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			logger.debug("在生成检验报告时,写"+report_file.getName()+"文件出错!");
			LogUtils.debugStackTrace(e.getStackTrace());
		} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
			logger.debug("在写日志时,数据库连接失败!");
			LogUtils.debugStackTrace(e.getStackTrace());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.debug("在写日志时,操作数据库失败!");
			LogUtils.debugStackTrace(e.getStackTrace());
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