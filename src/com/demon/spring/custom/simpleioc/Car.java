package com.demon.spring.custom.simpleioc;

/**
 * 
 * @author xuliang
 * @since 2018年12月14日 下午5:12:13
 *
 */
public class Car {

    private String name;
    private String length;
    private String width;
    private String height;
    private Wheel wheel;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLength() {
        return length;
    }
    public void setLength(String length) {
        this.length = length;
    }
    public String getWidth() {
        return width;
    }
    public void setWidth(String width) {
        this.width = width;
    }
    public String getHeight() {
        return height;
    }
    public void setHeight(String height) {
        this.height = height;
    }
    public Wheel getWheel() {
        return wheel;
    }
    public void setWheel(Wheel wheel) {
        this.wheel = wheel;
    }
    
}
