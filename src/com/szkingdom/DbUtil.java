package com.szkingdom;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
/**
 * 数据库关闭连接，数据集等常用函数。
 * 
 * @author tanlun
 */
public class DbUtil {
	private static final Logger log = Logger.getLogger(DbUtil.class);
	/**
	 * 关闭数据集
	 * 
	 * @param rs 要关闭的数据集 
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
				log.error("关闭数据集出错：[错误代码：" +sqlex.getErrorCode()+"] "+ sqlex.getMessage());
			}
			rs = null;
		}
	}
	/**
	 * 关闭Statement
	 * 
	 * @param stmt 要关闭的Statement 
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
				log.error("关闭Statement出错：[错误代码：" +sqlex.getErrorCode()+"] "+ sqlex.getMessage());
			}
			stmt = null;
		}
	}
	/**
	 * 关闭数据库连接
	 * 
	 * @param con 要关闭的数据库连接 
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
				log.error("关闭数据库连接出错：[错误代码：" +sqlex.getErrorCode()+"] "+ sqlex.getMessage());
			}
			con = null;
		}
	}
}
