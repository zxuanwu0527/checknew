package com.szkingdom;

import java.util.Date;
public class MD5_PM {


	private static final char code[] = { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
	    'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
	    'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
	    'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
	    'U', 'V', 'W', 'X', 'Y', 'Z', ',', '.', '/', ';', '\'', ':', '"',
	    '[', ']', '{', '}', '\\', '|', '!', '@', '#', '$', '%', '^', '&',
	    '*', '(', ')', '-', '_', '+', '=', '0', '1', '2', '3', '4', '5',
	    '6', '7', '8', '9' };

	/**
	* ��λ�����ƽ�
	*/
	public static void Md5_6(String md5Password) {
	   String testPassword;
	   MD5_PM md5Obj = new MD5_PM();
	   String result;
	   for (int a = 0; a < code.length; a++) {
	    testPassword = "";
	    testPassword += code[a];
	    for (int b = 0; b < code.length; b++) {
	     testPassword = testPassword.substring(0, 1);
	     testPassword += code[b];
	     for (int c = 0; c < code.length; c++) {
	      testPassword = testPassword.substring(0, 2);
	      testPassword += code[c];
	      for (int d = 0; d < code.length; d++) {
	       testPassword = testPassword.substring(0, 3);
	       testPassword += code[d];
	       for (int e = 0; e < code.length; e++) {
	        testPassword = testPassword.substring(0, 4);
	        testPassword += code[e];
	        for (int f = 0; f < code.length; f++) {
	         testPassword = testPassword.substring(0, 5);
	         testPassword += code[f];
	         // System.out.println(testPassword);
	         result = new MD5_JM().getMD5(testPassword.getBytes());
	         System.out.println(result);
	         if (md5Password.equals(result)) {
	          System.out.println("�����Ѿ��ƽ�!");
	          System.out.println("������:" + testPassword);
	          System.out.println("������:" + md5Password);
	          return;
	         }
	        }
	       }
	      }
	     }
	    }
	   }
	}

	/**
	* ��λ�����ƽ�,д�������λ�����ƽ�
	*/
	public static void Md5_7(String md5Password) {
	}

	/**
	* ��λ�����ƽ�,д�������λ�����ƽ�
	*/
	public static void Md5_8(String md5Password) {
	}

	public static void main(String[] args) {
		MD5_PM md5Obj = new MD5_PM();
	                // MD5���ܶ���
	   String md5Password = new MD5_JM().getMD5(password.getBytes());
	   // �������Ϊ��Ҫ�ҵ��ľ������ܵ�����
	   System.out.println("�����Ʋ�����!");
	   System.out.println("������:" + password);
	   System.out.println("������:" + md5Password);
	   System.out.println("����ʱ���ʱ��!");
	   System.out.println("��ʼʱ��:" + new Date());
	   Md5_6(md5Password);
	   // ���ε���6λ�ƽ⵽20λ�ƽ�..
	   // Md5_7(testResult);
	   System.out.println("����ʱ��:" + new Date());
	}

	private static final String password = "aaa918";

	/**
	* ����ֻ��һ��ʵ�ֵķ���, ���Լ��ƽ��ʱ����Լ�������ֱ��������, 
	* �����ƽ�, ����ֻ�ǽ���һ��aaaBc8���ܵĲ���
	*/
	private static final String MD5PWD = "������ݿ��е�����";

}
