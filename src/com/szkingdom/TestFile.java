package com.szkingdom;


import org.apache.log4j.Logger;
import org.dom4j.Element;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/** Index all text files under a directory. */
public class TestFile
{
	private static Logger logger = Logger.getLogger(TestFile.class);

	private TestFile()
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
	private static String   driverclass = null;
	private static String   url = null;
	private static String   userid = null;
	private static String   password = null;
	private static String   path_mulu = null;
	private static String   check_rtn  = String.valueOf(new Date().getTime());
	private static int      count_file = 0 ;//���ڼ�¼����ļ�˳��
	static void init()
	{	
		
		
		try
		{
			luceneConfig = XMLUtil.loadFile("FileConfig.xml");
			List nodelist = null;
			Iterator it = null;
			
			nodelist = luceneConfig.selectNodes("//root/path");
			it = nodelist.iterator();
			while(it.hasNext())
			{
				Element pathelement = (Element)it.next();
				path_mulu = (String)pathelement.getText();
			}
			
			
			nodelist = luceneConfig.selectNodes("//root/driverclass");
			it = nodelist.iterator();
			while(it.hasNext())
			{
				Element driverclasselement = (Element)it.next();
				driverclass = (String)driverclasselement.getText();
			}
			
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
				System.out.println("utf�������ݿ����������");
				
			}catch (SQLException e)
			{
				System.out.println("�������ݿ����");
			}
			
			//ѭ���ļ����µ��ļ�
			File file = new File(path_mulu);
			
			if(file.exists()){
		
				logger.debug("*********************************");
				logger.debug("***********��ʼ����ļ���***********");
				logger.debug("*********************************");
				CheckFile(file,conn);
			}
	        else
	        {
	        	logger.debug("û���ҵ��ļ�Ŀ¼��"+path_mulu);
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

			conn.close();
			conn=null;
			
		
		}catch (Exception e)
		{
			System.out.println("��ȡ�����ļ�����");
		}
		finally
		{
			
		}
		
		
	}
	/**
	 * ����ļ����Ƿ�Ϸ�
	 * @param file
	 * @param conn
	 */
	static void CheckFile(File file,Connection conn){
		
		if(file.isDirectory()){
	              File[] fileList = file.listFiles();
	              for (int i = 0; i < fileList.length; i++) {
	            	  	String filePath = fileList[i].getPath();
	            	  	
	            	  	CheckFile(new File(filePath),conn);
	            	  	
	    	  	  }
	    	  	
	    }

   
	    if(file.isFile())
	    {
	    	logger.debug("***************"+(++count_file)+"****************");
	    	String  filename = file.getName();
	    	logger.debug("�ļ�����"+filename);
	    	/*
	    	String[] params = (filename.substring(0, filename.length()-4)).split("_");
	    	if(params.length!=7)
	    	{
	    		logger.debug("�ļ�"+filename+"���Ϸ������ݲ���������7!");
	    	}
	    	else
	    	{
	    	*/
	    	try {
	    		
	    		String sql = "{call " + userid + ".proc_check_filename(?,?,?)}";	    		
	    		CallableStatement cstm = conn.prepareCall(sql);
	    		logger.debug(sql);
	    		/*
	    		for(int i=1;i<=7;i++)
	    		{
	    			cstm.setString(i, params[i-1]);
	    		}*/
	    		cstm.setString(1,filename.substring(0, filename.length()-4));
	    		cstm.setString(2,check_rtn);
	    		cstm.registerOutParameter(3, Types.VARCHAR);
	    		cstm.execute();
	    		String flag = cstm.getString(3);
	    		
	    		if(flag.equals("0"))
	    		{
	    			logger.debug("ִ�д洢���̳���!");
	    		}
	    		
	    		if(flag.equals("2"))
	    		{
	    			logger.debug("�ļ�"+filename+"���ļ������Ϸ�");
	    		}
	    		
	    		if(flag.equals("1"))
	    		{
	    			logger.debug("�ļ�"+filename+"���ļ����Ϸ�");
	    			CheckDate(file,conn);
	    		}
	    		cstm.close();
	    		cstm = null;
	    		
	    		
	    	} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.debug("�����ļ�"+file.getName()+"ʱ,���ô洢����proc_check_filename����!");
			}
	    	
	    }
	 
        
        
        
    }
	/**
	 * ����ļ�ͷ�ͼ�¼���Ƿ�Ϸ����ɹ������ļ����������Ƿ�Ϸ�
	 * @param file  
	 * @param conn
	 */
	static void CheckDate(File file,Connection conn)
	{
		try
		{
			RandomAccessFile br = new RandomAccessFile(file.getPath(),"r");
			//FileInputStream fis = new FileInputStream(file);
			//BufferedReader br   = new BufferedReader(new InputStreamReader(RandomAccessFile,"utf-8"));
			String s ="";
			int recordcount = 0;
			int count = 0;
			int x = 0;
	  		while((s = br.readLine())!=null)
	  		{
	  			//s =new String(s.getBytes(),"utf-8");
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
			  			try {
			  				String  filename = file.getName();
			  				String[] params  = filename.split("_");
				    		String sql = "{call " + userid + ".proc_check_file_cols(?,?,?,?,?,?)}";	    		
				    		CallableStatement cstm = conn.prepareCall(sql);
				    		logger.debug(sql);
				    		
				    		cstm.setString(1, params[4]);
				    		cstm.setString(2, String.valueOf(cols));
				    		cstm.setString(3, String.valueOf(x));
				    		cstm.setString(4, params[1]);
				    		cstm.setString(5, check_rtn);				    		
				    		cstm.registerOutParameter(6, Types.VARCHAR);
				    		
				    		cstm.execute();
				    		String flag = cstm.getString(6);
				    		
				    		if(flag.equals("0"))
				    		{
				    			logger.debug("�ļ�"+filename+"�ĵ�"+x+"��ִ�д洢����krms_check_file_cols����!");
				    		}
				    		
				    		if(flag.equals("2"))
				    		{
				    			logger.debug("�ļ�"+filename+"�ĵ�"+x+"�м�¼������ƥ��");
				    		}
				    		
				    		if(flag.equals("1"))
				    		{
				    			logger.debug("�ļ�"+filename+"�ĵ�"+x+"�м�¼����ƥ��");
				    			insert_data(conn,(s.split("0x0A")[0]).split("\\|"),params[4]);
				    		}
				    		cstm.close();
				    		cstm = null;
				    		
				    	} catch (SQLException e) {
							// TODO Auto-generated catch block
							logger.debug("�����ļ�"+file.getName()+"ʱ,���ô洢����proc_check_file_cols����!");
						}
				    	
			  		}
		  		}
	  		}
	  		else
	  		{
	  			logger.debug("�ļ�"+file.getName()+"��,�ļ�ͷ�ͼ�¼����ƥ��,Ӧ��"+recordcount+"��,ʵ��"+count+"��!");
	  		}
	  		br.close();
		}catch(Exception e)
		{
			logger.debug("��ȡ�ļ�"+file.getName()+"����");
		}
		
	}
	/**
	 * ���ݺϷ��ļ�һ�����Ĳ�������
	 * @param params
	 */
	static void insert_data(Connection conn,String[] params,String tablename)
	{
		try
		{
			String sql ="insert into rpt_"+tablename+"_temp values(";
			for(int j=0;j<params.length;j++)
	    	{
	    		sql += "?";
				
				if (j < params.length-1)
				{
					sql += ",";
				}
				//System.out.println(params[j].getBytes("utf-8")) ;
			}
			
			sql += ")";
			logger.debug(sql);
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, new String(params[0].getBytes("iso-8859-1"),"utf-8"));
			pst.setString(2, new String(params[1].getBytes("iso-8859-1"),"utf-8"));
			pst.setString(3, new String(params[2].getBytes("iso-8859-1"),"utf-8"));
			pst.setString(4, new String(params[3].getBytes("iso-8859-1"),"utf-8"));
			pst.setString(5, new String(params[4].getBytes("iso-8859-1"),"utf-8"));
			pst.execute();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("����!");
		}
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