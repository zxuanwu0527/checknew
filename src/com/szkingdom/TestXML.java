package com.szkingdom;


import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.CallableStatement;
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


/** Index all text files under a directory. */
public class TestXML
{
	private static Logger logger = Logger.getLogger(TestXML.class);

	private TestXML()
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
	private static String   driverclass = null;
	private static String   url = null;
	private static String   userid = null;
	private static String   password = null;
	private static String   path_mulu = null;
	private static String   check_rtn  = String.valueOf(new Date().getTime());
	private static int      count_file = 0 ;//用于记录检测备用库数据顺序
	
	static void init()
	{	
		
		
		Connection conn = null;
		
		try
		{
			luceneConfig = XMLUtil.loadFile("FileConfig.xml");//用于配置数据库参数的
			luceneConfig1 = XMLUtil.loadFile("xml/FileOrder.xml");//用于配置数据库参数的
			
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
		    
			Class.forName(driverclass);
		    conn = DriverManager.getConnection(url, userid, password);	    	
	    	Node node1 = luceneConfig1.selectSingleNode("//order/type[@id='data']");
	
	    	if(node1 == null)
	    	{
	    		logger.debug("配置文件没有配置数据检测,请检查配置文件是否需要检测!");
	    	}
	    	else
	    	{
	    		String  flag ="";
	    		List nodes = node1.selectNodes("dict");
	    		
	    		if(nodes.size()==0)
	    		{
	    			logger.debug("数据检测中,没有配置步骤节点!");
	    		}
	    		else
	    		{
		    		logger.debug("*********************************");
					logger.debug("***********开始检测备用库数据***********");
					logger.debug("*********************************");
		    		
		    		for(int j = 0;j<nodes.size();j++)
			    	{
				        if(nodes.get(j)!=null)
				        {
				    		Element element = (Element) nodes.get(j);
					    
				    		List pros = element.selectNodes("pro");
				    		if(pros.size() == 0)
				    		{
				    			logger.debug("数据检测中,第"+element.attributeValue("order")+"步骤下存储过程节点没有配置,请检测配置文件是否需要配置!");
				    		}
				    		else
				    		{
					    		logger.debug("正在检测第"+element.attributeValue("order")+"步骤,请稍后!");
					    		flag = CheckData(conn,pros,element.attributeValue("order"));
					    		if(flag.equals("0"))
					    		{
					    			logger.debug("检测第"+element.attributeValue("order")+"步骤失败!");
					    
					    		}
					    		
					    		if(flag.equals("1"))
					    		{
					    			logger.debug("检测第"+element.attributeValue("order")+"步骤成功!");
					    		}
					    		
				    		}
				    		
				        }
				    	
			    	}
		    		
	    		}
	    	}
			conn.close();
			conn = null;
		   
			
		} catch (ClassNotFoundException e) {
			logger.debug("创建数据库连接类出错！");
			
		}catch (SQLException e)
		{
			logger.debug("连接数据库出错！");
		}
		catch (Exception e)
		{
			logger.debug("读取配置文件出错");
		}
		finally
		{		
			DbUtil.closeConnection(conn);
			
		}	
			
			
			/*

			Reader reader = Resources.getResourceAsReader("SqlMapConfig.xml");
	
			sqlMapper = SqlMapClientBuilder.buildSqlMapClient(reader);
			reader.close();
			List list = sqlMapper.queryForList("tanlun.t_accessory.select");
			
			
			Class.forName(driverclass);
			Connection conn = DriverManager.getConnection(url, userid, password);
			
			String sSql = "{call " + userid + "." + sProcName + "(";
			sSql = 
			CallableStatement sqlstmt = conn.prepareCall(sSql);
		    sqlstmt.registerOutParameter(1, Types.INTEGER);
		    sqlstmt.registerOutParameter(2, Types.VARCHAR);
		    sqlstmt.setString(i++, (String)oParam);
		    sqlstmt.execute();
		    int iErrorCode = sqlstmt.getInt(1);
	        String sErrorMsg = sqlstmt.getString(2);
	        
		        
		    sqlstmt.close();
	   
          */
			
		
			
	}
	static  String CheckData(Connection conn,List pros,String i)
	{
		
		CallableStatement sqlstmt = null;
		String sSql = "";
		String flag  ="0";
	
	
	    
		for(int m = 0;m<pros.size();m++)
    	{
		   
			System.out.println(pros.size());
			if(pros.get(m)!=null)
		   {
	    		
			   	
			   
			    logger.debug("***************"+(++count_file)+"****************");
			   	Element elementpros = (Element)(pros.get(m));
			   	String  proname = elementpros.attributeValue("name");
			   	List params = elementpros.selectNodes("param");			   
	
		    	sSql ="{call " + userid + "." + proname + "(";
		    	for(int j=0;j<params.size();j++)
		    	{
		    		sSql += "?,";
					
				}
			
		    	sSql += "?,?,?)}";
		    	logger.debug(sSql);
				try{
					sqlstmt = conn.prepareCall(sSql);
			    
					for(int f=0;f<params.size();f++)
			    	{
			    		Element elementparam =(Element)(params.get(f));
			    		String paramvalue = elementparam.getText();
						sqlstmt.setString(f+1, paramvalue);
					}
					
					sqlstmt.setString(params.size()+1, String.valueOf(check_rtn));
					sqlstmt.setString(params.size()+2, String.valueOf(count_file));
			        sqlstmt.registerOutParameter(params.size()+3, Types.VARCHAR);
			        sqlstmt.execute();
			        String proflag = sqlstmt.getString(params.size()+3);
		    		if(proflag.equals("0"))
		    		{
		    			flag ="0";
	  			    	logger.debug("执行存储过程"+proname+"异常!");
		    		}
		    		
		    		if(proflag.equals("1"))
		    		{
		    			
		    			flag = "1";
		    			logger.debug("执行存储过程"+proname+"成功!");
		    		}
		    		
		    		if(proflag.equals("2"))
		    		{
		    			flag ="0";
		    			logger.debug("执行存储过程"+proname+"失败!");
		    		}
			        
		    		sqlstmt.close();
				    sqlstmt = null;
				}catch (SQLException e)
				{
					
					logger.debug("调用存储过程"+proname+"出错");
					return "0";
				}
				
		   }
	    }
		return flag;
		   
	}

	/**
	 * 将resultset对象变为list集合
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