package com.szkingdom.submission.datacheck.po;

import java.util.List;

public class TaskQueueStatus {

	private long msgid;
	private String host;
	private int port;
	//�������м���Ҫ���е���������
	private int tasksizse;
	//���ڼ���Ҫ���е����е�֤ȯ��˾���룬��ĿӦ��tasksizeһ��
	private List<String> zqgsdms;
	public final long getMsgid() {
		return msgid;
	}
	public final void setMsgid(long msgid) {
		this.msgid = msgid;
	}
	public final String getHost() {
		return host;
	}
	public final void setHost(String host) {
		this.host = host;
	}
	public final int getPort() {
		return port;
	}
	public final void setPort(int port) {
		this.port = port;
	}
	public final int getTasksizse() {
		return tasksizse;
	}
	public final void setTasksizse(int tasksizse) {
		this.tasksizse = tasksizse;
	}
	public final List<String> getZqgsdms() {
		return zqgsdms;
	}
	public final void setZqgsdms(List<String> zqgsdms) {
		this.zqgsdms = zqgsdms;
	}
	@Override
	public String toString() {
		return "TaskQueueStatus [msgid=" + msgid + ", host=" + host + ", port=" + port + ", tasksizse=" + tasksizse
				+ ", zqgsdms=" + zqgsdms + "]";
	}
}
