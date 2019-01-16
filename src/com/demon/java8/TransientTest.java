package com.demon.java8;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * transient 修饰的变量不参与序列化
 * transient 只能修饰变量，而不能修饰方法和类。注意，本地变量(ThreadLocal)是不能被transient 修饰的。变量如果是用户自定义类变量，则该类需要实现Serializable接口。 
 * 一个静态变量不管是否被transient 修饰，均不能被序列化
 * 
 * @author xuliang
 * @since 2018年10月9日 上午11:05:52
 *
 */
public class TransientTest implements Serializable {

    private static final long serialVersionUID = 3664713617520864796L;

    private Date d = new Date();
    private String uid;
    private transient String pwd;
    
    public TransientTest(String uid, String pwd) {
        this.uid = uid;
        this.pwd = pwd;
    }
    
    @Override
    public String toString() {
        String note = "";
        if(pwd == null){
            note = "not set pwd";
        }else{
            note = pwd;
        }
        return "BaseClass [d=" + d + ", uid=" + uid + ", pwd=" + pwd + ", note=" + note + "]";
    }
    
    public static void main(String[] args) {
        TransientTest base = new TransientTest("test", "test123456");
        System.out.println("old-----------》" + base);
        
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("D:\\baseclass.out"));
            oos.writeObject(base);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // transient 修饰的pwd，在反序列化后（从磁盘的序列化文件反序列化对象），pwd并没有保存初始化的值
        // 将transient 修饰符去掉，pwd 有保存初始化的值
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("D:\\baseclass.out"));
            TransientTest newBase = (TransientTest) ois.readObject();
            System.out.println("new-----------》" + newBase);
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
