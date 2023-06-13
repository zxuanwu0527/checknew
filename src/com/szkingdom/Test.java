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

//����Excel���� 
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
		logger.debug("***********��ʼ***********");
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
	//private static int      count_file = 0 ;//���ڼ�¼��ⱸ�ÿ�����˳��
	//private static Date    check_rtn = new Date();
	public static void init(String zqgsdm,String sjrq,String check_rtn)
	{	
		
		int      count_pro = 0 ;//���ڼ�¼��ⱸ�ÿ����ݴ洢����˳��
		boolean  flag = true ;//���ڼ�¼���鱸�����ݿ��Ƿ��д�"1"��ʾ�ɹ���"0"��ʾ���ɹ�
		
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
			
			logger.debug("----------------------------------�����ļ�·��="+path_mulu+"-------------------------------");
			logger.debug("���ݿ�������������:");
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
				logger.debug("***********��ʼ��ⱸ�ÿ�����***********");
				logger.debug("*********************************");
			    
				//���鱸�����ݿ�����
				for(int i=0;i<result.size();i++)
			    {
			    	logger.debug("***************"+(++count_pro)+"****************");
			    	HashMap pro = (HashMap)result.get(i);
			    	logger.debug("�洢�������ƣ�"+pro.get("name")+";������"+pro.get("param1"));
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
				        
				        
				        
				        //�����ж��Ƿ���Ҫ������������Ĵӱ��ÿ⵽���Ŀ�Ĵ洢����
				        
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
				        
				        
				        
				        
				        //�����ж��Ƿ���Ҫ������������Ĵӱ��ÿ⵽���Ŀ�Ĵ洢����
				        
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
				        
				        
				        
				        
				        
				        //�����ж��Ƿ���Ҫ������������Ĵӱ��ÿ⵽���Ŀ�Ĵ洢����
				        
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
						logger.debug("����洢���̳���");
					}
					finally
					{
						
						//������α������������ݿ�����
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
			    
			  //�������󱨸��ļ�
			  createResultFile(zqgsdm,sjrq,check_rtn,flag);
			  
			  if(flag)
			  {
				  com.szkingdom.Import_Data.importDate(zqgsdm, sjrq, check_rtn);
			  }
		   
				 
			} catch (ClassNotFoundException e) {
				logger.debug("�������ݿ����������");
				
			}catch (SQLException e)
			{
				logger.debug("�������ݿ����");
			}
			
			
			
			
		}catch (Exception e)
		{
			logger.debug("��ȡ�����ļ�����");
		}
			
		
	}
	/**
	 * ��resultset�����Ϊlist����,����resultsetѭ��һ��֮��ر�,�����ٴη��ʵò�������
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
	
	/**
	 *����������ȯ�̵Ľ����ļ����÷���ֻ������������µĽ����ļ�
	 *��һ�֣�ȯ�������ļ���ͨ������У�飬���ɳɹ��ļ���
	 *�ڶ��֣�ȯ�������ݵ��˱������ݿ⣬���ڲ��������ݵ�У��������ɴ����excel�ļ�
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
			logger.debug("***********��ʼ���ɱ����ļ�***********");
			logger.debug("*********************************");
			logger.debug(sql);
			/*��ǰ��ϵͳ��ǰʱ��Ϊ�����ļ�����
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
				String s =zqgsdm+"  "+sjrq+"�ϱ������Ѿ�ͨ������ɹ���";
				bw.write(s+"\n");
				bw.close();
				fos.close();
				
				//��������־��Ϣ
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
				//excelҳ��
				int excel_count = 0;
				WritableWorkbook book= Workbook.createWorkbook(report_file); 
				//������Ϊ"���󱨸�"�Ĺ���������0��ʾ���ǵ�һҳ 
	            WritableSheet sheet=book.createSheet("���󱨸�",0); 
	            WritableFont font = new WritableFont(WritableFont.TAHOMA, 9, WritableFont.BOLD);// ��������   
	            font.setColour(Colour.BLACK);// ������ɫ   
	            WritableCellFormat wc = new WritableCellFormat(font);   
	            wc.setAlignment(Alignment.CENTRE); // ���þ���  
	            wc.setBorder(Border.ALL, BorderLineStyle.THIN); // ���ñ߿���   
	            //wc.setBackground(jxl.format.Colour.BLUE_GREY); // ���õ�Ԫ��ı�����ɫ   
	             
	            ResultSetMetaData rsmd = null;
	            int cols = 0;
	            int rows = 1;
	            Label lable  = null;
	            
	            //���ñ�ͷ
	            
	            lable = new Label(0,0,"��������",wc);
	            sheet.addCell(lable); 
	            
	            lable = new Label(1,0,"����",wc);
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
						 //������õĵ�Ԫ����ӵ��������� 
						sheet.addCell(lable);  
					}		
					        
				   
					rows++;
					
					if(rows%50000==0)
					{
						excel_count++;
						sheet = book.createSheet("���󱨸�"+excel_count,excel_count); 
					}
					
				}
	            book.write();
	            book.close();
	            
	            rs.close();
				rs =null;
				stm.close();
				stm = null;
				
				//��������־��Ϣ
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
			//���ɼ�����󱨸���fdepƽ̨�ϵ�ok��־
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
			logger.debug("�����ɼ��鱨��ʱ,�ļ�"+report_file.getName()+"û���ҵ�!");
			LogUtils.debugStackTrace(e.getStackTrace());
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug("�����ɼ��鱨��ʱ,д"+report_file.getName()+"�ļ�����!");
			LogUtils.debugStackTrace(e.getStackTrace());
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			logger.debug("�����ɼ��鱨��ʱ,д"+report_file.getName()+"�ļ�����!");
			LogUtils.debugStackTrace(e.getStackTrace());
		} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
			logger.debug("��д��־ʱ,���ݿ�����ʧ��!");
			LogUtils.debugStackTrace(e.getStackTrace());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.debug("��д��־ʱ,�������ݿ�ʧ��!");
			LogUtils.debugStackTrace(e.getStackTrace());
		}
	}
	
	/**
	    * ���ڴ����ļ��У���������ڣ��򴴽����ļ���
	    * @param path 
	    */
   static  void   CreateFile(String   path)
   { 
		
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