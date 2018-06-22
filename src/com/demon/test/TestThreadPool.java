package com.demon.test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 如何合理地估算线程池大小 
 * 如果是CPU密集型应用，则线程池大小设置为N+1 
        如果是IO密集型应用，则线程池大小设置为2N+1 
 * http://ifeve.com/how-to-calculate-threadpool-size/ 
 * 
 * @author xuliang
 * @since 2018年5月22日 上午9:13:03
 *
 */
public class TestThreadPool {

    private static final int NTHREADS = 9;    
    private static final ExecutorService exec = Executors.newFixedThreadPool(NTHREADS);  
//        private static final ExecutorService exec = Executors.newCachedThreadPool();  
    // 使用 CachedThreadPool 比较耗内存，并发 200+的时候 会造成内存溢出  
    static long tempcount = System.currentTimeMillis()/1000;// 用于计算每秒时间差  
    static int prenum = 200;// 用于计算任务数量差  
    static int nums = 200;// 总任务数  
    static int uses = 0;// 耗时 计数  
      
    static long allstart = System.currentTimeMillis();// 程序启动时间 用于计算总耗时  
    static long logstime = 0;// 日志总耗时  
      
    public static void main(String[] args)  {  
        //QPS（TPS）：每秒钟request/事务 数量 ，QPS（TPS）= 并发数/平均响应时间  
        //并发数： 系统同时处理的request/事务数  
        //响应时间：  一般取平均响应时间  
          
        for (int i = 0; i < nums; i++) {  
            Runnable task = new  Runnable() {    
                @Override  
                public void run() {    
                    try {  
                        long start = System.currentTimeMillis();  
                        task();  
                        long use = System.currentTimeMillis()-start;  
//                          System.out.println("单个耗时"+use);  
                        getCurrentThreads(use);  
                    } catch (Exception e) {  
                        e.printStackTrace();  
                    }  
                }  
                  
                /** 
                 * 任务目标 
                 */  
                public void task(){  
                    try {
                        System.out.println("task doing");
                        TimeUnit.SECONDS.sleep(new Random().nextInt(5));
                    } catch (Exception e) {  
                        e.printStackTrace();  
                    }  
                }  
            };    
            exec.execute(task);    
        }  
    }  
  
    /** 
     * 每个任务分析 
     * @param use 单个任务耗时 
     */  
    public static void getCurrentThreads(long use){  
//          Map<Thread, StackTraceElement[]> maps = Thread.getAllStackTraces();  
//          System.out.println("Threads:"+maps.size()+"-currentThread:"+Thread.currentThread().getName()+"-"+Thread.currentThread().getId());  
          
        long start = System.currentTimeMillis();  
          
        prenum = prenum-1;// 任务数量 进度扣减  
        uses += use;  
          
        long now = System.currentTimeMillis()/1000;   
        if(now > tempcount){// 每秒输出一次  
            long freeMemory=Runtime.getRuntime().freeMemory() / 1024 / 1024;//已使用内存  
            long totalMemory=Runtime.getRuntime().totalMemory() / 1024 / 1024;//总共可使用内存  
            int cpu = Runtime.getRuntime().availableProcessors();//可用cpu逻辑处理器  
            
            System.out.println("---------------------------");
            System.out.printf("可用的cpu:%s ", cpu);
            System.out.printf("第%s秒: ", now-allstart/1000);  
            int ts = (nums-prenum);//每秒事务数  
            System.out.printf("每秒处理数:%s ", ts);  
            System.out.printf("平均耗时:%s ", ts==0?0:uses/ts);  
            System.out.printf("进度:%s ", nums);  
            System.out.printf("剩余:%s毫秒 ", nums*ts);  
            System.out.printf("可用内存:%sm ", freeMemory);  
            System.out.printf("可用总内存:%sm \n", totalMemory);  
            tempcount = now;  
            nums = prenum;  
            uses =0;  
        }  
          
        logstime += System.currentTimeMillis()-start;// 日志耗时累计  
          
        // 当任务执行完了以后 计算总耗时  
        if(prenum==0){  
            long alluse = System.currentTimeMillis()-allstart;  
            System.out.printf("总耗时%s毫秒,其中日志耗时%s毫秒\n",alluse,logstime);  
            System.exit(0);  
        }  
          
    }  
    
}
