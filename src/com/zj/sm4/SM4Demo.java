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
    	System.out.println("��������Կ��");
    	Scanner sc = new Scanner(System.in);
    	sm4.setSecretKey(sc.nextLine()); 	
		String input = null;
		
		/*//���ܹ���
		System.out.println("������ԭʼ�����ļ�����·����");
		input = sc.nextLine();
		Path opath = Paths.get(input);
		while (!Files.exists(opath) || (Files.isDirectory(opath))) {
			System.out.println("ԭʼ�ļ������ڣ����������룺");
			input = sc.nextLine();
			opath = Paths.get(input);
		}
				
		System.out.println("��������������ļ�����·������ע�⣺�����ļ�����·����ԭ�ļ�������ͬ�������ļ���Ӧ���Ͻӿڹ淶��");
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
				
		//���ܹ���
		System.out.println("����������ļ�����·����");
		input = sc.nextLine();
		Path epath = Paths.get(input);
				
		while (!Files.exists(epath) || (Files.isDirectory(epath))) {
			System.out.println("�ļ������ڣ����������룺");
			input = sc.nextLine();
			epath = Paths.get(input);
		}
				
		System.out.println("��������������ļ�����·������ע�⣺���ܾ���·����ԭ�ļ��������ļ���������ͬ��");
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
