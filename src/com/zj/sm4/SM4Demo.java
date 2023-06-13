package com.zj.sm4;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.* ;

public class SM4Demo {
	public static void main(String[] args) {
		SM4Utils sm4 = new SM4Utils();
		sm4.setHexString(false);
    	System.out.println("请输入密钥：");
    	Scanner sc = new Scanner(System.in);
    	sm4.setSecretKey(sc.nextLine()); 	
		String input = null;
		
		/*//加密过程
		System.out.println("请输入原始数据文件绝对路径：");
		input = sc.nextLine();
		Path opath = Paths.get(input);
		while (!Files.exists(opath) || (Files.isDirectory(opath))) {
			System.out.println("原始文件不存在，请重新输入：");
			input = sc.nextLine();
			opath = Paths.get(input);
		}
				
		System.out.println("请输入加密数据文件绝对路径：（注意：加密文件绝对路径与原文件不得相同；加密文件名应符合接口规范）");
		input = sc.nextLine();
		Path epath = Paths.get(input);
			
		FileInputStream fis = null;
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		BufferedReader br = null;
				
		File ofile = new File(opath.toString());
		    	
		try {
			fis = new FileInputStream(ofile);
			br   = new BufferedReader(new InputStreamReader(fis));
			fos = new FileOutputStream(epath.toString());
			bw = new BufferedWriter(new OutputStreamWriter(fos));
			String plainText = null; 
			String cipherText = null;
					
			while((plainText = br.readLine()) != null){
				cipherText = sm4.encryptData_ECB(plainText);
				bw.write(cipherText + "\n");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				br.close();
				bw.close();
				fos.close();
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	 				
		}*/
				
		//解密过程
		System.out.println("请输入加密文件绝对路径：");
		input = sc.nextLine();
		Path epath = Paths.get(input);
				
		while (!Files.exists(epath) || (Files.isDirectory(epath))) {
			System.out.println("文件不存在，请重新输入：");
			input = sc.nextLine();
			epath = Paths.get(input);
		}
				
		System.out.println("请输入解密数据文件绝对路径：（注意：解密绝对路径与原文件及加密文件均不得相同）");
		input = sc.nextLine();
		Path dpath = Paths.get(input);
		
		FileInputStream fis = null;
		BufferedReader br = null;
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		
		File efile = new File(epath.toString());
		
		try {												
			fis = new FileInputStream(efile);
			br = new BufferedReader(new InputStreamReader(fis));
			String plainText = null; 
			String cipherText = null;									
			fos = new FileOutputStream(dpath.toString());
			bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			while((cipherText=br.readLine())!=null) {
				plainText = sm4.decryptData_ECB(cipherText);
				bw.write(plainText + "\n");
			}		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				br.close();
				bw.close();
				fos.close();
		 		fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	 				
		}					
	}
}
