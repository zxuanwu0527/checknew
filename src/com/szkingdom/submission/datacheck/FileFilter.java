package com.szkingdom.submission.datacheck;

import java.io.FilenameFilter;
import java.io.File;

public class FileFilter implements FilenameFilter {
	private String type;
	public FileFilter(String type){
		this.type = type;
	}

	public boolean accept(File dir,String name){
		return name.toLowerCase().endsWith(type);	}
}
