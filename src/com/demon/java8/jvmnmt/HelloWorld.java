package com.demon.java8.jvmnmt;

public class HelloWorld {

    public static void main(String[] args) {
        System.out.println("Hello World");
    }
    
}

/**
 * NMT（Native Memory Tracking 本机内存跟踪）分析JVM 内存
 * -XX:NativeMemoryTracking=summary  NMT 开启summary 模式，只收集汇总信息；detail 模式，收集每次调用的信息，off 模式，关闭。开启NMT 会有 5%~10% 的性能损耗
 * -XX:+UnlockDiagnosticVMOptions -XX:+PrintNMTStatistics 在JVM 退出的时候打印内存使用情况
 * 
 * JDK 提供了jcmd 命令来访问NMT 数据
 * jcmd <pid> VM.native_memory [summary | detail | baseline | summary.diff | detail.diff | shudown] scale=[GB | MB | KB]
        summary 只打印打印按分类汇总的内存用法
        detail  打印按分类汇总的内存用法、virtual memory map和每次内存分配调用
        baseline    创建内存快照，以比较不同时间的内存差异
        summary.diff    打印自上次baseline到现在的内存差异，显示汇总信息
        detail.diff 打印自上次baseline到现在的内存差异, 显示详细信息
        shutdown    关闭NMT功能
        scale   指定内存单位，默认为KB
 * 
 * 
 *  C:\Users\Administrator\Desktop>javac HelloWorld.java
    C:\Users\Administrator\Desktop>java -XX:NativeMemoryTracking=summary -XX:+UnlockDiagnosticVMOptions -XX:+PrintNMTStatistics HelloWorld
    Hello World
    
    Native Memory Tracking:
    
    Total: reserved=3435682KB, committed=236082KB
    -                 Java Heap (reserved=2021376KB, committed=126976KB)            堆内存，reserved 保留的空间，committed 实际可使用的空间
                                (mmap: reserved=2021376KB, committed=126976KB)
    
    -                     Class (reserved=1061997KB, committed=10093KB)             Class 内存占用，统计的是Java 类元数据所占用的空间
                                (classes #393)                                      JVM 可以通过参数调整元数据空间大小  -XX:MaxMetaspaceSize=value
                                (malloc=5229KB #145)                                这里基本是Bootstrap 加载的核心类库所占的内存，可以使用 -XX:InitialBootClassLoaderMetaspaceSize=30720 调整启动类加载器元数据区
                                (mmap: reserved=1056768KB, committed=4864KB)
    
    -                    Thread (reserved=15423KB, committed=15423KB)               线程的内存占用，包括主线程、Cleaner 线程等，也包括GC 等本地线程
                                (thread #16)                                        JDK8 默认GC方式是ParallelGC（-XX:+UseParallelGC），线程数为16；
                                (stack: reserved=15360KB, committed=15360KB)        若使用G1 GC（-XX:+UseG1GC），线程数为24，内存占用也要高点
                                (malloc=45KB #81)                                   JIT 编译器是默认开启的，若将其关闭（-XX:-TieredCompilation），本地线程数也会减少
                                (arena=18KB #30)
    
    -                      Code (reserved=249633KB, committed=2569KB)               CodeCache 相关内存，也就是JIT compile 存储编译热点方法等信息占用的内存，关闭JIT 后，内存占用也会降低
                                (malloc=33KB #307)                                  -XX:InitialCodeCacheSize=value 初始大小
                                (mmap: reserved=249600KB, committed=2536KB)         -XX:ReservedCodeCacheSize=value 最大大小
    
    -                        GC (reserved=79755KB, committed=73523KB)               GC部分，G1 GC的话，由于其本身的设施和数据结构非常复杂，内存会高出不少
                                (malloc=5771KB #117)                                Serial GC 的话，内存占用最少，可以大大降低单个function 的启动和运动开销
                                (mmap: reserved=73984KB, committed=67752KB)         
    
    -                  Compiler (reserved=133KB, committed=133KB)                   JIT 的开销，关闭TieredCompilation 会降低内存
                                (malloc=2KB #26)
                                (arena=131KB #3)
    
    -                  Internal (reserved=5376KB, committed=5376KB)                 包含堆外内存 Direct Buffer
                                (malloc=5312KB #1331)
                                (mmap: reserved=64KB, committed=64KB)
    
    -                    Symbol (reserved=1486KB, committed=1486KB)
                                (malloc=934KB #96)
                                (arena=552KB #1)
    
    -    Native Memory Tracking (reserved=38KB, committed=38KB)
                                (malloc=3KB #40)
                                (tracking overhead=35KB)
    
    -               Arena Chunk (reserved=465KB, committed=465KB)
                                (malloc=465KB)


-----------------------------------------------------------------------------
        使用G1 GC后，内存和线程数都高了
    -                    Thread (reserved=23646KB, committed=23646KB)
                                (thread #24)
                                (stack: reserved=23552KB, committed=23552KB)
                                (malloc=67KB #122)
                                (arena=27KB #46)




 **/
