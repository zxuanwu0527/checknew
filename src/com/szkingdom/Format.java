package com.szkingdom;

import java.util.Date;

public class Format {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Date   report_file_date = new Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf.format(report_file_date));
	}

}
