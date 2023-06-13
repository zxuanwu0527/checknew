package com.szkingdom.submission.sync;


import java.util.Map;

/**
 * 记录需要同步的进程的端口
 * @author leipeng
 *
 */
public class SyncPort {
	private Map<String, String> serverPort;

	public final Map<String, String> getServerPort() {
		return serverPort;
	}

	public final void setServerPort(Map<String, String> serverPort) {
		this.serverPort = serverPort;
	}

}
