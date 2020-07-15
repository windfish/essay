package com.demon.hadoop.custom.mapreduce.mapper;

import com.demon.hadoop.custom.mapreduce.inputformat.MapInputFormat;
import com.demon.hadoop.custom.mapreduce.outputformat.MapOutputFormat;

/**
 * Mapper 阶段逻辑处理
 */
public class Mapper {

    /**
     * 初始化方法
     */
    protected void setUp(MapInputFormat mapInputFormat, MapOutputFormat mapOutputFormat){

    }

    /**
     * 将每一行的数据，拆分为单个词，将词个数写入文件中
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
