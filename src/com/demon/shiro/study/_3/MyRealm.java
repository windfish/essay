package com.demon.shiro.study._3;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.realm.AuthenticatingRealm;

public class MyRealm extends AuthenticatingRealm {

    private PasswordService passwordService;
    
    public void setPasswordService(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken paramAuthenticationToken)
            throws AuthenticationException {
        return new SimpleAuthenticationInfo("admin", passwordService.encryptPassword("123"), getName());
    }
    
}
