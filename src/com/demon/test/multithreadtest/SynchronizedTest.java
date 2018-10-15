package com.demon.test.multithreadtest;

/**
 * <pre>
 * 同步普通方法，锁的对象是当前对象
 * 同步静态方法，锁的对象是Class 对象
 * 同步块，锁的是()中的对象
 * 
 * synchronized 原理：JVM 是通过进入、退出对象监视器（Monitor）来实现方法、同步块的同步的
 * 编译后，在同步方法前加一个 monitorenter 指令，在退出方法和异常处插入 monitorexit 指令
 * 
 * javac -p SynchronizedTest
 * 
 * public class com.demon.test.sync.SynchronizedTest {
      public com.demon.test.sync.SynchronizedTest();
        Code:
           0: aload_0
           1: invokespecial #1                  // Method java/lang/Object."<init>":()V
           4: return
    
      public static void main(java.lang.String[]);
        Code:
           0: ldc           #2                  // class com/demon/test/sync/SynchronizedTest
           2: dup
           3: astore_1
           4: <b>**monitorenter**</b>
           5: getstatic     #3                  // Field java/lang/System.out:Ljava/io/PrintStream;
           8: ldc           #4                  // String synchronized
          10: invokevirtual #5                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
          13: aload_1
          14: <b>**monitorexit**</b>
          15: goto          23
          18: astore_2
          19: aload_1
          20: <b>**monitorexit**</b>
          21: aload_2
          22: athrow
          23: return
        Exception table:
           from    to  target type
               5    15    18   any
              18    21    18   any
    }
 * 
 * </pre>
 * 
 * @author xuliang
 * @since 2018年10月12日 上午10:01:49
 *
 */
public class SynchronizedTest {

    public static void main(String[] args) {
        synchronized (SynchronizedTest.class) {
            System.out.println("synchronized");
        }
    }
    
}
