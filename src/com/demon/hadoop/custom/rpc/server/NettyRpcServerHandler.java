package com.demon.hadoop.custom.rpc.server;

import com.demon.hadoop.custom.rpc.protocol.RpcRequest;
import com.demon.hadoop.custom.rpc.protocol.RpcResponse;
import com.demon.hadoop.custom.rpc.service.HelloService;
import com.demon.hadoop.custom.rpc.service.impl.HelloServiceImpl;
import com.demon.util.JsonUtil;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class NettyRpcServerHandler extends ChannelHandlerAdapter {

    // 提供的服务列表，接口-实现类 映射关系
    private static final Map<Class, Class> serviceMap = new HashMap<>();

    static {
        System.out.println("init external service begin...");
        serviceMap.put(HelloService.class, HelloServiceImpl.class);

        System.out.println("external service list: " + JsonUtil.toJsonPrettyString(serviceMap));
        System.out.println("init external service end...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        channelRead0(ctx, msg);
    }

    private void channelRead0(ChannelHandlerContext ctx, Object msg) {
        System.out.println(msg);
        RpcResponse response = new RpcResponse();
        RpcRequest request = (RpcRequest) msg;
        response.setRequestId(request.getRequestId());

        try {
            // 收到请求开始处理请求
            Object obj = handler(request);
            response.setResult(obj);
        } catch (Throwable e) {
            response.setThrowable(e);
            e.printStackTrace();
        }
        System.out.println(response);
        ctx.writeAndFlush(response);
    }

    private Object handler(RpcRequest request) throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, InvocationTargetException {
        // 通过反射创建服务类
        Class<?> clazz = Class.forName(request.getClassName());
        Class<?> implClass = serviceMap.get(clazz);
        System.out.println("service impl: " + implClass);
        Object serviceBean = implClass.newInstance();

        // 通过反射创建服务类的方法
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        FastClass fastClass = FastClass.create(serviceBean.getClass());
        FastMethod fastMethod = fastClass.getMethod(methodName, parameterTypes);

        return fastMethod.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("netty rpc server exception. " + cause.getMessage());
        ctx.close();
    }
}
