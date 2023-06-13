package com.szkingdom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
public class Insert_Data {


	private static Logger logger = Logger.getLogger(Insert_Data.class);
	

    
	/**
	 * 根据合法文件将数据插入备用数据库
	 * @param file
	 */
	public   static  void insert_data(Connection conn,File file,String encoding,int cols)
	{
		
		String tablename = "";
		String zqgsdm ="";
		String dataflag="";
		String[] params  = null;
		FileInputStream fis1 = null;
		
		BufferedReader  br1  = null;
		
		PreparedStatement pst = null;
		
		try
		{
			String filename = file.getName();
					
			tablename = filename.split("_")[4];
			
			zqgsdm   = filename.split("_")[1];
			/*
			 * 是否是增量还是全量，增量为Z，全量为Q。
			 */
			dataflag  = filename.split("_")[5].substring(0, filename.split("_")[5].length()-4); 
			
			logger.debug(dataflag);
			
			String sql ="insert into rzrq.sc_"+tablename+"_"+zqgsdm+"_"+dataflag+"  values(";
			
			String s = "";
			
			int i = 0 ; //用于记录读取的条数,每次保存1000条
			
		
			for(int j=0;j<cols;j++)
			{
				sql += "?";
				
				if (j < cols-1)
				{
					sql += ",";
				}
				//System.out.println(params[j].getBytes("utf-8")) ;
			}
		
		    sql += ")";		
		    logger.debug(sql);
			
			//插入数据,每次1000条
		    fis1 = new FileInputStream(file);
			
			br1  = new BufferedReader(new InputStreamReader(fis1,encoding)); 
			
		    pst = conn.prepareStatement(sql);
		    
			//用于存储空记录标志
		    int   flag = 0;
		    
		    while((s = br1.readLine())!=null)
			{
				if(s.indexOf("RecordCount=")>0)
				{
					int w=s.indexOf("RecordCount=");
					String str_2 = s.substring(w+12);
					flag = Integer.parseInt(str_2);
					if(flag==0)
					{
						break;
					}
					else
					{
						continue;
					}
				
				}
				else
				{
					/*
					if(s.indexOf("0x0A")<0)
					{
						continue;
					}
					
					params = (s.split("0x0A")[0]).split("\\|");
					*/
					params = s.split("\\|");
					if(params.length>=cols)
					{
						for(int m=0;m<cols;m++)
						{
						    pst.setString(m+1, params[m]);	
						}
						pst.addBatch();
					}
					else
					{
						int k=0;
						while(k<cols)
						{
							
							if(k<params.length)
							{							
								pst.setString(k+1, params[k]);	
							}
							else
							{
								pst.setString(k+1, null);
							}
							k++;
						}
						
						pst.addBatch();
					}
					i++;
				}
				
				if(i%1000==0)
				{
					pst.executeBatch();
				}
			
			}
			if((i%1000!=0)&&(flag!=0))	
			{
				pst.executeBatch();
			}
			br1.close();
			fis1.close();
			pst.close();
			pst = null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtils.debugStackTrace(e.getStackTrace()); 
			logger.debug("插入备用数据库出错!");
			if(pst!=null)
			{
				try {
					pst.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					logger.debug("关闭数据库查询成功!");
				}
				pst = null;
			}
			if(br1!=null)
			{
				try {
					br1.close();
					fis1.close();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					logger.debug("关闭当前读取的文件成功!");
				}
			}
		}
	}
	
	/**
	 * 检验所有文件成功后，删除该轮备用数据库该券商的所有接口历史数据
	 * @param file
	 */
	public   static  String delete_data(Connection conn,File file,String zqgsdm)
	{
		CallableStatement sqlstmt = null;
		String sql = "";
		String pro_flag ="1";
	
	    logger.debug("*********************************");
		logger.debug("***********开始删除"+zqgsdm+"备用库数据***********");
		logger.debug("*********************************");
	    
		//检验备用数据库数据
	
		sql ="{call krcs.proc_check_delete_byksj(?,?)}";
		logger.debug(sql);
		try{
			sqlstmt = conn.prepareCall(sql);
			sqlstmt.setString(1, zqgsdm);
			sqlstmt.registerOutParameter(2, Types.VARCHAR);
			sqlstmt.execute();
			logger.debug(sqlstmt.getString(2));
			pro_flag = sqlstmt.getString(2);
	        
	        //用于判断是否需要继续处理下面的从备用库到中心库的存储过程
			if(sqlstmt!=null)
			{
				sqlstmt.close();
				sqlstmt = null;
			}
		}catch (Exception e)
		{
			logger.debug("处理存储过程出错！");
			LogUtils.debugStackTrace(e.getStackTrace()); 
			return "0";
		}
		return 	pro_flag;
	}
    


}
