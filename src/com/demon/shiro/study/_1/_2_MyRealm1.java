package com.demon.shiro.study._1;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * Realm：域，Shiro从从Realm获取安全数据（如用户、角色、权限），
 * 也就是SecurityManager 要验证用户身份，那么它需要从Realm获取相应的用户进行比较以确定用户身份是否合法；
 * 也需要从Realm得到用户相应的角色/权限进行验证用户是否能进行操作；
 * 可以把Realm看成DataSource，即安全数据源。如我们之前的ini配置方式将使用org.apache.shiro.realm.text.IniRealm。
 * </pre>
 * @author xuliang
 * @since 2019年7月11日 上午10:55:30
 *
 */
public class _2_MyRealm1 implements Realm {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 返回名称
     */
    @Override
    public String getName() {
        return "_1_2_MyRealm1";
    }

    /**
     * 是否支持该类型的token
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        // 仅支持 UsernamePasswordToken 这种类型
        return token instanceof UsernamePasswordToken;
    }

    /**
     * 根据token 获取认证信息
     */
    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        logger.info("_1_2_MyRealm1 getAuthenticationInfo begin...");
        String username = (String)token.getPrincipal();     // 用户名
        String password = new String((char[])token.getCredentials());   // 密码
        // 身份认证
        if(!"admin".equals(username)){
            throw new UnknownAccountException("用户名不存在");
        }
        if(!"123".equals(password)){
            throw new IncorrectCredentialsException("密码不正确");
        }
        // 如果身份认证成功，返回一个 AuthenticationInfo 实现
        return new SimpleAuthenticationInfo(username, password, getName());
    }
    
}
