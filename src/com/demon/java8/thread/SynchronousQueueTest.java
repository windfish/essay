package com.demon.java8.thread;

import java.util.concurrent.SynchronousQueue;

public class SynchronousQueueTest {

    public static void main(String[] args) {
        SynchronousQueue<Integer> queue = new SynchronousQueue<>();
        
        Thread put = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=1;i<=10;i++){
                    try {
                        System.out.println("queue pre put");
                        queue.put(i);
                        System.out.println("queue put");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        
        Thread take = new Thread(new Runnable() {
            @Override
            public void run() {
                int i=1;
                while(i<=10){
                    try {
                        Integer take2 = queue.take();
                        System.out.println("take: "+take2);
                        Thread.sleep(2000);
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        
        put.start();
        take.start();
        
    }
    
}
