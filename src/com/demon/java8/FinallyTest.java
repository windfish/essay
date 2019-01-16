package com.demon.java8;

/**
 * 
 * @author xuliang
 * @since 2018年2月28日 上午9:30:10
 *
 */
public class FinallyTest {

    public static void main(String[] args) {
        System.out.println(test1());    // 10
        System.out.println(test2());    // 15
    }
    
    private static int test1(){
        int i = 10;
        try{
            return i;
        }finally {
            i = 15;
        }
    }
    
    private static int test2(){
        int i = 10;
        try{
            
        }finally {
            i = 15;
        }
        return i;
    }
    
}
