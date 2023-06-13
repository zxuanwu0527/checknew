package com.szkingdom;


import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/** Index all text files under a directory. */
public class CopyDataXML
{
	private static Logger logger = Logger.getLogger(CopyDataXML.class);

	private CopyDataXML()
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
	private static int      count_file = 0 ;//���ڼ�¼����ļ�˳��
	static void init()
	{	
		
		
		try
		{
			luceneConfig = XMLUtil.loadFile("FileConfig.xml");//�����������ݿ������
			luceneConfig1 = XMLUtil.loadFile("xml/CopyData.xml");//�����������ݿ������
				
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
				logger.debug("�������ݿ����������");
				
			}catch (SQLException e)
			{
				logger.debug("�������ݿ����");
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
			logger.debug("��ȡ�����ļ�����");
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
	    	
	    	/*
	    	String[] params = (filename.substring(0, filename.length()-4)).split("_");
	    	if(params.length!=7)
	    	{
	    		logger.debug("�ļ�"+filename+"���Ϸ������ݲ���������7!");
	    	}
	    	else
	    	{
	    	*/
	    	
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
					    
				    		List pros = element.selectNodes("pro");
				    		if(pros == null)
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
		    		//�����ⶼ�ɹ��ˣ���ônodes.size()==j
		    		if(nodes.size()==j)
		    		{
		    			CopyFile(conn,file,true);
		    		}
		    		else
		    		{
		    			CopyFile(conn,file,false);
		    		}
			    		
			    		
			        
		    	}
	    	}
		    	
	    }
	 
        
        
        
    }
	/**
	 * ���ڴ������˵��ļ����ļ����ɹ�����copy����Ҫ����ĳɹ��ļ�Ŀ¼�£����
	 * �ļ����ʧ�ܣ�ͬ����copy����������ļ�Ŀ¼���Է���ά����Ա���ҡ�copy��ɺ�ɾ��
	 * ���Ŀ¼�µ�����ļ��������´��ظ����
	 * @param file
	 */
   static  void CopyFile(Connection conn,File file,boolean flag)
   {
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
		
		try {
			
			File   wf = null;
			String path ="";
			if(flag)
			{
				path = successfile+"/"+file.getParentFile().getName()+"/";
				
				wf = new File(successfile+"/"+file.getParentFile().getName()+"/"+file.getName());
				if(wf.exists())
				{
					wf = null;
					wf = new File( successfile+"/"+file.getParentFile().getName()+"/"+filename.substring(0, filename.length()-4)+"_"+new Date().getTime()+".txt");
				}
			}
			else
			{
				path = failfile+"/"+file.getParentFile().getName()+"/";
				wf = new File(failfile+"/"+file.getParentFile().getName()+"/"+file.getName());
				if(wf.exists())
				{
					wf = null;
					wf = new File( failfile+"/"+file.getParentFile().getName()+"/"+filename.substring(0, filename.length()-4)+"_"+new Date().getTime()+".txt");
				}
			}
			CreateFile(path);
			
			
			FileInputStream fis = new FileInputStream(file);
			logger.debug(file.getPath());
			logger.debug(wf.getPath());
			
			FileOutputStream fos = new FileOutputStream(wf);
			
    	
			BufferedReader  br  = new BufferedReader(new InputStreamReader(fis,encoding));
			
			BufferedWriter bw   = new BufferedWriter(new OutputStreamWriter(fos,encoding));
			
			String s ="";
			
			while((s = br.readLine())!=null)
			{
	
				bw.write(s+"\n");
				if(flag)
				{
					if(s.indexOf("RecordCount=")>0)
					{
						continue;
					
					}
					else
					{
						String[] params  = filename.split("_");
						
						insert_data(conn,(s.split("0x0A")[0]).split("\\|"),params[4]);
					}
				}
			}
			
			bw.close();
			br.close();
			
			
		
		
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.debug("�ڱ����ļ�ʱ,"+file.getName()+"û���ҵ�!");
		}catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.debug("�ڱ����ļ�ʱ,���ö�ȡ"+file.getName()+"�ļ������ʽΪ"+encoding+"�쳣!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug("�ڱ����ļ�ʱ,��ȡ"+file.getName()+"�ļ�����!");
		}finally{
			//file.delete();
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
			       file.mkdir();       
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
	static String CheckData(File file,Connection conn,List pros)
	{
		
		
		String filename = file.getName();
		String flag  ="0";
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
		    			    			flag = "1";
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
			for(int k=0;k<params.length;k++)
	    	{
			    pst.setString(k+1, params[k]);
				
	    	}
			pst.execute();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("���뱸�����ݿ����!");
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