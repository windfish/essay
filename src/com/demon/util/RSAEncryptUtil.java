package com.demon.util;


import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;


public class RSAEncryptUtil {
	private static final Logger logger = Logger.getLogger(RSAEncryptUtil.class);
	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
	
	public  static void main( String [] argus) throws Exception {
        KeyPair generateRSAKeyPair = generateRSAKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) generateRSAKeyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) generateRSAKeyPair.getPublic();
        System.out.println("privateKey: "+Base64.encode(privateKey.getEncoded()));
        System.out.println("publicKey: "+Base64.encode(publicKey.getEncoded()));
        
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", 12312313123L);
        map.put("data", "测试RSA加密的原始数据asadaaaaaaaaaaaaaaaaaaaasdad12312312dasd测试RSA加密的原始数据asadaaaaaaaaaaaaaaaaaaaasdad12312312dasd测试RSA加密的原始数据asadaaaaaaaaaaaaaaaaaaaasdad12312312dasd");
        String data = JSON.toJSONString(map);
        System.out.println("data:"+data);
        
        System.out.println("--------------------------------");
        String encryptByPublicKey = encryptByPublicKey(Base64.encode(publicKey.getEncoded()), data);
        System.out.println("公钥加密："+encryptByPublicKey);
        System.out.println("私钥解密:"+decryptByPrivateKey(Base64.encode(privateKey.getEncoded()), encryptByPublicKey));
        
        /*System.out.println("--------------------------------");
        byte[] encrypt = encrypt(publicKey, data.getBytes("UTF-8"));
        System.out.println("公钥加密："+Base64.encode(encrypt));
        System.out.println("私钥解密:"+Base64.encode(decrypt(privateKey, encrypt)));
        
        System.out.println("--------------------------------");
        String encrypt2 = encryptForPrKey(Base64.encode(privateKey.getEncoded()), data);
        System.out.println("私钥加密："+encrypt2);
        System.out.println("公钥解密:"+decryptForPuKey(Base64.encode(publicKey.getEncoded()), encrypt2));*/
        
    }
	
	/** 
     * 随机生成RSA密钥对(默认密钥长度为1024) 
     * 
     * @return 
     */  
    public static KeyPair generateRSAKeyPair()  
    {  
        return generateRSAKeyPair(1024);  
    }  
  
    /** 
     * 随机生成RSA密钥对 
     * 
     * @param keyLength 
     *            密钥长度，范围：512～2048<br> 
     *            一般1024 
     * @return 
     */  
    public static KeyPair generateRSAKeyPair(int keyLength)  
    {  
        try  
        {  
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");  
            kpg.initialize(keyLength);  
            return kpg.genKeyPair();  
        } catch (NoSuchAlgorithmException e)  
        {  
            e.printStackTrace();  
            return null;  
        }  
    } 
	
	/**
	 * 从字符串中加载公钥
	 *
	 * @param publicKeyStr
	 *            公钥数据字符串
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr)
			throws Exception {
		try {
			byte[] buffer = Base64.decode(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}

	/**
	 * 从文件中加载私钥
	 *
	 * @param path
	 *            私钥文件名
	 * @return 是否成功
	 * @throws Exception
	 */
	public static String loadPrivateKeyByFile(String path) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new FileReader(path
					+ "/privateKey.keystore"));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				sb.append(readLine);
			}
			br.close();
			return sb.toString();
		} catch (IOException e) {
			throw new Exception("私钥数据读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥输入流为空");
		}
	}

	public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr)
			throws Exception {
		try {
			byte[] buffer = Base64.decode(privateKeyStr);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("私钥非法");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}
	private static final int MAX_ENCRYPT_BLOCK = 117;
	private static final int MAX_DECRYPT_BLOCK = 128;
	public static byte[] encryptByPublicKey(byte[] data,RSAPublicKey publicKey)
			throws Exception {
		// 对数据加密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	/**
	 * 公钥加密过程
	 *
	 * @param publicKey
	 *            公钥
	 * @param plainTextData
	 *            明文数据
	 * @return
	 * @throws Exception
	 *             加密过程中的异常信息
	 * @Note 明文过长时，需要分段加密
	 */
	public static byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData)
			throws Exception {
		if (publicKey == null) {
			throw new Exception("加密公钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			// 使用默认RSA
			cipher = Cipher.getInstance("RSA");
			// cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(plainTextData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此加密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("加密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏");
		}
	}


	/**
	 * 私钥加密过程
	 *
	 * @param privateKey
	 *            私钥
	 * @param plainTextData
	 *            明文数据
	 * @return
	 * @throws Exception
	 *             加密过程中的异常信息
	 * @Note 明文过长时，需要分段加密
	 */
	public static byte[] encrypt(RSAPrivateKey privateKey, byte[] plainTextData)
			throws Exception {
		if (privateKey == null) {
			throw new Exception("加密私钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			// 使用默认RSA
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(plainTextData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此加密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("加密私钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏");
		}
	}

	public static byte[] decryptByPrivateKey(byte[] encryptedData, RSAPrivateKey privateKey)
			throws Exception {

		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

	/**
	 * 私钥解密过程
	 *
	 * @param privateKey
	 *            私钥
	 * @param cipherData
	 *            密文数据
	 * @return 明文
	 * @throws Exception
	 *             解密过程中的异常信息
	 * @Note 明文过长时，需要分段解密
	 */
	public static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData)
			throws Exception {
		if (privateKey == null) {
			throw new Exception("解密私钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			// 使用默认RSA
			cipher = Cipher.getInstance("RSA");
			// cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(cipherData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此解密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("解密私钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("密文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("密文数据已损坏");
		}
	}



	/**
	 * 公钥解密过程
	 *
	 * @param publicKey
	 *            公钥
	 * @param cipherData
	 *            密文数据
	 * @return 明文
	 * @throws Exception
	 *             解密过程中的异常信息
	 * @Note 明文过长时，需要分段解密
	 */
	public static byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData)
			throws Exception {
		if (publicKey == null) {
			throw new Exception("解密公钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			// 使用默认RSA
			cipher = Cipher.getInstance("RSA");
			// cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(cipherData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此解密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("解密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("密文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("密文数据已损坏");
		}
	}


	 /**
	 * 公钥加密
	 * @param publicKeyStr
	 * @param data 加密字符串
	 * @return String 密文数据
	 * @throws Exception
	 */
	public static String encryptForPuKey(String publicKeyStr, String data) throws Exception{
		 RSAPublicKey publicKey= RSAEncryptUtil.loadPublicKeyByStr(publicKeyStr);
		 byte[] cipherData=encrypt(publicKey,data.getBytes());
		 String cipher=Base64.encode(cipherData);
		 return cipher;
	 }
	public static String encryptByPublicKey(String publicKeyStr, String data) throws Exception{
		RSAPublicKey publicKey= RSAEncryptUtil.loadPublicKeyByStr(publicKeyStr);
		byte[] cipherData=encryptByPublicKey(data.getBytes(),publicKey);
		String cipher=Base64.encode(cipherData);
		cipher = cipher.replaceAll("\\+", "*");
		cipher = cipher.replaceAll("/", "-");
		return cipher;
	}
	/**
	 * 私钥加密
	 * @param privateKeyStr 私钥
	 * @param data 加密字符串
	 * @return String 密文数据
	 * @throws Exception
	 */
	public static String encryptForPrKey(String privateKeyStr, String data) throws Exception{
		RSAPrivateKey privateKey= RSAEncryptUtil.loadPrivateKeyByStr(privateKeyStr);
		 byte[] cipherData=encrypt(privateKey,data.getBytes());
		 String cipher=Base64.encode(cipherData);
		 return cipher;
	 }

	/**
	 * 私钥解密方法
	 * @param privateKey 私钥
	 * @param plainTextData 密文
	 * @return String 明文
	 * @throws Exception
	 */
	public static String decryptForPrKey(String privateKey, String plainTextData) throws Exception{
		byte[] res= RSAEncryptUtil.decrypt(RSAEncryptUtil.loadPrivateKeyByStr(privateKey), Base64.decode(plainTextData));
		String restr=new String(res,"UTF-8");
		return restr;
	}
	public static String decryptByPrivateKey(String privateKey, String plainTextData) throws Exception{
		plainTextData = plainTextData.replaceAll("\\*", "+");
		plainTextData = plainTextData.replaceAll("-", "/");
		byte[] res= RSAEncryptUtil.decryptByPrivateKey(Base64.decode(plainTextData),RSAEncryptUtil.loadPrivateKeyByStr(privateKey));
		String restr=new String(res,"UTF-8");

		return restr;
	}

	/**
	 * 公钥解密方法
	 * @param publicKey
	 * @param plainTextData 密文
	 * @return 明文
	 * @throws Exception
	 */
	public static String decryptForPuKey(String publicKey, String plainTextData) throws Exception{
		byte[]	res= RSAEncryptUtil.decrypt(RSAEncryptUtil.loadPublicKeyByStr(publicKey), Base64.decode(plainTextData));
		String restr=new String(res);
		return restr;
	}

	/**
	 * RSA签名
	 *
	 * @param content
	 *            待签名数据
	 * @param privateKey
	 *            商户私钥
	 * @param input_charset
	 *            编码格式
	 * @return 签名值
	 */
	public static String sign(String content, String privateKey,
							  String input_charset)
	{
		try
		{
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					Base64.decode(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			Signature signature = Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(input_charset));

			byte[] signed = signature.sign();

			String signStr = Base64.encode(signed);

			signStr = signStr.replaceAll("\\+", "*");
			signStr = signStr.replaceAll("/", "-");
			return signStr;
		}
		catch (Exception e)
		{
			logger.error("RSA sign error " + e.getMessage(),e);
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * RSA验签名检查
	 *
	 * @param content
	 *            待签名数据
	 * @param sign
	 *            签名值
	 * @param public_key
	 *            支付宝公钥
	 * @param input_charset
	 *            编码格式
	 * @return 布尔值
	 */
	public static boolean doCheck(String content, String sign,
								  String public_key, String input_charset)
	{
		try
		{
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = Base64.decode(public_key);
			PublicKey pubKey = keyFactory
					.generatePublic(new X509EncodedKeySpec(encodedKey));

			Signature signature = Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initVerify(pubKey);
			signature.update(content.getBytes(input_charset));

			sign = sign.replaceAll("\\*", "+");
			sign = sign.replaceAll("-", "/");

			boolean bverify = signature.verify(Base64.decode(sign));
			return bverify;

		}
		catch (Exception e)
		{
			logger.error("RSA verify error " + e.getMessage(),e);
			e.printStackTrace();
		}

		return false;
	}
	
    /**
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("signType")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }
    public static String sign(Map<String, String> sArray,String priKey,String charset){

        Map<String, String> result = paraFilter(sArray);

        String data = createLinkString(result);
        //需要验证签名的字符串
        logger.info("需要验证签名的字符串："+data);
        String s = sign(data,priKey,charset);
        logger.info("需要验证签名之后的字符串："+s);
        return s;
    }
    public static boolean doCheck(Map<String, String> sArray,String sign,String pubKey,String charset){

		Map<String, String> result = paraFilter(sArray);

		String data = createLinkString(result);
		//需要验证签名的字符串
		logger.info("需要验证签名的字符串："+data);
		return  doCheck(data,sign,pubKey,charset);
	}


    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if(value!=null && value!=""){
                if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                    prestr = prestr + key + "=" + value;
                } else {
                    prestr = prestr + key + "=" + value + "&";
                }
            }
        }


        return prestr;
    }
}
