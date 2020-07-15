package com.demon.hadoop.custom.mapreduce.bootstrap;

import com.demon.hadoop.custom.mapreduce.common.Context;
import com.demon.hadoop.custom.mapreduce.inputformat.MapInputFormat;
import com.demon.hadoop.custom.mapreduce.inputformat.ReduceInputFormat;
import com.demon.hadoop.custom.mapreduce.job.Job;
import com.demon.hadoop.custom.mapreduce.mapper.Mapper;
import com.demon.hadoop.custom.mapreduce.outputformat.MapOutputFormat;
import com.demon.hadoop.custom.mapreduce.outputformat.ReduceOutputFormat;
import com.demon.hadoop.custom.mapreduce.partitioner.Partitioner;
import com.demon.hadoop.custom.mapreduce.reducer.Reducer;
import com.demon.hadoop.custom.mapreduce.sort.Sort;

/**
 * 测试MapReduce 计算文件中的英文词各出现了多少次
 */
public class TestMapReduce {

    public static void main(String[] args) {
        Context context = new Context();
        Job job = new Job(context);

        // 负责Mapper 阶段读取数据
        job.setMapInputFormatClass(MapInputFormat.class);
        // 负责Mapper 阶段输出数据
        job.setMapOutputFormatClass(MapOutputFormat.class);
        // 负责Mapper 阶段的逻辑处理
        job.setMapperClass(Mapper.class);

        // 设定分区数
        job.setPartitionNum(3);
        // 分区器
        job.setPartitionerClass(Partitioner.class);
        // 排序器
        job.setSortClass(Sort.class);

        // 负责Reducer 阶段读取数据
        job.setReduceInputFormatClass(ReduceInputFormat.class);
        // 负责Reducer 阶段输出数据
        job.setReduceOutputFormatClass(ReduceOutputFormat.class);
        // 负责Reducer 阶段的逻辑处理
        job.setReducerClass(Reducer.class);

        // 源文件目录
        job.setInputDir("/Users/xuliang/data/fs/mapreduce/input/");
        // 结果输出目录
        job.setOutputDir("/Users/xuliang/data/fs/mapreduce/output/");

        job.submit();

        System.exit(0);
    }
}
