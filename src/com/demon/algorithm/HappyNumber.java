package com.demon.algorithm;

import java.util.HashSet;
import java.util.Set;

/**
 * 快乐数字：在给定的进位制下，该数字所有数位(digits)的平方和，得到的新数再次求所有数位的平方和，如此重复进行，最终结果必为1。
 * 
 * @author xuliang
 * @since 2018年10月16日 上午9:52:01
 *
 */
public class HappyNumber {

    public static boolean validate(int number){
        Set<Integer> set = new HashSet<>();
        while(number != 1){
            int sum = 0;
            while(number > 0){
                // 计算数字每一位的平方和
                sum += (number % 10) * (number % 10);
                number = number / 10;
            }
            if(set.contains(sum)){
                return false;
            }else{
                set.add(sum);
            }
            number = sum;
        }
        return true;
    }
    
    public static void main(String[] args) {
        System.out.println(validate(10));
        System.out.println(validate(11));
    }
    
}
