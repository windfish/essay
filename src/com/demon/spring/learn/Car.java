package com.demon.spring.learn;

public class Car {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Car ["+getClass().getName() + "@" + Integer.toHexString(hashCode())+"] [name=" + name + "]";
    }
    
}
