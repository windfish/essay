package com.demon.hadoop.custom.rpc.common;

import com.demon.hadoop.custom.rpc.client.NettyRpcClient;
import com.demon.hadoop.custom.rpc.client.RpcClient;
import com.demon.hadoop.custom.rpc.protocol.RpcRequest;
import com.demon.hadoop.custom.rpc.protocol.RpcResponse;
import com.demon.util.JsonUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * 反射工具类
 */
public class ProxyFactory {

    /**
     * 根据接口类，构造代理对象
     */
    public static <T> T create(Class<T> interfaceClass){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class[]{interfaceClass}, new RpcInvoker<T>(interfaceClass));
    }

    static class RpcInvoker<T> implements InvocationHandler {
        private Class<T> clazz;

        public RpcInvoker(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("代理方法开始执行...");

            /**
             * 构造请求对象
             */
            RpcRequest request = new RpcRequest();

            String requestId = UUID.randomUUID().toString();
            String className = method.getDeclaringClass().getName();
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();

            request.setRequestId(requestId);
            request.setClassName(className);
            request.setMethodName(methodName);
            request.setParameterTypes(parameterTypes);
            request.setParameters(args);
            System.out.println(JsonUtil.toJsonString(request));

            // 构造客户端
            RpcClient nettyRpcClient = new NettyRpcClient(Properties.HOST, Properties.PORT);
            // 连接服务器
            nettyRpcClient.connect();
            // 发送请求
            RpcResponse response = nettyRpcClient.send(request);
            System.out.println("client send result: " + response);
            return response.getResult();
        }
    }


}
