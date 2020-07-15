package com.demon.hadoop.custom.mapreduce.inputformat;

import java.io.*;

public class MapInputFormat {

    private long key;
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

    private BufferedReader initReader(String filePath){
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
