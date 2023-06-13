package com.szkingdom.submission.datacheck;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.szkingdom.submission.datacheck.DataConstants.DataTye;

public class DbOperImpl implements DbOper {

	static Logger logger = Logger.getLogger(DbOperImpl.class);
	
	public void insert(String sql, Object[] params, DataTye[] type) throws DaoException {
		Connection conn = null;
		PreparedStatement pst = null;
		try{
			conn = ObjectFactory.instance().getConnection();
			pst = conn.prepareStatement(sql);
			if(params != null && type != null && params.length != type.length){
				throw new DaoException("param num is not according with the type num");
			}else if((params != null && type == null) || (params == null && type != null) ){
				throw new DaoException("param num is not according with the type num");
			}else if(params == null && type == null){
			}else{
				for(int i = 0; i < params.length; i++){
					switch (type[i]) {
					case VARCHAR:
						pst.setString(i+1, (String)params[i]);
						break;
					case NUMBER:
						pst.setInt(i+1, (Integer)params[i]);
						break;
					case LONG:
						pst.setLong(i + 1, (Long) params[i]);
						break;
					default:
						break;
					}
				}
			}
			logger.info("execute ["+sql+"]");
			pst.execute();
		}catch(Exception e){
			throw new DaoException(e);
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
	
	public Map<Integer, Object> exeProcedure(String sql, Object[] inParams, DataTye[] inType, int[] inOrder, 
			DataTye[] outType, int[] outOrder) throws DaoException {
		Connection conn = null;
		CallableStatement cstm = null;
		
		try {
			conn = ObjectFactory.instance().getConnection();
			cstm = conn.prepareCall(sql);
			if(inParams != null && inType != null && inParams.length != inType.length){
				throw new DaoException("in param num is not according with the in type num");
			}else if((inParams != null && inType == null) || (inParams == null && inType != null) ){
				throw new DaoException("in param num is not according with the in type num");
			}else if(inParams == null && inType == null){
			}else{
				for(int i = 0; i < inParams.length; i++){
					switch (inType[i]) {
					case VARCHAR:
						cstm.setString(inOrder[i], (String)inParams[i]);
						break;
					case NUMBER:
						cstm.setInt(inOrder[i], (Integer)inParams[i]);
						break;
					default:
						break;
					}
				}
			}
			boolean hasOut = false;
			if(outOrder != null && outType != null && outOrder.length != outType.length){
				throw new DaoException("out order num is not according with the out type num");
			}else if((outOrder != null && outType == null) || (outOrder == null && outType != null) ){
				throw new DaoException("out order num is not according with the out type num");
			}else if(outOrder == null && outType == null){
			}else{
				hasOut = true;
				for(int i = 0; i < outOrder.length; i++){
					switch (inType[i]) {
					case VARCHAR:
						cstm.registerOutParameter(outOrder[i], Types.VARCHAR);
						break;
					case NUMBER:
						cstm.registerOutParameter(outOrder[i], Types.NUMERIC);
						break;
					default:
						break;
					}
				}
			}
			if(logger.isDebugEnabled()){
				logger.debug("execute ["+sql+"]");
			}
			cstm.execute();
			Map<Integer, Object> returnVal = null;
			if(hasOut){
				returnVal = new HashMap<Integer, Object>();
				for(int i = 0; i < outOrder.length; i++){
					switch (inType[i]) {
					case VARCHAR:
						returnVal.put(outOrder[i], cstm.getString(outOrder[i]));
						break;
					case NUMBER:
						returnVal.put(outOrder[i], cstm.getInt(outOrder[i]));
						break;
					default:
						break;
					}
				}
			}
			return returnVal;
		} catch (SQLException e) {
			throw new DaoException(e);
		}finally{
			try {
				if(cstm != null){
					cstm.close();
				}
			} catch (SQLException e) {			}
			try {
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {			}
		}
	}
	
	public void batchInsert(String sql, List<String[]> paramsList, DataTye[] type) throws DaoException {
		Connection conn = null;
		PreparedStatement pst = null;
		try{
			if(paramsList != null && paramsList.size() > 0){
				conn = ObjectFactory.instance().getConnection();
				pst = conn.prepareStatement(sql);
				for(String[] params : paramsList){
					for(int i = 0; i < params.length; i++){
						switch (type[i]) {
						case VARCHAR:
							pst.setString(i+1, params[i]);
							break;
						case NUMBER:
							pst.setInt(i+1, Integer.parseInt(params[i].trim()));
							break;
						case LONG:
							pst.setLong(i+1, Long.parseLong(params[i].trim()));
							break;
						default:
							break;
						}
					}
					pst.addBatch();
				}
				logger.info("execute ["+sql+"]");
				pst.executeBatch();
			}
		}catch(Exception e){
			logger.error("", e);
			throw new DaoException(e);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String, String>> queryList(String sql)throws DaoException{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List result = null;
		try{
			conn = ObjectFactory.instance().getConnection();
			pst = conn.prepareStatement(sql);
			logger.info("execute ["+sql+"]");
			rs = pst.executeQuery();
			if(rs != null){
				result = new ArrayList();
				while(rs.next()){
					HashMap<String, String> row = new HashMap<String, String>();
					ResultSetMetaData  rsmd = rs.getMetaData();
					int cols = rsmd.getColumnCount();
					for(int i=1;i<=cols;i++){
						String value = null;
						if (rs.getObject(i) != null){
							value = rs.getObject(i).toString();
							if(StringUtils.isNotBlank(value)){
								row.put(rsmd.getColumnName(i).toLowerCase(), value);
							}
				        	}
					}				
					result.add(row);
				}
			}
		}catch(Exception e){
			logger.error("query error", e);
			throw new DaoException(e);
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
					conn.close();
				}
			} catch (SQLException e) {			}
		}
		return result;
	}

	public List<Map<String, String>> queryProcList(String sql) throws DaoException {
		logger.warn("no implements!!");
		return null;
	}
}
