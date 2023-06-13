package com.zj.sm4;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class SM4Utils
{
    private String secretKey = "";
    private String iv = "";
    private boolean hexString = false;
    
    public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getIv() {
		return iv;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}

	public boolean isHexString() {
		return hexString;
	}

	public void setHexString(boolean hexString) {
		this.hexString = hexString;
	}

	public SM4Utils()
    {
    }
    
    public String encryptData_ECB(String plainText)
    {
        try 
        {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_ENCRYPT;
            
            byte[] keyBytes;
            keyBytes = secretKey.getBytes();
            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, keyBytes);
            byte[] encrypted = sm4.sm4_crypt_ecb(ctx, plainText.getBytes("UTF-8"));
            String cipherText = Base64.getEncoder().encodeToString(encrypted);
            if (cipherText != null && cipherText.trim().length() > 0)
            {
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }
            return cipherText;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public String decryptData_ECB(String cipherText)
    {
        try 
        {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_DECRYPT;
            
            byte[] keyBytes;
            keyBytes = secretKey.getBytes();
            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, keyBytes);
            byte[] decrypted = sm4.sm4_crypt_ecb(ctx, Base64.getDecoder().decode(cipherText));
            return new String(decrypted, "UTF-8");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public String encryptData_CBC(String plainText){  
         try{  
                SM4_Context ctx = new SM4_Context();  
                ctx.isPadding = true;  
                ctx.mode = SM4.SM4_ENCRYPT;  
                  
                byte[] keyBytes;  
                byte[] ivBytes;  
               
                keyBytes = secretKey.getBytes();  
                ivBytes = iv.getBytes();  
                  
                SM4 sm4 = new SM4();  
                sm4.sm4_setkey_enc(ctx, keyBytes);  
                byte[] encrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, plainText.getBytes("UTF-8"));  
                String cipherText = Base64.getEncoder().encodeToString(encrypted);
                if (cipherText != null && cipherText.trim().length() > 0)  
                {  
                    Pattern p = Pattern.compile("\\s*|\t|\r|\n");  
                    Matcher m = p.matcher(cipherText);  
                    cipherText = m.replaceAll("");  
                }  
                return cipherText;  
            }   
            catch (Exception e)   
            {  
                e.printStackTrace();  
                return null;  
            }  
        }  
          
        public String decryptData_CBC(String cipherText)  
        {  
            try   
            {  
                SM4_Context ctx = new SM4_Context();  
                ctx.isPadding = true;  
                ctx.mode = SM4.SM4_DECRYPT;  
                  
                byte[] keyBytes;  
                byte[] ivBytes;  
                if (hexString)  
                {  
                    keyBytes = Util.hexStringToBytes(secretKey);  
                    ivBytes = Util.hexStringToBytes(iv);  
                }  
                else  
                {  
                    keyBytes = secretKey.getBytes();  
                    ivBytes = iv.getBytes();  
                }  
                  
                SM4 sm4 = new SM4();  
                sm4.sm4_setkey_dec(ctx, keyBytes);  
                byte[] decrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, Base64.getDecoder().decode(cipherText));
                return new String(decrypted, "UTF-8");  
            }   
            catch (Exception e)  
            {  
                e.printStackTrace();
                return null;  
            }  
        }    

    	/**
    	 * SM4加密数据
    	 * @param plainText 原文
    	 * @return
    	 */
    	public static String SM4Encode(String plainText){

            SM4Utils sm4 = new SM4Utils();
            sm4.setSecretKey("tradeneeq&123456");
            sm4.setHexString(false);
            return sm4.encryptData_ECB(plainText);
    	}
    	/**
    	 * SM4解密
    	 * @param cipherText 密文
    	 * @return
    	 */
    	public static String SM4Decode(String cipherText){
            SM4Utils sm4 = new SM4Utils();
            sm4.setSecretKey("tradeneeq&123456");
            sm4.setHexString(false);
            return sm4.decryptData_ECB(cipherText);
    	}
}
