package com.szkingdom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.szkingdom.a.XMLUtil;

public class Test_File {

	/**
	 * @param args
	 */
	
	private static Logger logger = Logger.getLogger(Test_File.class);
	
	private static org.dom4j.Document luceneConfig;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		
		luceneConfig = XMLUtil.loadFile("FileConfig.xml");//用于配置数据库参数的
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
		
		CreateFile(reportfile+"10270000/");
		
    	try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
		    conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:krcs", "krcs", "krcs");
		    sql = "select t3.* from T_KRMS_CHECK_DICT t1,T_KRMS_check_execute t2,T_KRMS_check_detail_config t3 where t1.check_id = t2.check_id and t2.name = t3.name order by t1.CHECK_TYPE,t1.CHECK_DETAIL_TYPE,t2.Run_orders";
			stm = conn.createStatement();
		    rs = stm.executeQuery(sql);
		    Date check_rtn = new Date();
		    logger.debug("*********************************");
			logger.debug("***********开始检测备用库数据***********");
			logger.debug("*********************************");
			System.out.print(new Date());
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
            String mdate = sdf.format(new Date());
			System.out.print(new Date());
			
			FileOutputStream fos = new FileOutputStream(new File(reportfile+"10270000/report_"+mdate+".txt"));	
			BufferedWriter bw   = new BufferedWriter(new OutputStreamWriter(fos));
			String s ="";
			while(rs.next()){
				s = rs.getString("param1");
				bw.write(s+"\n");
			}
			bw.close();
			rs.close();
			rs =null;
			stm.close();
			stm = null;
			conn.close();
			conn = null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("处理结果集时，连接数据库出错！");
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
			       file.mkdir();       
			       System.out.println("have   made   a   dir   ！"   );       
			    }
				s = s.replaceFirst("/", ".");
					
				j = s.indexOf('/');
				
			}
	             
	  }    



}
