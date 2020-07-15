package com.demon.hadoop.custom.mapreduce.task;

import com.demon.hadoop.custom.mapreduce.common.Context;
import com.demon.hadoop.custom.mapreduce.inputformat.MapInputFormat;
import com.demon.hadoop.custom.mapreduce.mapper.Mapper;
import com.demon.hadoop.custom.mapreduce.outputformat.MapOutputFormat;

public class MapTask {

    public void run(InputSplit split, Context context){
        // 生成一个TaskId
        String taskId = initTaskId(split, context);
        // 初始化Mapper 的输入
        MapInputFormat mapInputFormat = new MapInputFormat(split.getBlockPath());
        // 结果存放的临时目录
        String tempOutputPath = context.getConfig("mapreduce_temp");
        MapOutputFormat mapOutputFormat = new MapOutputFormat(tempOutputPath, context, taskId);

        Mapper mapper = null;
        try {
            mapper = context.getMapperClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        mapper.run(mapInputFormat, mapOutputFormat);
    }

    private String initTaskId(InputSplit split, Context context){
        return "map_task_" + split.getBlockId();
    }
}
