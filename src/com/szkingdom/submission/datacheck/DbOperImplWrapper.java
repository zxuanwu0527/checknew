package com.szkingdom.submission.datacheck;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.szkingdom.submission.datacheck.DataConstants.DataTye;

public class DbOperImplWrapper implements DbOper {

	private final Logger logger = Logger.getLogger(DbOperImplWrapper.class);
	
	private static List<Map<String, String>> prolist = null;
	public void insert(String sql, Object[] params, DataTye[] type) throws DaoException {
		logger.warn("no implements!!");

	}

	public void batchInsert(String sql, List<String[]> paramsList, DataTye[] type) throws DaoException {
		logger.warn("no implements!!");
	}

	public Map<Integer, Object> exeProcedure(String sql, Object[] inParams, DataTye[] inType, int[] inOrder,
			DataTye[] outType, int[] outOrder) throws DaoException {
		logger.warn("no implements!!");
		return null;
	}

	public List<Map<String, String>> queryList(String sql) throws DaoException {
		logger.warn("no implements!!");
		return null;
	}

	public List<Map<String, String>> queryProcList(String sql) throws DaoException {
		if(prolist == null || prolist.size() == 0){
			DbOper dao = (DbOper)ObjectFactory.instance().getBean("dao");
			prolist = dao.queryList(sql);
		}
		return prolist;
	}

}
