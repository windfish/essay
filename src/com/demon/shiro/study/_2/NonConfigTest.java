package com.demon.shiro.study._2;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Assert;
import org.junit.Test;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 无配置文件的方式
 * @author xuliang
 * @since 2019年7月11日 下午2:11:46
 *
 */
public class NonConfigTest {

    @Test
    public void test(){
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        
        // 设置认证器authenticator 和认证策略AuthenticationStrategy
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        authenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());    // 验证策略
        securityManager.setAuthenticator(authenticator);
        
        // 设置授权authorizer
        ModularRealmAuthorizer authorizer = new ModularRealmAuthorizer();
        authorizer.setPermissionResolver(new WildcardPermissionResolver());     // 通配符权限解析器
        
        // 设置Realm  
        DruidDataSource ds = new DruidDataSource();  
        ds.setDriverClassName("com.mysql.jdbc.Driver");  
        ds.setUrl("jdbc:mysql://localhost:3306/shiro");  
        ds.setUsername("root");  
        ds.setPassword("");
        
        JdbcRealm jdbcRealm = new JdbcRealm();
        jdbcRealm.setDataSource(ds);
        jdbcRealm.setPermissionsLookupEnabled(true);    // 启用权限查找
        securityManager.setRealm(jdbcRealm);
        
        // 将securityManager 设置到SecurityUtils 全局使用
        SecurityUtils.setSecurityManager(securityManager);
        
        Subject subject = SecurityUtils.getSubject();
        subject.login(new UsernamePasswordToken("admin", "123"));
        Assert.assertTrue(subject.isAuthenticated());
    }
    
}
