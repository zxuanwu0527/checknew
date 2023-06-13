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
	 * ���ݺϷ��ļ������ݲ��뱸�����ݿ�
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
			 * �Ƿ�����������ȫ��������ΪZ��ȫ��ΪQ��
			 */
			dataflag  = filename.split("_")[5].substring(0, filename.split("_")[5].length()-4); 
			
			logger.debug(dataflag);
			
			String sql ="insert into rzrq.sc_"+tablename+"_"+zqgsdm+"_"+dataflag+"  values(";
			
			String s = "";
			
			int i = 0 ; //���ڼ�¼��ȡ������,ÿ�α���1000��
			
		
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
			
			//��������,ÿ��1000��
		    fis1 = new FileInputStream(file);
			
			br1  = new BufferedReader(new InputStreamReader(fis1,encoding)); 
			
		    pst = conn.prepareStatement(sql);
		    
			//���ڴ洢�ռ�¼��־
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
			logger.debug("���뱸�����ݿ����!");
			if(pst!=null)
			{
				try {
					pst.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					logger.debug("�ر����ݿ��ѯ�ɹ�!");
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
					logger.debug("�رյ�ǰ��ȡ���ļ��ɹ�!");
				}
			}
		}
	}
	
	/**
	 * ���������ļ��ɹ���ɾ�����ֱ������ݿ��ȯ�̵����нӿ���ʷ����
	 * @param file
	 */
	public   static  String delete_data(Connection conn,File file,String zqgsdm)
	{
		CallableStatement sqlstmt = null;
		String sql = "";
		String pro_flag ="1";
	
	    logger.debug("*********************************");
		logger.debug("***********��ʼɾ��"+zqgsdm+"���ÿ�����***********");
		logger.debug("*********************************");
	    
		//���鱸�����ݿ�����
	
		sql ="{call krcs.proc_check_delete_byksj(?,?)}";
		logger.debug(sql);
		try{
			sqlstmt = conn.prepareCall(sql);
			sqlstmt.setString(1, zqgsdm);
			sqlstmt.registerOutParameter(2, Types.VARCHAR);
			sqlstmt.execute();
			logger.debug(sqlstmt.getString(2));
			pro_flag = sqlstmt.getString(2);
	        
	        //�����ж��Ƿ���Ҫ������������Ĵӱ��ÿ⵽���Ŀ�Ĵ洢����
			if(sqlstmt!=null)
			{
				sqlstmt.close();
				sqlstmt = null;
			}
		}catch (Exception e)
		{
			logger.debug("����洢���̳���");
			LogUtils.debugStackTrace(e.getStackTrace()); 
			return "0";
		}
		return 	pro_flag;
	}
    


}
