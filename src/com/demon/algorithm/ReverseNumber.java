package com.demon.algorithm;

/**
 * 倒序数字：递归法
 * 
 * @author xuliang
 * @since 2018年10月16日 上午9:46:10
 *
 */
public class ReverseNumber {

    public static String reverse(int number){
        if(number <= 0){
            return "";
        }
        return (number % 10) + reverse(number / 10) ;
    }
    
    public static void main(String[] args) {
        System.out.println(reverse(123456789));
    }
    
}
