package com.demon.hadoop.custom.mapreduce.outputformat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Reducer 阶段输出格式化工具类
 */
public class ReduceOutputFormat {

    // 当前处理的词
    private String key;
    // 当前处理的词的个数列表
    private List<Integer> values;
    // 当前读取的最后一行
    private String lastLine = null;
    // 当前读取的最后一个词，其应该不同意当前处理的词。因为排过序，所有读取到不同的词时，表示前一个词的个数都已读完，可以返回处理了
    private String lastKey = null;
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

    /**
     * 读取map 处理后，并且已经排序后的结果
     * 读取到相同的词时，一直往下读
     * 直到读到不同的词，记录当前读到的位置（lastLine 和lastKey）
     * 然后返回前一个词及其个数列表
     */
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
