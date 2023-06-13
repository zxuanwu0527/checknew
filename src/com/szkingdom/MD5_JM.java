package com.szkingdom;


import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  

//import sun.io.CharToByteConverter;
//import sun.io.MalformedInputException;
  
public class MD5_JM {  
  
    public String getMD5(byte[] source){  
        String s=null;  
        //�������ֽ�ת����16���Ʊ�ʾ���ַ�   
        char[] hexDigits={'0','1','2','3','4','5','6','7','8','9',  
                'a','b','c','d','e','f'};  
        try {  
            MessageDigest md=MessageDigest.getInstance("MD5");  
            md.update(source);  
            //MD5�ļ�������һ��128λ�ĳ����������ֽڱ�ʾΪ16���ֽ�   
            byte[] tmp=md.digest();  
            //ÿ���ֽ���16���Ʊ�ʾ�Ļ���ʹ��2���ַ�(��4λһ��,��4λһ��)�����Ա�ʾ��16������Ҫ32���ַ�   
            char[] str=new char[16*2];  
            int k=0;//ת������ж�Ӧ���ַ�λ��   
            for(int i=0;i<16;i++){//��MD5��ÿһ���ֽ�ת����16�����ַ�   
                byte byte0=tmp[i];  
                str[k++]=hexDigits[byte0>>>4 & 0xf];//���ֽڸ�4λ����16����ת��   
                str[k++]=hexDigits[byte0 & 0xf];    //���ֽڵ�4λ����16����ת��   
            }  
            s=new String(str);  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        return s;  
    }  
    /*public static void main(String[] args) {  
        MD5_JM md5Util=new MD5_JM();  
        String result=md5Util.getMD5("http://www.honglonglong.com".getBytes()); 
        
        System.out.println(result); 
        System.out.println(Integer.toHexString("http://www.honglonglong.com".getBytes()[0]));
        byte a[] ={':'};
        System.out.println(a[0]);
        System.out.println(getKey(result));
        
    } */
    
   /* public static String getKey(String md5password)
    {
    	byte key[]  = md5password.getBytes(); 
    	AES��Կ��ͨ��md5���ܺ�����32λ��16��������ɵ����ģ�������ͨ����������ּ���һ��
    	 * ���㷽�����£�ÿ��16������ͨ������1����µ�16����ֵ�����ֵ����16������f��
    	  
        char f[] =new char[key.length];
    	for(int i=0;i<key.length;i++)
    	{
    		int m =(int) key[i];
    		
           
    		if(m+1>102)
    		{
  
    			f[i]=(char)key[i-1];
    		}
    		else
    		{
    			//�����16������λ9�����
    			if(m+1==58)
    			{
    				f[i]='a';
    			}
    			else
    			{
    				f[i] = (char)(m+1);
    			}
    			
    		}
    		
    	}
    	CharToByteConverter converter =CharToByteConverter.getDefault();
    	try {
			key = converter.convertAll(f);
		} catch (MalformedInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	return  new String(key);
    }*/
    
  
}  

