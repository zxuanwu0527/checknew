package com.szkingdom.submission.datacheck.po;

import java.util.Map;

public class SyncProperty {
	private Map<String, String> processors;
	private String masterIp;
	private int masterPort;
	public final Map<String, String> getProcessors() {
		return processors;
	}
	public final void setProcessors(Map<String, String> processors) {
		this.processors = processors;
	}
	public final String getMasterIp() {
		return masterIp;
	}
	public final void setMasterIp(String masterIp) {
		this.masterIp = masterIp;
	}
	public final int getMasterPort() {
		return masterPort;
	}
	public final void setMasterPort(int masterPort) {
		this.masterPort = masterPort;
	}
}
