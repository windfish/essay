package com.demon.hadoop.custom.fs_local;

import java.util.List;

public class LocalFileSystemTest {

    private static LocalFileSystem fs = new LocalFileSystem();

    public static void main(String[] args) {
        upload();
        download();
        mkdir();
        list();
    }

    private static void upload(){
        String filePath = "/Users/xuliang/data/test.txt";

        fs.put(filePath, "/test/");
    }

    private static void download(){
        fs.get("/test/test.txt", "/Users/xuliang/");
    }

    private static void mkdir(){
        fs.mkdir("/aa/bb/cc/dd");
    }

    private static void list(){
        List<String> l = fs.list("/test");
        for(String s: l){
            System.out.println(s);
        }
    }
}
