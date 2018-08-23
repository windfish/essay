package com.demon.jvm._3gc;

/**
 * 一个对象的自我拯救
 * 1、对象可以在被 GC 时自我拯救
 * 2、这种自救的机会只有一次，因为一个对象的 finalize() 方法最多只会被系统自动调用一次
 * 
 * @author xuliang
 * @since 2018年8月21日 上午10:36:28
 *
 */
public class FinalizeEscapeGC {

    public static FinalizeEscapeGC SAVE_HOOK = null;
    
    public void isAlive() {
        System.out.println("yes, i am still alive :)");
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("finalize method executed!");
        FinalizeEscapeGC.SAVE_HOOK = this;
    }
    
    public static void main(String[] args) throws InterruptedException {
        SAVE_HOOK = new FinalizeEscapeGC();
        
        // 对象第一次成功拯救自己
        SAVE_HOOK = null;
        System.gc();
        // 因为 finalize 方法优先级很低，所以暂停0.5秒等待它
        Thread.sleep(500);
        if(SAVE_HOOK != null){
            SAVE_HOOK.isAlive();
        }else{
            System.out.println("no, i am dead :(");
        }
        
        // 下面代码完全一样，但是自救失败，因为同一个对象的 finalize() 方法最多只会被系统自动调用一次
        SAVE_HOOK = null;
        System.gc();
        // 因为 finalize 方法优先级很低，所以暂停0.5秒等待它
        Thread.sleep(500);
        if(SAVE_HOOK != null){
            SAVE_HOOK.isAlive();
        }else{
            System.out.println("no, i am dead :(");
        }
    }
    
}
