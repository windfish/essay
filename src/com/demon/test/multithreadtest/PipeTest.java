package com.demon.test.multithreadtest;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

/**
 * 两个线程利用 PipedWriter 和 PipedReader 通信，生产者
 * 
 * @author xuliang
 * @since 2018年10月12日 上午11:34:40
 *
 */
public class PipeTest {

    public static void piped() throws Exception{
        // 面向字符，PipedInpuntStream 面向字节
        PipedWriter writer = new PipedWriter();
        PipedReader reader = new PipedReader();
        
        // 输入输出流链接
        writer.connect(reader);
        
        Thread t1 = new Thread(){
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " running");
                try{
                    for(int i=0;i<10;i++){
                        writer.write(i);
                        Thread.sleep(100);
                    }
                }catch (Exception e) {
                }finally {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        
        Thread t2 = new Thread(){
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " running");
                try{
                    int msg = -1;
                    while((msg = reader.read()) != -1){
                        System.out.println(Thread.currentThread().getName() + " msg=" + msg);
                    }
                }catch (Exception e) {
                }finally {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        
        t2.start();
        t1.start();
    }
    
    public static void main(String[] args) throws Exception {
        PipeTest.piped();
    }
    
}
