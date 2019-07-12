package com.demon.shiro.study._3;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.ByteSource;

/**
 * 
 * @author xuliang
 * @since 2019年7月12日 上午10:20:26
 *
 */
public class MyRealm2 extends AuthenticatingRealm {

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        // 从数据库或其他地方取 username、password、salt，来构造SimpleAuthenticationInfo，与传入的token 来比较。
        // 在HashedCredentialsMatcher 的doCredentialsMatch 方法中，会校验结果
        String username = "admin";
        String password = "123456";
        String salt = HashRule.generatorSalt();
        String hashPassword = HashRule.hashPassword(password, username, salt);
        
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(username, hashPassword, getName());
        info.setCredentialsSalt(ByteSource.Util.bytes(username + salt));
        return info;
    }

}
