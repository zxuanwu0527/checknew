package com.szkingdom.submission.datacheck;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.szkingdom.submission.datacheck.po.DataConstants.ZqgsStatus;

public class CheckService {
	
	private static Logger logger = Logger.getLogger(CheckService.class);

	private static final String updateSql = "update zqgs_check_status set checkstatus=?, checkprocessor=?, updatetime=sysdate where zqgsdm=?";
	
	private static final String sql = "select zqgsdm from zqgs_check_status where checkstatus=? for update";
	
	//获取没有正在运行的证券公司
	public String getValiableZqgs(String rootPath, int fileLen){
		String zqgsdm = null;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> zqgsList = new ArrayList<String>();
		try{
			conn = ObjectFactory.instance().getConnection();
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			pst.setInt(1, ZqgsStatus.NotRunning.getCode());
			if(logger.isDebugEnabled()){
				logger.debug("execute ["+sql+"],checkstatus:"+ZqgsStatus.NotRunning.getCode());
			}
			rs = pst.executeQuery();
			if(rs != null){
				while(rs.next()){
					//ResultSetMetaData  rsmd = rs.getMetaData();
					//int cols = rsmd.getColumnCount();
					//for(int i=1;i<=cols;i++){
					String value = null;
					if (rs.getObject(1) != null){
						value = rs.getObject(1).toString();
						if(StringUtils.isNotBlank(value)){
							zqgsList.add(value);
						}
					}				
				}
			}
			
			Collections.shuffle(zqgsList);
			for (int i = 0; i < zqgsList.size(); i++) {
				File zqgsdir = new File(rootPath + File.separator + zqgsList.get(i));
				if(zqgsdir != null && zqgsdir.exists() && zqgsdir.listFiles() != null && (zqgsdir.listFiles().length >= fileLen)){
					zqgsdm = zqgsList.get(i);
					break;
				}else{
					zqgsdm = null;
				}
			}
			
			if(StringUtils.isNotBlank(zqgsdm)){
				PreparedStatement pst2 = null;
				ResultSet rs2 = null;
				try{
					pst2 = conn.prepareStatement(updateSql);
					pst2.setInt(1, ZqgsStatus.IsRunning.getCode());
					pst2.setString(2, DataCheckOracleQueueMain.pid);
					pst2.setString(3, zqgsdm);
					logger.info("execute ["+updateSql+"], status"+ ZqgsStatus.IsRunning.getCode()+", zqgs:"+zqgsdm);
					rs2= pst2.executeQuery();
				}catch(Exception e){
					logger.error("update error", e);
				}finally{
					pst2.close();
					rs2.close();  
				}
			}
			conn.commit();
		}catch(Exception e){
			zqgsdm = null;
			logger.error("query error", e);
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {}
			}
			try {
				if(pst != null){
					pst.close();
				}
			} catch (SQLException e) {			}
			try {
				if(conn != null){
					conn.commit();
					conn.close();
				}
			} catch (SQLException e) {			}
		}
		return zqgsdm;
	}
	
	public void setFinished(String zqgsdm){
		Connection conn = null;
		PreparedStatement pst = null;
		try{
			
			conn = ObjectFactory.instance().getConnection();
			pst = conn.prepareStatement(updateSql);
			pst.setInt(1, ZqgsStatus.NotRunning.getCode());
			pst.setString(2, DataCheckOracleQueueMain.pid);
			pst.setString(3, zqgsdm);
			logger.info("execute ["+updateSql+"], stauts:"+ZqgsStatus.NotRunning.getCode()+", zqgs:"+zqgsdm);
			pst.execute();
		}catch(Exception e){
			logger.error("", e);
		}finally{
			try {
				if(pst != null){
					pst.close();
				}
			} catch (SQLException e) {			}
			try {
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {			}
		}
	}
}
