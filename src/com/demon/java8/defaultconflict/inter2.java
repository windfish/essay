package com.demon.java8.defaultconflict;

public interface inter2 extends inter1 {

    default void hello(){
        System.out.println("inter2 hello");
    }
    
}
