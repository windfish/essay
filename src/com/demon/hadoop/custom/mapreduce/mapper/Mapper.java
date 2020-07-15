package com.demon.hadoop.custom.mapreduce.mapper;

import com.demon.hadoop.custom.mapreduce.inputformat.MapInputFormat;
import com.demon.hadoop.custom.mapreduce.outputformat.MapOutputFormat;

public class Mapper {

    /**
     * 初始化方法
     */
    protected void setUp(MapInputFormat mapInputFormat, MapOutputFormat mapOutputFormat){

    }

    /**
     * 一条记录处理一次的map 方法
     */
    protected void map(Object currentKey, Object currentValue, MapOutputFormat mapOutputFormat){
        String value = (String) currentValue;
        String[] words = value.split(" ");

        for (String word: words){
            mapOutputFormat.write(word, 1);
        }
    }

    protected void cleanUp(MapInputFormat mapInputFormat, MapOutputFormat mapOutputFormat){
        mapOutputFormat.close();
    }

    public void run(MapInputFormat mapInputFormat, MapOutputFormat mapOutputFormat){
        setUp(mapInputFormat, mapOutputFormat);
        try{
            while (mapInputFormat.hasNextKey()){
                // 如果有key-value 值，就调用map 执行一次数据处理
                map(mapInputFormat.getCurrentKey(), mapInputFormat.getCurrentValue(), mapOutputFormat);
            }
        }finally {
            cleanUp(mapInputFormat, mapOutputFormat);
        }
    }
}
