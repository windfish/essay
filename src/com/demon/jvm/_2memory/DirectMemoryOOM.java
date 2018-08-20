package com.demon.jvm._2memory;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

/**
 * 直接内存溢出
 * VM Args: -Xmx20m -XX:MaxDirectMemorySize=10m
 * @author xuliang
 * @since 2018年8月20日 下午4:17:48
 *
 */
public class DirectMemoryOOM {

    private static final int _1MB = 1024 * 1024;
    
    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        while(true){
            unsafe.allocateMemory(_1MB);
        }
    }
    
}
