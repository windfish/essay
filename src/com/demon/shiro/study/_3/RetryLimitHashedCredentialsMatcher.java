package com.demon.shiro.study._3;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 限制密码重试次数
 * <br>
 * 使用缓存记录重试次数和超时时间；验证通过，则从缓存中清除记录
 * 
 * @author xuliang
 * @since 2019年7月12日 上午10:46:14
 *
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String)token.getPrincipal();
        AtomicInteger count = cache.getUnchecked(username);
        if(count.incrementAndGet() > 5){
            throw new ExcessiveAttemptsException();
        }
        
        boolean match = super.doCredentialsMatch(token, info);
        if(match){
            cache.invalidate(username);
        }
        return match;
    }
    
    private static LoadingCache<String, AtomicInteger> cache = CacheBuilder.newBuilder()
                            .refreshAfterWrite(1, TimeUnit.HOURS)
                            .build(new CacheLoader<String, AtomicInteger>() {
                                @Override
                                public AtomicInteger load(String username) throws Exception {
                                    return new AtomicInteger(0);
                                } 
                            });
    
    public static void main(String[] args) {
        AtomicInteger count = cache.getUnchecked("user");
        System.out.println("--------------"+count.intValue());
        count.incrementAndGet();
        System.out.println("--------------"+count.intValue());
        System.out.println("--------------"+cache.getUnchecked("user").intValue());
        cache.invalidate("user");
        System.out.println("--------------"+cache.getUnchecked("user").intValue());
    }
    
}
