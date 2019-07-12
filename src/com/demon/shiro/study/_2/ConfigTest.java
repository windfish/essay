package com.demon.shiro.study._2;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;
import org.junit.Test;

/**
 * 基于配置文件 shiro.ini 的方式
 * 
 * @author xuliang
 * @since 2019年7月11日 下午2:41:01
 *
 */
public class ConfigTest {

    @Test
    public void test() {
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:com/demon/shiro/study/_2/shiro.ini");

        SecurityManager securityManager = factory.getInstance();

        // 将SecurityManager设置到SecurityUtils 方便全局使用
        SecurityUtils.setSecurityManager(securityManager);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("admin", "123");
        subject.login(token);

        Assert.assertTrue(subject.isAuthenticated());
    }

}
