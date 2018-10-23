package com.demon.java8.defaultconflict;

/**
 * 类中的方法，优先于接口中的默认方法
 * 子接口中的默认方法优先于父接口中同名的默认方法
 * 两个接口中存在相同的默认方法，则实现类需要解决默认方法冲突
 * 
 * @author xuliang
 * @since 2018年10月23日 下午3:07:52
 *
 */
public class Main implements inter2, inter1 {

//    public void hello(){
//        System.out.println("main hello");
//    }
    
    public static void main(String[] args) {
        new Main().hello();
    }
    
}
