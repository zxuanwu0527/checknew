package com.szkingdom.submission.datacheck;

public class ThreadParam {

	private static final	int DEFAULT_THREAD_NUM = 10; 
	/**
	 * 以下参数主要为了调节系统性能，尽量合理利用cpu
	 */
	//解压、解密、验证记录数操作的并发线程数
	private int basicCheckThreadNum = DEFAULT_THREAD_NUM;
	//同时执行1509个存储过程的公司数
	private int logicCheckZqgsNum = DEFAULT_THREAD_NUM;
	//执行1509个存储过程的线程池大小
	private int procThreadNum = DEFAULT_THREAD_NUM;
	
	public final int getBasicCheckThreadNum() {
		return basicCheckThreadNum;
	}
	public final void setBasicCheckThreadNum(int basicCheckThreadNum) {
		this.basicCheckThreadNum = basicCheckThreadNum;
	}
	public final int getLogicCheckZqgsNum() {
		return logicCheckZqgsNum;
	}
	public final void setLogicCheckZqgsNum(int logicCheckZqgsNum) {
		this.logicCheckZqgsNum = logicCheckZqgsNum;
	}
	public final int getProcThreadNum() {
		return procThreadNum;
	}
	public final void setProcThreadNum(int procThreadNum) {
		this.procThreadNum = procThreadNum;
	}
	

}
