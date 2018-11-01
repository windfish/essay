package com.demon.java8.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 死锁问题定位：
 * 首先，获取进程PID，jps 或ps 命令
 * 然后，jstack 获取线程栈，jstack PID
 * 分析输出：DeadLock.txt
 * 
 * 也可以使用ThreadMXBean 扫描服务进程、定位死锁位置。需注意，对线程进行快照是重量级的操作，注意频度和时机。
 * </pre>
 * 
 * @author xuliang
 * @since 2018年11月1日 上午11:32:06
 *
 */
public class DeadLockTest extends Thread {

    private String first;
    private String second;
    
    public DeadLockTest(String name, String first, String second) {
        super(name);
        this.first = first;
        this.second = second;
    }
    
    @Override
    public void run() {
        synchronized (first) {
            System.out.println(this.getName() + " : " + first);
            try {
                Thread.sleep(1000);
                synchronized (second) {
                    System.out.println(this.getName() + " : " + second);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        Runnable deadLockCheck = new Runnable() {
            @Override
            public void run() {
                long[] threadIds = mxBean.findDeadlockedThreads();
                if(threadIds != null){
                    ThreadInfo[] threadInfos = mxBean.getThreadInfo(threadIds);
                    System.out.println("Detected DeadLock Thread: ");
                    for(ThreadInfo info: threadInfos){
                        System.out.println(info.getThreadName());
                    }
                }
            }
        };
        ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor();
        schedule.scheduleAtFixedRate(deadLockCheck, 5, 10, TimeUnit.SECONDS);
        
        String lockA = "A";
        String lockB = "B";
        DeadLockTest t1 = new DeadLockTest("threadA", lockA, lockB);
        DeadLockTest t2 = new DeadLockTest("threadB", lockB, lockA);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
    
}
