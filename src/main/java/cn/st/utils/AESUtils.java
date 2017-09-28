package cn.st.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.spec.KeySpec;

/**
 * Created by coolearth on 17-9-28.
 */
public class AESUtils {

    public static final String CHARSET_NAME="UTF-8";

    /**
     * 将内容通过aes加密,及base64编码转换成新的加密内容
     * @param content 待加密内容
     * @param password 密钥
     * @param salt 盐值
     * @param iv iv
     * @return
     */
    public static String encrypt(String content,String password,String salt,String iv){
        try{
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(CHARSET_NAME), 100, password.getBytes().length*8);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher= Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE,secret, new IvParameterSpec(iv.getBytes(CHARSET_NAME)));
            byte[]result=cipher.doFinal(content.getBytes(CHARSET_NAME));
            return Base64.encodeBase64String(result);
        }catch (Exception e) {
            System.out.println("exception:"+e.toString());
        }
        return null;
    }

}
