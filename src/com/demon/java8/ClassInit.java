package com.demon.java8;

/**
 * <pre>
 * 类初始化顺序
 * 
 * 同一个类中，static 修饰的成员变量和静态代码块，按声明的顺序进行初始化
 * 正常的顺序：static 变量和代码块 --> 其他成员变量 --> 匿名代码块 --> 构造方法
 * 例子中，由于static StaticTest st = new StaticTest(); 的存在，导致构造方法提前，而匿名代码块要在构造方法之前执行，所以也提前执行，而变量b 由于是static修饰，并且在后面， 因此初始化值为0
 * 
 * 父子类，子类实例化的顺序：初始化父类static --> 子类static --> 父类其他的成员变量 --> 父类构造方法 --> 子类其他成员变量 --> 子类构造方法
 * 
 * </pre>
 */
public class ClassInit
{
    public static void main(String[] args)
    {
        staticFunction();
    }
    
    static ClassInit st = new ClassInit();

    static
    {
        System.out.println("1");
    }

    {
        System.out.println("2");
    }

    ClassInit(){
        System.out.println("3");
        System.out.println("a="+a+",b="+b);
    }
    
    public static void staticFunction(){
        System.out.println("4");
    }

    int a=110;
    static int b =112;
}

class Child extends ClassInit {
    
    public static void main(String[] args) {
        System.out.println("child 1");
        new Child();
    }
    
    {
        System.out.println("child 2");
    }
    
}
