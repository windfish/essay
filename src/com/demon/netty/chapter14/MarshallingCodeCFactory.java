package com.demon.netty.chapter14;


import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 * MarshallingCodeCFactory 工厂类，用来获取JBoss Marshalling 类
 * @author xuliang
 * @since 2018/2/22 14:43
 */
public final class MarshallingCodeCFactory {

    /**
     * 创建 Jboss Marshing 解码器 MarshingDecoder
     */
    public static MarshallingDecoder buildMarshallingDecoder(){
        // 通过 Marshalling 工具类，获取 MarshallerFactory 实例；serial 表示创建的是Java 序列化工厂对象
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
        MarshallingDecoder decoder = new MarshallingDecoder(provider, 1024);
        return decoder;
    }

    /**
     * 创建 Jboss Marshing 编码器 MarshallingEncoder
     */
    public static MarshallingEncoder buildMarshallingEncoder(){
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
        MarshallingEncoder encoder = new MarshallingEncoder(provider);
        return encoder;
    }

    /**
     * 创建自定义解码器 MarshingDecoder
     */
    public static NettyMarshallingDecoder buildCustomMarshallingDecoder(){
        // 通过 Marshalling 工具类，获取 MarshallerFactory 实例；serial 表示创建的是Java 序列化工厂对象
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
        NettyMarshallingDecoder decoder = new NettyMarshallingDecoder(provider, 1024);
        return decoder;
    }

    /**
     * 创建 Jboss Marshing 编码器 MarshallingEncoder
     */
    public static NettyMarshallingEncoder buildCustomMarshallingEncoder(){
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
        NettyMarshallingEncoder encoder = new NettyMarshallingEncoder(provider);
        return encoder;
    }

}
