package com.szkingdom.submission.datacheck;

import java.util.List;
import java.util.Map;

import com.szkingdom.submission.datacheck.DataConstants.DataTye;

public interface DbOper {

	public void insert(String sql, Object[] params, DataTye[] type)throws DaoException;
	/**
	 * ������������
	 * @param sql
	 * @param paramsList
	 * @param type
	 * @throws DaoException
	 */
	public void batchInsert(String sql, List<String[]> paramsList, DataTye[] type)throws DaoException;
	/**
	 * ����map���������keyΪ�洢���̶�Ӧ�Ĳ������
	 * @param sql
	 * @param inParams
	 * @param inType
	 * @param inOrder
	 * @param outParams
	 * @param outType
	 * @param outOrder
	 * @return
	 * @throws DaoException
	 */
	public Map<Integer, Object>  exeProcedure(String sql, Object[] inParams, DataTye[] inType, int[] inOrder, 
			DataTye[] outType, int[] outOrder)throws DaoException;
	
	public List<Map<String, String>> queryList(String sql)throws DaoException;
	
	public List<Map<String, String>> queryProcList(String sql)throws DaoException;
}
