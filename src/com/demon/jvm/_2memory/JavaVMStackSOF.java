package com.demon.jvm._2memory;

/**
 * Java StackOverFlowError
 * VM Args: -Xss128k
 * @author xuliang
 * @since 2018年8月20日 下午2:39:45
 *
 */
public class JavaVMStackSOF {

    private int stackLength = 1;
    
    public void stackLeak() {
        stackLength++;
        stackLeak();
    }
    
    public static void main(String[] args) {
        JavaVMStackSOF oom = new JavaVMStackSOF();
        try{
            oom.stackLeak();
        }catch (Throwable e) {
            System.out.println("stack length:"+oom.stackLength);
            throw e;
        }
    }
    
}
