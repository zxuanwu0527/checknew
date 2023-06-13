package com.szkingdom.submission.datacheck.po;

public class TaskFinishInfo {

	private String processorIp;
	private int processPort;
	private String zqgsdm;
	private long starttime;
	private long endtime;
	public final String getProcessorIp() {
		return processorIp;
	}
	public final void setProcessorIp(String processorIp) {
		this.processorIp = processorIp;
	}
	public final int getProcessPort() {
		return processPort;
	}
	public final void setProcessPort(int processPort) {
		this.processPort = processPort;
	}
	public final String getZqgsdm() {
		return zqgsdm;
	}
	public final void setZqgsdm(String zqgsdm) {
		this.zqgsdm = zqgsdm;
	}
	public final long getStarttime() {
		return starttime;
	}
	public final void setStarttime(long starttime) {
		this.starttime = starttime;
	}
	public final long getEndtime() {
		return endtime;
	}
	public final void setEndtime(long endtime) {
		this.endtime = endtime;
	}
	@Override
	public String toString() {
		return "TaskFinishInfo [processorIp=" + processorIp + ", processPort=" + processPort + ", zqgsdm=" + zqgsdm
				+ ", starttime=" + starttime + ", endtime=" + endtime + "]";
	}
	
}
