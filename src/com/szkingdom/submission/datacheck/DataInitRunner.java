package com.szkingdom.submission.datacheck;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.szkingdom.submission.datacheck.DataConstants.DataTye;

/**
 * 运行校验存储过程 
 * @author leipeng
 */
public class DataInitRunner implements Callable<String> {

	static Logger logger = Logger.getLogger(DataInitRunner.class);
	private String procSql;
	private Map<String, String> pramaters;
	private String zqgsdm;
	private String sjrq;
	private String check_rtn;
	private int index;
	
	public DataInitRunner(String zqgsdm, String sjrq, String check_rtn, String procSql, Map<String, String> pramaters, int index) {
		this.procSql = procSql;
		this.pramaters = pramaters;
		this.zqgsdm = zqgsdm;
		this.sjrq = sjrq;
		this.check_rtn = check_rtn;
		this.index = index;
	}
	
	public String call() throws Exception {
		if(logger.isDebugEnabled()){
			logger.debug(procSql);
		}
		DbOper dao = (DbOper)ObjectFactory.instance().getBean("dao");
		Object[] inParams = new Object[pramaters.size() + 3];
		DataTye[] inType = new DataTye[pramaters.size() + 3];
		int[] inOrder = new int[pramaters.size() + 3]; 
		DataTye[] outType =  {DataTye.VARCHAR};;
		int[] outOrder ={pramaters.size() + 4};			
		
		int inparaIndex = 0;
		for (int k = 1; k < pramaters.size(); k++) {
			inParams[inparaIndex] = (String) pramaters.get("param" + k);
			inType[inparaIndex] = DataTye.VARCHAR;
			inOrder[inparaIndex++] = k;
		}
		inParams[inparaIndex] = check_rtn;
		inType[inparaIndex] = DataTye.VARCHAR;
		inOrder[inparaIndex++] = pramaters.size();
		
		inParams[inparaIndex] = String.valueOf(index + 1);
		inType[inparaIndex] = DataTye.VARCHAR;
		inOrder[inparaIndex++] = pramaters.size() + 1;
		
		inParams[inparaIndex] = zqgsdm;
		inType[inparaIndex] = DataTye.VARCHAR;
		inOrder[inparaIndex++] = pramaters.size() + 2;
		
		inParams[inparaIndex] = sjrq;
		inType[inparaIndex] = DataTye.VARCHAR;
		inOrder[inparaIndex++] = pramaters.size() + 3;
		Map<Integer, Object> proResult = null;
		try {
			proResult = dao.exeProcedure(procSql, inParams, inType, inOrder, outType, outOrder);
			String pro_flag = (String)proResult.get(pramaters.size() + 4);
			if(logger.isDebugEnabled()){
				logger.debug("result:"+pro_flag);
			}
			return pro_flag;
		} catch (DaoException e) {
			logger.error("执行存储过程["+procSql+"]出错", e);
		}
		return "";
	}

}
