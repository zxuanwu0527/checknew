package com.szkingdom.submission.datacheck.po;

import java.io.File;

public class CheckFile {

	private File zqgsDir;
	private String zqgsdm;
	public final File getZqgsDir() {
		return zqgsDir;
	}
	public final void setZqgsDir(File zqgsDir) {
		this.zqgsDir = zqgsDir;
	}
	public final String getZqgsdm() {
		return zqgsdm;
	}
	public final void setZqgsdm(String zqgsdm) {
		this.zqgsdm = zqgsdm;
	}
	
	public CheckFile(String zqgsdm, File zqgsDir) {
		this.zqgsDir = zqgsDir;
		this.zqgsdm = zqgsdm;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CheckFile other = (CheckFile) obj;
		if (zqgsdm == null) {
			if (other.zqgsdm != null)
				return false;
		} else if (!zqgsdm.equals(other.zqgsdm))
			return false;
		return true;
	}
	@Override
	public final String toString() {
		return  zqgsdm + ",";
	}
	
}
