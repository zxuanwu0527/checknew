package com.szkingdom;


import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  

//import sun.io.CharToByteConverter;
//import sun.io.MalformedInputException;
  
public class MD5_JM {  
  
    public String getMD5(byte[] source){  
        String s=null;  
        //用来将字节转换成16进制表示的字符   
        char[] hexDigits={'0','1','2','3','4','5','6','7','8','9',  
                'a','b','c','d','e','f'};  
        try {  
            MessageDigest md=MessageDigest.getInstance("MD5");  
            md.update(source);  
            //MD5的计算结果是一个128位的长整数，用字节表示为16个字节   
            byte[] tmp=md.digest();  
            //每个字节用16进制表示的话，使用2个字符(高4位一个,低4位一个)，所以表示成16进制需要32个字符   
            char[] str=new char[16*2];  
            int k=0;//转换结果中对应的字符位置   
            for(int i=0;i<16;i++){//对MD5的每一个字节转换成16进制字符   
                byte byte0=tmp[i];  
                str[k++]=hexDigits[byte0>>>4 & 0xf];//对字节高4位进行16进制转换   
                str[k++]=hexDigits[byte0 & 0xf];    //对字节低4位进行16进制转换   
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
    	AES密钥在通过md5加密后生成32位由16进制数组成的密文，该密文通过下面计算又加密一次
    	 * 计算方法如下，每个16进制数通过增加1变成新的16进制值，如果值大于16进制数f的
    	  
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
    			//处理该16进制数位9的情况
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

