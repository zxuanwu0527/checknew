package com.szkingdom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Tl {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file =new File("E:/ZJ/zxzq/SC_11020000_20120525203939.LIST");
		try {
			FileInputStream fis = new FileInputStream(file);
			
			BufferedReader  br  = new BufferedReader(new InputStreamReader(fis,"utf-8"));
			String s ="";
			
		
			while((s=br.readLine())!=null)
			{
				System.out.println(s);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
       // System.out.println(file.getName());
        
        //String s =" ???Version=1.0|RecordCount=30";
        //System.out.println(s.replaceAll(" ", "").matches("\\?*Version=\\d+\\.\\d+\\|RecordCount=\\d+"));
	}

}
