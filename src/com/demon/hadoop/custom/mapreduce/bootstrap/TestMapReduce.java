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

public class TestMapReduce {

    public static void main(String[] args) {
        Context context = new Context();
        Job job = new Job(context);

        job.setMapInputFormatClass(MapInputFormat.class);
        job.setMapOutputFormatClass(MapOutputFormat.class);
        job.setMapperClass(Mapper.class);

        job.setPartitionNum(3);
        job.setPartitionerClass(Partitioner.class);
        job.setSortClass(Sort.class);

        job.setReduceInputFormatClass(ReduceInputFormat.class);
        job.setReduceOutputFormatClass(ReduceOutputFormat.class);
        job.setReducerClass(Reducer.class);

        job.setInputDir("/Users/xuliang/data/fs/mapreduce/input/");
        job.setOutputDir("/Users/xuliang/data/fs/mapreduce/output/");

        job.submit();

        System.exit(0);
    }
}
