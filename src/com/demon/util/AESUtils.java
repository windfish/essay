package com.demon.util;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/**
 * AES 加密/解密
 * @author xuliang
 * @since 2017年12月15日 下午3:12:43
 *
 */
public class AESUtils {
    
    public static void main(String[] args) throws Exception {
        String key = "加密密钥1234";
        String original = "AES对称加密";
        System.out.println("AES加密前："+original);
        String encode = encode(original, key);
        System.out.println("AES加密后："+encode);
        System.out.println("解密后："+decode(encode, key));
    }
    
    /**
     * 加密
     * @param source 原文
     * @param strKey 加密密钥
     * @return 密文
     * @throws Exception
     */
	public static String encode(String source, String strKey) throws Exception {
		Key key = getAESKey(strKey);

		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(1, key);

		byte[] plaiText = source.getBytes("UTF-8");
		byte[] cipherText = cipher.doFinal(plaiText);
		String encodeString = HexUtil.encodeHexString(cipherText);
		return encodeString;
	}

	/**
	 * 解密
	 * @param source 密文
	 * @param strKey 加密密钥
	 * @return 原文
	 * @throws Exception
	 */
	public static String decode(String source, String strKey) throws Exception {
		Key key = getAESKey(strKey);
		byte[] cipherText = HexUtil.decodeHexString(source);

		Cipher cp1 = Cipher.getInstance("AES");
		cp1.init(2, key);
		byte[] decryText = cp1.doFinal(cipherText);
		String result = new String(decryText, "UTF-8");
		return result;
	}

	private static Key getAESKey(String strKey) throws Exception {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		secureRandom.setSeed(strKey.getBytes());
		keyGen.init(128, secureRandom);
		Key key = keyGen.generateKey();
		return key;
	}
	
}