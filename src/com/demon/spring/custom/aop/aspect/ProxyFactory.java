package com.demon.spring.custom.aop.aspect;

import com.demon.spring.custom.aop.AdvisorSupport;
import com.demon.spring.custom.aop.AopProxy;
import com.demon.spring.custom.aop.CGlibDynamicAopProxy;
import com.demon.spring.custom.aop.JdkDynamicAopProxy;

/**
 * 代理对象工厂
 * @author xuliang
 * @since 2018年12月19日 下午5:23:51
 *
 */
public class ProxyFactory extends AdvisorSupport implements AopProxy {

    @Override
    public Object getProxy() {
        return createAopProxy().getProxy();
    }
    
    private AopProxy createAopProxy(){
        Class<?>[] interfaces = this.getTargetSource().getInterfaces();
        // 无接口，则使用cglib 代理
        if(interfaces == null || interfaces.length <= 0){
            return new CGlibDynamicAopProxy(this);
        }
        // 默认有接口，使用jdk 代理
        return new JdkDynamicAopProxy(this);
    }

}
