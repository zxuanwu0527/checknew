package com.szkingdom.submission.datacheck.po;

public interface DataConstants{
	public enum ZqgsStatus{
		IsRunning(1), NotRunning(0);
		private int code;
		private ZqgsStatus(int code) {
			this.code = code;
		}
		public final int getCode() {
			return code;
		}
		public final void setCode(int code) {
			this.code = code;
		}
		
	}
	public enum MsgType{
		TaskQueueStatus, TaskFinishedInfo;
	}
}

