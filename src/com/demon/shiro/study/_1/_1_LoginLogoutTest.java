package com.demon.shiro.study._1;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author xuliang
 * @since 2019年7月11日 上午10:29:48
 *
 */
public class _1_LoginLogoutTest {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Test
    public void test() {

        // 获取 SecurityManager 工厂，从INI 配置文件里初始化SecurityManager
        Factory<SecurityManager> factory = getIniSecurityManagerFactory_MyRealm();  //getIniSecurityManagerFactory();
        // 使用工厂方法构造SecurityManager
        SecurityManager instance = factory.getInstance();
        // 将SecurityManager 绑定给SecurityUtils，全局公用
        SecurityUtils.setSecurityManager(instance);
        // 获取subject
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("admin", "123");
        
        try{
            // 登录，验证身份
            subject.login(token);
        }catch (Exception e) {
//            logger.warn("login exception", e);
        }
        // 断言用户已验证通过
        Assert.assertEquals(true, subject.isAuthenticated());
        // 退出
        subject.logout();
        logger.info("test end");
    }
    
    /**
     * 默认配置
     */
    public Factory<SecurityManager> getIniSecurityManagerFactory(){
        return new IniSecurityManagerFactory("classpath:com/demon/shiro/study/_1/shiro.ini");
    }
    
    /**
     * 自定义Realm
     */
    public Factory<SecurityManager> getIniSecurityManagerFactory_MyRealm(){
        return new IniSecurityManagerFactory("classpath:com/demon/shiro/study/_1/shiro-realm.ini");
    }

}
