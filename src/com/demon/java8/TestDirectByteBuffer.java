package com.demon.java8;

import java.nio.ByteBuffer;
import sun.nio.ch.DirectBuffer;

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
