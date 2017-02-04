package com.demon.java8;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class TestMethodReferences {

	public static void main(String[] args) {
		// 构造方法引用，语法是：Class::new ，对于泛型来说语法是：Class<T >::new
		Car car = Car.create( Car::new );
		List< Car > cars = Arrays.asList( car );
		
		// 静态方法引用，语法是：Class::static_method
		cars.forEach( Car::collide );
		
		// 类实例的方法引用，语法是：Class::method
		cars.forEach( Car::repair );
		
		// 引用特殊类的方法，语法是：instance::method
		Car police = Car.create( Car::new );
		cars.forEach( police::follow );
	}
	
	public static class Car {
	    public static Car create( final Supplier< Car > supplier ) {
	        return supplier.get();
	    }              

	    public static void collide( final Car car ) {
	        System.out.println( "Collided " + car.toString() );
	    }

	    public void follow( final Car another ) {
	        System.out.println( this.toString() + " Following the " + another.toString() );
	    }

	    public void repair() {
	        System.out.println( "Repaired " + this.toString() );
	    }
	}
}
