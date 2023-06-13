package com.szkingdom.submission.datacheck.po;

public class CheckTask {

	private String filePath;
	private String zqgsdm;
	public final String getFilePath() {
		return filePath;
	}
	public final void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public final String getZqgsdm() {
		return zqgsdm;
	}
	public final void setZqgsdm(String zqgsdm) {
		this.zqgsdm = zqgsdm;
	}
	
	public CheckTask(CheckFile cf) {
		this.filePath = cf.getZqgsDir().getAbsolutePath();
		this.zqgsdm = cf.getZqgsdm();
	}
	@Override
	public String toString() {
		return "CheckTask [filePath=" + filePath + ", zqgsdm=" + zqgsdm + "]";
	}
	
}
