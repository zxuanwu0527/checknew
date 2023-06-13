package com.szkingdom;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
/**
 * ���ݿ�ر����ӣ����ݼ��ȳ��ú�����
 * 
 * @author tanlun
 */
public class DbUtil {
	private static final Logger log = Logger.getLogger(DbUtil.class);
	/**
	 * �ر����ݼ�
	 * 
	 * @param rs Ҫ�رյ����ݼ� 
	 */
	public static void closeResultSet(ResultSet rs)
	{
		if (rs != null)
		{
			try {
				rs.close();
			}
			catch(SQLException sqlex)
			{
				log.error("�ر����ݼ�����[������룺" +sqlex.getErrorCode()+"] "+ sqlex.getMessage());
			}
			rs = null;
		}
	}
	/**
	 * �ر�Statement
	 * 
	 * @param stmt Ҫ�رյ�Statement 
	 */
	public static void closeStatement(Statement stmt)
	{
		if (stmt != null)
		{
			try {
				 stmt.close(); 
			}
			catch(SQLException sqlex)
			{
				log.error("�ر�Statement����[������룺" +sqlex.getErrorCode()+"] "+ sqlex.getMessage());
			}
			stmt = null;
		}
	}
	/**
	 * �ر����ݿ�����
	 * 
	 * @param con Ҫ�رյ����ݿ����� 
	 */
	public static void closeConnection(Connection con)
	{
		if (con != null)
		{
			try {
				 con.close();  
			}
			catch(SQLException sqlex)
			{
				log.error("�ر����ݿ����ӳ���[������룺" +sqlex.getErrorCode()+"] "+ sqlex.getMessage());
			}
			con = null;
		}
	}
}
