package com.demon.java8.proxy.staticproxy;

public class StaticProxyImpl implements Inter {

    private Inter inter;
    
    public StaticProxyImpl() {
        // 代理对象
        inter = new InterImpl();
    }
    
    @Override
    public void execute() {
        System.out.println("before proxy execute");
        
        inter.execute();
        
        System.out.println("after proxy execute");
    }

    public static void main(String[] args) {
        Inter i = new StaticProxyImpl();
        i.execute();
    }
    
}
