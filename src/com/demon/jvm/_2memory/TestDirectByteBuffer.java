package com.demon.jvm._2memory;

import java.nio.ByteBuffer;
import sun.nio.ch.DirectBuffer;

/**
 * 直接内存分配
 * @author xuliang
 * @since 2018年8月20日 下午4:39:05
 *
 */
public class TestDirectByteBuffer {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        System.out.println(buffer.isDirect());
        if(buffer.isDirect()){
            DirectBuffer db = (DirectBuffer)buffer;
            db.cleaner().clean();
        }
        
    }
    
}
