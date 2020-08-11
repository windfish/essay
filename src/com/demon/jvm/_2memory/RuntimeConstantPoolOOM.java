package com.demon.jvm._2memory;

import java.util.ArrayList;
import java.util.List;

/**
 * 运行时常量池溢出
 * JDK 1.6及之前，字符串常量池位于永久代内，会报错 OOM：PermGen space
 * JDK 1.7之后，字符串常量池被移出了永久代，不会报错
 * VM Args: -XX:PermSize=10m -XX:MaxPermSize=10m
 * @author xuliang
 * @since 2018年8月20日 下午3:12:29
 *
 */
public class RuntimeConstantPoolOOM {

    public static void main(String[] args) {
        String str1 = new StringBuilder("计算机").append("软件").toString();
        /*
         * JDK 1.6 返回false；JDK 1.7 返回true
         * JDK 1.6 intern() 方法会将首次遇到的字符串实例复制到常量池（永久代中），返回的是永久代中字符串的引用，而StringBuilder创建的字符串实例在Java堆中
         * JDK 1.7 intern() 方法不会再复制实例，而是只记录首次出现的字符串的引用，返回的引用与StringBuilder创建的字符串实例是同一个
         */
        System.out.println(str1.intern() == str1);  
        
        String str2 = new StringBuilder("ja").append("va").toString();
        System.out.println(str2.intern() == str2);  // 常量池中默认有java这个字符串，所以返回false


        List<String> list = new ArrayList<String>();
        int i=0;
        while(true){
            list.add(String.valueOf(i++).intern());
        }
        
    }
    
}
