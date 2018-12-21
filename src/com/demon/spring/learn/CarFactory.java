package com.demon.spring.learn;

/**
 * 工厂模式注入
 * @author xuliang
 * @since 2018年12月21日 下午4:57:58
 *
 */
public class CarFactory {

    public static Car getCarStatic(){
        Car car = new Car();
        car.setName("static car");
        return car;
    }
    
    public Car getCar(){
        Car car = new Car();
        car.setName("non-static car");
        return car;
    }
    
}
