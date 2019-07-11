package com.demon.shiro;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 授权，基于注解方式
 * @author xuliang
 * @since 2019年7月11日 上午9:38:48
 *
 */
@Component
public class AuthorizationTest {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @RequiresAuthentication
    public void updateUser(){
        // 要求 Subject 在当前的 session中已经被验证。
        logger.info("updateUser");
    }
    
    @RequiresGuest
    public void signUp(){
        // 要求当前Subject是一个“guest(访客)”，也就是，在访问或调用被注解的类/实例/方法时，他们没有被认证或者在被前一个Session 记住。
        logger.info("signUp");
    }
    
    @RequiresPermissions("user:create")
    public void createUser(){
        // 要求当前Subject在执行被注解的方法时具备一个或多个对应的权限。
        logger.info("createUser");
    }
    
    @RequiresUser
    public void modifyUser() {
        // 要求当前 Subject 是一个程序用户，“程序用户”是一个已知身份的 Subject，或者在当前 Session 中被验证过或者在以前的 Session 中被记住过。
        logger.info("modifyUser");
    }
    
}
