package com.szkingdom.submission.datacheck;

public class ThreadParam {

	private static final	int DEFAULT_THREAD_NUM = 10; 
	/**
	 * ���²�����ҪΪ�˵���ϵͳ���ܣ�������������cpu
	 */
	//��ѹ�����ܡ���֤��¼�������Ĳ����߳���
	private int basicCheckThreadNum = DEFAULT_THREAD_NUM;
	//ͬʱִ��1509���洢���̵Ĺ�˾��
	private int logicCheckZqgsNum = DEFAULT_THREAD_NUM;
	//ִ��1509���洢���̵��̳߳ش�С
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
