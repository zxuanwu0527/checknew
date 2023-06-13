package com.szkingdom;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Node;

import com.szkingdom.a.XMLUtil;

public class Import_Data {

	/**
	 * @param args
	 */
	private static Logger logger = Logger.getLogger(Import_Data.class);
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private static org.dom4j.Document luceneConfig =null;
	private static String   driverclass = null;
	private static String   url = null;
	private static String   userid = null;
	private static String   password = null;
	private static String   path_mulu = null;
	//private static int      count_file = 0 ;//���ڼ�¼��ⱸ�ÿ�����˳��
	//private static Date    check_rtn = new Date();
	public static synchronized void importDate(String zqgsdm,String sjrq,String check_rtn)
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
			
			logger.debug("----------------------------------�ӱ��ÿ�ͬ�����ݵ����Ŀ�-------------------------------");
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
			    sql = "select t3.* from T_KRMS_CHECK_DICT t1,T_KRMS_check_execute t2,T_KRMS_check_detail_config t3 where t1.check_id = t2.check_id and t2.name = t3.name and t1.check_id=90000 order by t1.CHECK_TYPE,t1.CHECK_DETAIL_TYPE,t2.Run_orders";
				stm = conn.createStatement();
			    rs = stm.executeQuery(sql);
			    List result = getResult(rs);
			    		    
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
	

}
