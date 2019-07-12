package com.demon.shiro.study._3;

import java.security.Key;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.Sha1Hash;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 编码、加解密
 * @author xuliang
 * @since 2019年7月11日 下午2:47:35
 *
 */
public class Crypto {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * base64 编解码
     */
    @Test
    public void testBase64(){
        String str = "hello";
        String base64Encoded = Base64.encodeToString(str.getBytes());
        String str2 = Base64.decodeToString(base64Encoded);
        Assert.assertEquals(str, str2);
    }
    
    /**
     * 16进制编解码
     */
    @Test
    public void testHex(){
        String str = "hello";
        String hexEncoded = Hex.encodeToString(str.getBytes());
        String str2 = new String(Hex.decode(hexEncoded));
        Assert.assertEquals(str, str2);
    }
    
    /**
     * MD5
     */
    @Test
    public void testMD5(){
        String str = "str";
        String salt = "123";
        String md5 = new Md5Hash(str, salt).toString();
        String md5_2 = new Md5Hash(str, salt, 2).toString();
        logger.info("md5:{} md5_2:{}", md5, md5_2);
    }
    
    @Test
    public void testSHA(){
        String str = "hello";
        String salt = "123";
        String sha256 = new Sha256Hash(str, salt).toString();
        String sha1 = new Sha1Hash(str, salt).toString();
        String sha512 = new Sha512Hash(str, salt).toString();
        logger.info("sha256:{} sha1:{} sha512:{}", sha256, sha1, sha512);
    }
    
    /**
     * 通用的hash
     */
    @Test
    public void testSimpleHash(){
        String str = "hello";
        String salt = "123";
        String hash = new SimpleHash("SHA-1", str, salt).toString();
        logger.info("simple hash sha1:{}", hash);
    }
    
    @Test
    public void testHashService(){
        DefaultHashService hashService = new DefaultHashService();
        
        HashRequest request = new HashRequest.Builder()
                        .setAlgorithmName("MD5")    // 算法
                        .setSource("hello")     // 数据
                        .setSalt("123")         // salt
                        .setIterations(2)       // 迭代次数
                        .build();
        
        String string = hashService.computeHash(request).toString();
        logger.info("hashService:{}", string);
    }
    
    /**
     * AES 加解密
     */
    @Test
    public void testAES(){
        AesCipherService aesCipherService = new AesCipherService();
        aesCipherService.setKeySize(128);
        Key key = aesCipherService.generateNewKey();    // 生成key
        String str = "hello";
        String encode = aesCipherService.encrypt(str.getBytes(), key.getEncoded()).toHex(); // 加密
        String str2 = aesCipherService.decrypt(encode.getBytes(), key.getEncoded()).toHex(); // 解密
        
        Assert.assertEquals(str, str2);
        
    }
    
}
