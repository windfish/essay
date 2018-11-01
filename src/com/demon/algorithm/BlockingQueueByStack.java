package com.demon.algorithm;

import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用栈实现阻塞队列
 * 由于栈是LIFO，而队列是FIFO，因此，使用两个栈来实现
 * 当写入时，写入第一个栈；当取出时，先判断第二个栈是否有值，有值直接取出，没有值的就把第一个栈的数据顺次取出写入第二个栈，然后从第二个栈取数据
 * 要求阻塞的话，就在写入时，判断当前容量是否已满，已满不能写入
 * 
 * @author xuliang
 * @since 2018年11月1日 下午4:31:17
 *
 */
public class BlockingQueueByStack<T> {
    
    private Stack<T> s1;
    private Stack<T> s2;
    private int capacity;
    private int size;
    private ReentrantLock lock = new ReentrantLock();
    
    public BlockingQueueByStack(int capacity) {
        s1 = new Stack<>();
        s2 = new Stack<>();
        this.capacity = capacity;
    }
    
    public boolean put(T t){
        lock.lock();
        try{
            if(size == capacity){
                return false;
            }else{
                s1.push(t);
                size++;
                return true;
            }
        }finally {
            lock.unlock();
        }
    }
    
    public T poll(){
        lock.lock();
        try{
            if(s1.isEmpty() && s2.isEmpty()){
                return null;
            }
            if(s2.isEmpty()){
                while(!s1.isEmpty()){
                    s2.push(s1.pop());
                }
            }
            T t = s2.pop();
            size--;
            return t;
        }finally {
            lock.unlock();
        }
    }
    
    public int size(){
        return this.size;
    }
    
    public static void main(String[] args) throws InterruptedException {
        final BlockingQueueByStack<Integer> queue = new BlockingQueueByStack<>(5);
        /*for(int i=1;i<10;i++){
            queue.put(i);
        }
        Integer i = null;
        int index = 100;
        while((i=queue.poll()) != null){
            System.out.println(i);
            index++;
            if(index < 110){
                queue.put(index);
            }
        }
        System.out.println(queue.size());*/
        
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<100;i++){
                    boolean put = queue.put(i);
                    while(!put){
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        put = queue.put(i);
                    }
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    Integer poll = queue.poll();
                    if(poll == null){
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else{
                        System.out.println(poll);
                        if(poll == 99){
                            break;
                        }
                    }
                }
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
    
}
