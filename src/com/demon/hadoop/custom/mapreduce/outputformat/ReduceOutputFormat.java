package com.demon.hadoop.custom.mapreduce.outputformat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ReduceOutputFormat {

    private String key;
    private String lastLine = null;
    private String lastKey = null;
    private List<Integer> values;
    private PrintWriter printWriter;
    private BufferedReader reader;

    private boolean isDone = false;

    public BufferedReader getReader() {
        return reader;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
        values = new ArrayList<>();
        // 先读一行
        try {
            lastLine = reader.readLine();
            String[] key_value = lastLine.split("\t");
            key = key_value[0];
            values.add(Integer.parseInt(key_value[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean nextKeyValue(){
        // 处理每一组的第一条数据
        if(lastLine == null){
            // 读完了
            return false;
        }else{
            String[] key_value = lastLine.split("\t");
            key = key_value[0];
            values.clear();
            values.add(Integer.parseInt(key_value[1]));
        }
        // 继续读数据
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] key_value = line.split("\t");
                String currentKey = key_value[0];

                if(currentKey.equals(key)){
                    values.add(Integer.parseInt(key_value[1]));
                }else{
                    // 如果不相同，表示之前的key 的数据已读完，后面的key 都是不同的key
                    lastLine = line;
                    lastKey = key;

                    return true;
                }
            }
            lastLine = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    public void setPrintWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    public String getCurrentKey(){
        return key;
    }

    public List<Integer> getCurrentValues(){
        return values;
    }

    public void close(){
        printWriter.close();
    }
    // 最终输出，输出汇总的结果
    public void write(String key, int value){
        printWriter.println(key + "\t" + value);
    }
}
