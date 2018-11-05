package com.demon.java8.thread;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

/**
 * 
 * @author xuliang
 * @since 2018年11月5日 下午5:29:37
 *
 */
public class CAS {

    private final AtomicLongFieldUpdater<AtomicBTreePartion> lockUpdate = AtomicLongFieldUpdater.newUpdater(AtomicBTreePartion.class, "lock");
    
    class AtomicBTreePartion {
        private volatile long lock;
        
        public void acquireLock(){
            long t = Thread.currentThread().getId();
            while(!lockUpdate.compareAndSet(this, 0L, t)){
                // 循环再次修改，相当于尝试获取锁
            }
        }
        
        public void releaseLock(){
            long t = Thread.currentThread().getId();
            while(!lockUpdate.compareAndSet(this, t, 0L)){
                // 循环再次修改，相当于尝试释放锁
            }
        }
    }
    
}

