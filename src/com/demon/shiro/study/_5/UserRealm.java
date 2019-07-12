package com.demon.shiro.study._5;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

public class UserRealm extends AuthorizingRealm {

    private UserService userService;
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String)principals.getPrimaryPrincipal();  
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();  
        authorizationInfo.setRoles(userService.findRoles(username));
        authorizationInfo.setStringPermissions(userService.findPermissions(username));
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String)token.getPrincipal();  
        User user = userService.findByUsername(username);  
        if(user == null) {  
            throw new UnknownAccountException();//没找到帐号  
        }  
        if(Boolean.TRUE.equals(user.getLocked())) {  
            throw new LockedAccountException(); //帐号锁定  
        }  
        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以在此判断或自定义实现  
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(  
                user.getUsername(), //用户名  
                user.getPassword(), //密码  
                ByteSource.Util.bytes(user.getCredentialsSalt()),   //salt=username+salt  
                getName()  //realm name  
        );  
        return authenticationInfo;  
    }

}
