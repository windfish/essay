package com.demon.hadoop.custom.rpc.codec;

public interface Serialization {

    /**
     * 序列化
     */
    <T> byte[] serialize(T t);

    /**
     * 反序列化
     */
    <T> T deserialize(byte[] data, Class<?> clazz);

}
