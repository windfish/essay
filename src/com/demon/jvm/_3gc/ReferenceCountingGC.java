package com.demon.jvm._3gc;

/**
 * 引用计数算法实验，循环引用时，对象是否会被回收
 * @JVM args: -XX:+PrintGCDetails
 */
public class ReferenceCountingGC {

    public Object instance = null;

    private static final int _1M = 1024 * 1024;

    private byte[] bigSize = new byte[2 * _1M];

    public static void testGC(){
        ReferenceCountingGC objA = new ReferenceCountingGC();
        ReferenceCountingGC objB = new ReferenceCountingGC();

        objA.instance = objB;
        objB.instance = objA;

        objA = null;
        objB = null;

        System.gc();
    }

    public static void main(String[] args) {
        // 可以看到对象还是被回收了，证明虚拟机并不是通过引用计数算法来判断两个对象是否存活的
        testGC();
    }
}
