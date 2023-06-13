package com.szkingdom;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * @author ����¡¡��
 *
 */
public class AES {


	 /**
	  * AES�����㷨
	  */
	 public AES() {
	 }
	 /** 
	  * ���� 
	  * @param content ��Ҫ���ܵ����� 
	  * @param keyWord  ������Կ 
	  * @return byte[]  ���ܺ���ֽ�����
	  */  
	 public static byte[] encrypt(String content, String keyWord) {  
	         try {             
	                 KeyGenerator kgen = KeyGenerator.getInstance("AES");
	                 SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );  
	                 secureRandom.setSeed(keyWord.getBytes());  
	                 kgen.init(128,secureRandom);  
	                 SecretKey secretKey = kgen.generateKey();  
	                 byte[] enCodeFormat = secretKey.getEncoded();  
	                 SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");  
	                 Cipher cipher = Cipher.getInstance("AES");// ����������  
	                 byte[] byteContent = content.getBytes("utf-8");  
	                 cipher.init(Cipher.ENCRYPT_MODE, key);// ��ʼ��  
	                 byte[] result = cipher.doFinal(byteContent);  
	                 return result; // ����  
	         } catch (NoSuchAlgorithmException e) {  
	                 e.printStackTrace();  
	         } catch (NoSuchPaddingException e) {  
	                 e.printStackTrace();  
	         } catch (InvalidKeyException e) {  
	                 e.printStackTrace();  
	         } catch (UnsupportedEncodingException e) {  
	                 e.printStackTrace();  
	         } catch (IllegalBlockSizeException e) {  
	                 e.printStackTrace();  
	         } catch (BadPaddingException e) {  
	                 e.printStackTrace();  
	         }  
	         return null;  
	 }
	 /**
	  * @param content ��Ҫ���ܵ�����
	  * @param password ������Կ
	  * @return String  ���ܺ���ַ���
	  */
	 public static String encrypttoStr(String content, String password){
	  return parseByte2HexStr(encrypt(content,password));  
	 }
	 /**���� 
	  * @param content  ���������� 
	  * @param keyWord ������Կ 
	  * @return  byte[]
	  */  
	 public static byte[] decrypt(byte[] content, String keyWord) {
	         try {  
	                  KeyGenerator kgen = KeyGenerator.getInstance("AES");  
	                  SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );  
	                 secureRandom.setSeed(keyWord.getBytes());  
	                 kgen.init(128,secureRandom);  
	                SecretKey secretKey = kgen.generateKey();  
	                  byte[] enCodeFormat = secretKey.getEncoded();  
	                  SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");              
	                  Cipher cipher = Cipher.getInstance("AES");// ����������  
	                 cipher.init(Cipher.DECRYPT_MODE, key);// ��ʼ��  
	                 byte[] result = cipher.doFinal(content);  
	                 return result; // ����  
	         } catch (NoSuchAlgorithmException e) {  
	                 e.printStackTrace();  
	         } catch (NoSuchPaddingException e) {  
	                 e.printStackTrace();  
	         } catch (InvalidKeyException e) {  
	                 e.printStackTrace();  
	         } catch (IllegalBlockSizeException e) {  
	                 e.printStackTrace();  
	         } catch (BadPaddingException e) {  
	                 e.printStackTrace();  
	         }  
	         return null;  
	 }
	 /**
	  * @param content ����������(�ַ���)
	  * @param keyWord ������Կ
	  * @return byte[]
	  */
	 public static byte[] decrypt(String content, String keyWord) {
	  return decrypt(parseHexStr2Byte(content),keyWord);  
	 }
	 /**��������ת����16���� 
	  * @param buf 
	  * @return  String
	  */  
	 public static String parseByte2HexStr(byte buf[]) {  
	         StringBuffer sb = new StringBuffer();  
	         for (int i = 0; i < buf.length; i++) {  
	                 String hex = Integer.toHexString(buf[i] & 0xFF);  
	                 if (hex.length() == 1) {  
	                         hex = '0' + hex;  
	                 }  
	                 sb.append(hex.toUpperCase());  
	         }  
	         return sb.toString();  
	 }
	 /**��16����ת��Ϊ������ 
	  * @param hexStr 
	  * @return  byte[]
	  */  
	 public static byte[] parseHexStr2Byte(String hexStr) {  
	         if (hexStr.length() < 1)  
	                 return null;  
	         byte[] result = new byte[hexStr.length()/2];  
	         for (int i = 0;i< hexStr.length()/2; i++) {  
	                 int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  
	                 int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  
	                 result[i] = (byte) (high * 16 + low);  
	         }  
	         return result;  
	 }  


	 public static void main(String[] args) throws UnsupportedEncodingException {
	  String content = "̷������111";  
	  String Key = "http://www.honglonglong.com";  
	  
	  //����
	  System.out.println("����ǰ��" + content);
	  String encryptResult = encrypttoStr(content, "a02fcbb8fe6cbc452803db415b8740aa");
	  System.out.println("���ܺ�" + encryptResult); 
	  //����  
	  byte[] decryptResult = decrypt(encryptResult,"a02fcbb8fe6cbc452803db415b8740aa");  
	  System.out.println("���ܺ�" + new String(decryptResult,"utf-8")); 
	 }
}

