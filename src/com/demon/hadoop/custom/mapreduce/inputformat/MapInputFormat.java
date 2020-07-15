package com.demon.hadoop.custom.mapreduce.inputformat;

import java.io.*;

/**
 * Map 阶段数据输入格式化工具
 */
public class MapInputFormat {

    // 记录文件读取的字节数
    private long key;
    // 记录读取的一行数据
    private String value;

    private String blockPath;
    // 逐行读取器
    private BufferedReader reader = null;

    public MapInputFormat() {
    }

    public MapInputFormat(String inputPath) {
        this.blockPath = inputPath;
        this.reader = initReader(blockPath);
    }

    /**
     * 初始化行读取器
     * @param filePath 待读取的文件路径
     * @return
     */
    private BufferedReader initReader(String filePath){
        // 初始化时，读取的字节设为0
        this.key = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在！");
            e.printStackTrace();
        }
        return reader;
    }

    /**
     * 校验文件是否还有可读取的内容，若有，则读取一行数据，并返回true
     */
    public boolean hasNextKey(){
        try {
            this.value = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(this.value == null){
            close();
            return false;
        }else{
            this.key += this.value.length();
            return true;
        }
    }

    public long getCurrentKey(){
        return this.key;
    }

    public String getCurrentValue(){
        return this.value;
    }

    private void close(){
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
