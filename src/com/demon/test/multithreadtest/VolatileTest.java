package com.demon.test.multithreadtest;

import java.util.concurrent.TimeUnit;

/**
 * volatile 关键字
 * Java 内存模型（JMM）规定，所有的变量数据都保存在主内存中，而每个线程都有自己的工作内存（高速缓存）
 * 
 * 线程工作时，会将主内存中的数据拷贝到工作内存中，对数据的操作都是基于工作内存的，不能操作主内存和其他线程工作内存的数据，然后之后再把更新之后存的数据刷新到主内存中
 * 
 * volatile 修饰变量后，会强制将写操作造成的数据改变立即刷新到主内存中，并清空其他线程工作内存中相应的数据，强制其从主内存重新获取数据。
 * volatile 修饰修饰后，线程并不是直接从主内存获取数据，还是需要拷贝数据到工作内存
 * volatile 修饰后，只能保证内存可见性、防止JVM 指令重排，并不能保证线程的原子性，也就是不能保证线程安全性
 * 
 * 
 * 例子：主线程关闭子线程
 * 
 * @author xuliang
 * @since 2018年10月12日 上午10:58:23
 *
 */
public class VolatileTest implements Runnable {

    private static volatile boolean run = true;
    
    @Override
    public void run() {
        while(run){
            System.out.println(Thread.currentThread().getName() + " 正在执行");
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " 执行完毕");
    }
    
    private void stop(){
        System.out.println("停止线程");
        run = false;
    }
    
    public static void main(String[] args) throws InterruptedException {
        VolatileTest t = new VolatileTest();
        new Thread(t).start();
        System.out.println("main 线程运行中");
        
        TimeUnit.SECONDS.sleep(2);
        
        t.stop();
        int i=0;
        while(i<100){
            i++;
            System.out.println(i);
            TimeUnit.MILLISECONDS.sleep(50);
        }
        System.out.println("main 线程退出了");
    }

}
