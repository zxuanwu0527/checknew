package com.szkingdom.submission.datacheck;

import java.util.List;
import java.util.Map;

public class DataCheckProperty {

	private String rootDir;
	private String successFileDir;
	private String failFileDir;
	private String fileEncoding;
	private int submissionFileCount;
	private List<String> zqgsDirList;
	private boolean checkQOnFri;
	private String backupEncryptFileDir;
	private String reportFileDir;
	private int stopTimeBegin;
	private int stopTimeEnd;
	private Map<String, String> decryptKeyMap;
	private String jhjgSql;
	private String workSpaceFileDir;
	private int dataFileCount;
	
	private int dataNumPerInsert;
	public final boolean isCheckQOnFri() {
		return checkQOnFri;
	}
	public final void setCheckQOnFri(boolean checkQOnFri) {
		this.checkQOnFri = checkQOnFri;
	}
	public int getDataNumPerInsert() {
		return dataNumPerInsert;
	}
	public void setDataNumPerInsert(int dataNumPerInsert) {
		this.dataNumPerInsert = dataNumPerInsert;
	}
	public String getJhjgSql() {
		return jhjgSql;
	}
	public void setJhjgSql(String jhjgSql) {
		this.jhjgSql = jhjgSql;
	}
	public String getRootDir() {
		return rootDir;
	}
	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}
	public String getSuccessFileDir() {
		return successFileDir;
	}
	public void setSuccessFileDir(String successFileDir) {
		this.successFileDir = successFileDir;
	}
	public String getFailFileDir() {
		return failFileDir;
	}
	public void setFailFileDir(String failFileDir) {
		this.failFileDir = failFileDir;
	}
	public String getFileEncoding() {
		return fileEncoding;
	}
	public void setFileEncoding(String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}
	public int getSubmissionFileCount() {
		return submissionFileCount;
	}
	public void setSubmissionFileCount(int submissionFileCount) {
		this.submissionFileCount = submissionFileCount;
	}
	public List<String> getZqgsDirList() {
		return zqgsDirList;
	}
	public void setZqgsDirList(List<String> zqgsDirList) {
		this.zqgsDirList = zqgsDirList;
	}
	public String getBackupEncryptFileDir() {
		return backupEncryptFileDir;
	}
	public void setBackupEncryptFileDir(String backupEncryptFileDir) {
		this.backupEncryptFileDir = backupEncryptFileDir;
	}
	public String getReportFileDir() {
		return reportFileDir;
	}
	public void setReportFileDir(String reportFileDir) {
		this.reportFileDir = reportFileDir;
	}
	public int getStopTimeBegin() {
		return stopTimeBegin;
	}
	public void setStopTimeBegin(int stopTimeBegin) {
		this.stopTimeBegin = stopTimeBegin;
	}
	public int getStopTimeEnd() {
		return stopTimeEnd;
	}
	public void setStopTimeEnd(int stopTimeEnd) {
		this.stopTimeEnd = stopTimeEnd;
	}
	public Map<String, String> getDecryptKeyMap() {
		return decryptKeyMap;
	}
	public void setDecryptKeyMap(Map<String, String> decryptKeyMap) {
		this.decryptKeyMap = decryptKeyMap;
	}

	public String getWorkSpaceFileDir() {
		return workSpaceFileDir;
	}
	public void setWorkSpaceFileDir(String workSpaceFileDir) {
		this.workSpaceFileDir = workSpaceFileDir;
	}
	public int getDataFileCount() {
		return dataFileCount;
	}
	public void setDataFileCount(int dataFileCount) {
		this.dataFileCount = dataFileCount;
	}
}
