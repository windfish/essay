package com.demon.jvm._2memory;

import java.util.ArrayList;
import java.util.List;

/**
 * Java 堆内存溢出
 * VM args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails
 * @author xuliang
 * @since 2018年8月20日 上午11:34:51
 *
 */
public class HeapOOM {

    public static void main(String[] args) {
        List<OOMObject> list = new ArrayList<>();
        while(true){
            list.add(new OOMObject());
        }
    }
    
    static class OOMObject {
        
    }
    
}
