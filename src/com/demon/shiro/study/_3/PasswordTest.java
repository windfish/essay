package com.demon.shiro.study._3;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author xuliang
 * @since 2019年7月11日 下午4:49:08
 *
 */
public class PasswordTest {

    /**
     * DefaultPasswordService配合PasswordMatcher实现简单的密码加密与验证服务
     */
    @Test
    public void test(){
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:com/demon/shiro/study/_3/password.ini");
        SecurityManager instance = factory.getInstance();
        SecurityUtils.setSecurityManager(instance);
        
        Subject subject = SecurityUtils.getSubject();
        subject.login(new UsernamePasswordToken("admin", "123"));
        
        System.out.println(subject.isAuthenticated());
        
        Assert.assertTrue(subject.isAuthenticated());
    }
    
    /**
     * HashedCredentialsMatcher 实现密码验证服务，可以提供自己的salt，生成密码散列值的算法需要自己实现
     */
    @Test
    public void testHash(){
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:com/demon/shiro/study/_3/hash.ini");
        SecurityManager instance = factory.getInstance();
        SecurityUtils.setSecurityManager(instance);
        
        Subject subject = SecurityUtils.getSubject();
        subject.login(new UsernamePasswordToken("admin", "12345"));
        
        Object principal = subject.getPrincipal();
        System.out.println("-----------------" + JSON.toJSONString(principal));
        
        System.out.println("--------------------" + subject.isAuthenticated());
        
        Assert.assertTrue(subject.isAuthenticated());
    }

}
