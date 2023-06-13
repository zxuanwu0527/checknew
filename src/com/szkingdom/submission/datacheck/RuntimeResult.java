package com.szkingdom.submission.datacheck;

public class RuntimeResult {

	private boolean isSuccess;
	private String normalInfo;
	private String errInfo;
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getNormalInfo() {
		return normalInfo;
	}
	public void setNormalInfo(String normalInfo) {
		this.normalInfo = normalInfo;
	}
	public String getErrInfo() {
		return errInfo;
	}
	public void setErrInfo(String errInfo) {
		this.errInfo = errInfo;
	}
	@Override
	public String toString() {
		return "RuntimeResult [isSuccess=" + isSuccess + ", normalInfo=" + normalInfo + ", errInfo=" + errInfo
				+ "]";
	}
}