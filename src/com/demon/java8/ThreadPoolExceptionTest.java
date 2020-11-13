package com.demon.java8;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author xuliang
 * @since 2020/11/13 13:56
 */
public class ThreadPoolExceptionTest {

    private static ExecutorService executors = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {
        Future<?> submit = executors.submit(() -> {
            System.out.println("thread start...");
            String s = null;
            System.out.println(s.length());

            System.out.println("thread end...");
        });

        try {
            Object o = submit.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
