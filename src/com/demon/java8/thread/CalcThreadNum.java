package com.demon.java8.thread;

/**
 * <pre>
 * 五个线程
 * Reference Handler 处理引用对象本身的垃圾回收
 * Finalizer 处理用户的Finalize 方法
 * Signal Dispatcher 外部JVM 的命令转发器
 * Attach Listener 接收外部命令，如jmap、jstack
 * main 主线程
 * </pre>
 * 
 * @author xuliang
 * @since 2018年11月1日 上午10:34:34
 *
 */
public class CalcThreadNum {

    public static void main(String[] args) {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        while(group.getParent() != null){
            group = group.getParent();
        }
        int count = group.activeCount();
        System.out.println("thread count: "+count);
        
        Thread[] threads = new Thread[count];
        group.enumerate(threads);
        for(Thread t: threads){
            System.out.println(t.getName());
        }
        
        System.out.println("Hello World");
    }
    
}
