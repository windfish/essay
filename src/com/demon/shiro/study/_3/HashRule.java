package com.demon.shiro.study._3;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * 密码hash 的生成规则：md5(密码 + 用户名 + salt)
 * @author xuliang
 * @since 2019年7月12日 上午10:12:33
 *
 */
public class HashRule {

    private static final String algorithmName = "MD5";
    private static final int hashIterations = 2;
    private static SecureRandomNumberGenerator generator = new SecureRandomNumberGenerator();
    
    public static String generatorSalt(){
        return generator.nextBytes().toHex();
    }
    
    public static int getHashIterations(){
        return hashIterations;
    }
    
    public static String hashPassword(String password, String username, String salt){
        SimpleHash hash = new SimpleHash(algorithmName, password, username + salt, hashIterations);
        return hash.toHex();
    }
    
    public static void main(String[] args) {
        System.out.println(generatorSalt());
    }
    
}
