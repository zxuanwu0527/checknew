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
	private static String   driverclass = null;
	private static String   url = null;
	private static String   userid = null;
	private static String   password = null;
	private static String   path_mulu = null;
	private static String   check_rtn  = String.valueOf(new Date().getTime());
	private static int      count_file = 0 ;//���ڼ�¼��ⱸ�ÿ�����˳��
	
	static void init()
	{	
		
		
		Connection conn = null;
		
		try
		{
			luceneConfig = XMLUtil.loadFile("FileConfig.xml");//�����������ݿ������
			luceneConfig1 = XMLUtil.loadFile("xml/FileOrder.xml");//�����������ݿ������
			
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
			
			logger.debug("----------------------------------�����ļ�·��="+path_mulu+"-------------------------------");
			logger.debug("���ݿ�������������:");
			logger.debug("driverclass="+driverclass);
			logger.debug("url="+url);
			logger.debug("userid="+userid);
			logger.debug("password="+password);
		    
			Class.forName(driverclass);
		    conn = DriverManager.getConnection(url, userid, password);	    	
	    	Node node1 = luceneConfig1.selectSingleNode("//order/type[@id='data']");
	
	    	if(node1 == null)
	    	{
	    		logger.debug("�����ļ�û���������ݼ��,���������ļ��Ƿ���Ҫ���!");
	    	}
	    	else
	    	{
	    		String  flag ="";
	    		List nodes = node1.selectNodes("dict");
	    		
	    		if(nodes.size()==0)
	    		{
	    			logger.debug("���ݼ����,û�����ò���ڵ�!");
	    		}
	    		else
	    		{
		    		logger.debug("*********************************");
					logger.debug("***********��ʼ��ⱸ�ÿ�����***********");
					logger.debug("*********************************");
		    		
		    		for(int j = 0;j<nodes.size();j++)
			    	{
				        if(nodes.get(j)!=null)
				        {
				    		Element element = (Element) nodes.get(j);
					    
				    		List pros = element.selectNodes("pro");
				    		if(pros.size() == 0)
				    		{
				    			logger.debug("���ݼ����,��"+element.attributeValue("order")+"�����´洢���̽ڵ�û������,���������ļ��Ƿ���Ҫ����!");
				    		}
				    		else
				    		{
					    		logger.debug("���ڼ���"+element.attributeValue("order")+"����,���Ժ�!");
					    		flag = CheckData(conn,pros,element.attributeValue("order"));
					    		if(flag.equals("0"))
					    		{
					    			logger.debug("����"+element.attributeValue("order")+"����ʧ��!");
					    
					    		}
					    		
					    		if(flag.equals("1"))
					    		{
					    			logger.debug("����"+element.attributeValue("order")+"����ɹ�!");
					    		}
					    		
				    		}
				    		
				        }
				    	
			    	}
		    		
	    		}
	    	}
			conn.close();
			conn = null;
		   
			
		} catch (ClassNotFoundException e) {
			logger.debug("�������ݿ����������");
			
		}catch (SQLException e)
		{
			logger.debug("�������ݿ����");
		}
		catch (Exception e)
		{
			logger.debug("��ȡ�����ļ�����");
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
	  			    	logger.debug("ִ�д洢����"+proname+"�쳣!");
		    		}
		    		
		    		if(proflag.equals("1"))
		    		{
		    			
		    			flag = "1";
		    			logger.debug("ִ�д洢����"+proname+"�ɹ�!");
		    		}
		    		
		    		if(proflag.equals("2"))
		    		{
		    			flag ="0";
		    			logger.debug("ִ�д洢����"+proname+"ʧ��!");
		    		}
			        
		    		sqlstmt.close();
				    sqlstmt = null;
				}catch (SQLException e)
				{
					
					logger.debug("���ô洢����"+proname+"����");
					return "0";
				}
				
		   }
	    }
		return flag;
		   
	}

	/**
	 * ��resultset�����Ϊlist����
	 * 
	 * @param  ��ѯ�Ľ����
	 * @return ����һ��list ���ϡ�
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
			logger.debug("��������ʱ���������ݿ����");
		}
		return result;
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