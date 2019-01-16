package com.demon.java8.multithread;

/**
 * 交替打印奇偶数
 * 
 * @author xuliang
 * @since 2018年10月12日 下午1:52:44
 *
 */
public class NumberTest {

    private int num = 1;
    private boolean flag = true;
    
    private static class Thread1 extends Thread {

        private NumberTest number;
        
        public Thread1(NumberTest number) {
            this.number = number;
        }
        
        @Override
        public void run() {
            while(number.num <= 100){
                synchronized (NumberTest.class) {
                    if(number.flag){
                        System.out.println(Thread.currentThread().getName() + " : " + number.num);
                        number.num += 1;
                        number.flag = false;
                        NumberTest.class.notify();
                    }else{
                        try {
                            NumberTest.class.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        
    }
    
    private static class Thread2 extends Thread {

        private NumberTest number;
        
        public Thread2(NumberTest number) {
            this.number = number;
        }
        
        @Override
        public void run() {
            while(number.num <= 100){
                synchronized (NumberTest.class) {
                    if(!number.flag){
                        System.out.println(Thread.currentThread().getName() + " : " + number.num);
                        number.num += 1;
                        number.flag = true;
                        NumberTest.class.notify();
                    }else{
                        try {
                            NumberTest.class.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        
    }
    
    public static void main(String[] args) {
        NumberTest number = new NumberTest();
        
        new Thread1(number).start();
        new Thread2(number).start();
    }
    
}
