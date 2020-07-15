package com.demon.hadoop.custom.mapreduce.job;

import com.demon.hadoop.custom.mapreduce.common.Context;
import com.demon.hadoop.custom.mapreduce.inputformat.MapInputFormat;
import com.demon.hadoop.custom.mapreduce.inputformat.ReduceInputFormat;
import com.demon.hadoop.custom.mapreduce.mapper.Mapper;
import com.demon.hadoop.custom.mapreduce.outputformat.MapOutputFormat;
import com.demon.hadoop.custom.mapreduce.outputformat.ReduceOutputFormat;
import com.demon.hadoop.custom.mapreduce.partitioner.Partitioner;
import com.demon.hadoop.custom.mapreduce.reducer.Reducer;
import com.demon.hadoop.custom.mapreduce.sort.Sort;
import com.demon.hadoop.custom.mapreduce.split.Spliter;
import com.demon.hadoop.custom.mapreduce.task.InputSplit;
import com.demon.hadoop.custom.mapreduce.task.MapTaskRunner;
import com.demon.hadoop.custom.mapreduce.task.ReduceTaskRunner;
import com.demon.hadoop.custom.mapreduce.utils.PathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Job {

    private Context context;
    private ExecutorService executor = null;

    public Job(Context context) {
        this.context = context;
        int cpuCore = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(cpuCore);
    }

    public void submit(){
        run(context);
    }

    private void run(Context context) {
        // 初始化Job，生成JobId
        initJob(context);
        // 逻辑切片
        Spliter spliter = new Spliter();
        // 每个InputSplit 逻辑切片都包装了一个数据块，最终都会启动一个任务去处理
        List<InputSplit> splits = spliter.getSplits(context, context.getInputDir());

        // 启动map 阶段计算
        runMapper(context, splits);
        // 启动shuffle 阶段
        runShuffle(context);
        // 启动reduce 阶段计算
        runReducer(context);

        System.out.println("Job complete ...");
    }

    private void initJob(Context context){
        String outputDir = context.getOutputDir();
        File file = new File(outputDir);
        if(file.listFiles().length != 0){
            PathUtils.delete(outputDir);
            PathUtils.mkdir(outputDir);
        }
        String jobId = "job_" + new Date().getTime();
        context.setJobId(jobId);
    }

    private void runMapper(Context context, List<InputSplit> splits){
        List<Future<Boolean>> futures = new ArrayList<>();

        for(InputSplit split: splits){
            MapTaskRunner taskRunner = new MapTaskRunner(split, context);
            Future<Boolean> future = executor.submit(taskRunner);
            futures.add(future);
        }
        System.out.println("Mapper 阶段Task 任务提交完毕");

        checkTaskComplete(futures);
        System.out.println("Mapper 阶段执行完毕");
    }

    private void runReducer(Context context){
        List<Future<Boolean>> futures = new ArrayList<>();

        String jobId = context.getJobId();
        int partitions = context.getPartitionNum();

        // 有几个分区，就创建几个 ReduceTask
        for(int i=0;i<partitions;i++){
            ReduceTaskRunner taskRunner = new ReduceTaskRunner(jobId, i, context);
            Future<Boolean> future = executor.submit(taskRunner);
            futures.add(future);
        }
        System.out.println("Reduce 阶段Task 任务提交完毕");

        checkTaskComplete(futures);
        System.out.println("Reduce 阶段执行完毕");
    }

    private void runShuffle(Context context){
       // map 阶段的结果排序、汇总操作，目前放在MapOutputFormat 中了
    }

    private void checkTaskComplete(List<Future<Boolean>> futures){
        for(Future<Boolean> f: futures){
            try {
                Boolean b = f.get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void setMapInputFormatClass(Class<MapInputFormat> mapInputFormatClass){
        context.setMapInputFormatClass(mapInputFormatClass);
    }

    public void setMapOutputFormatClass(Class<MapOutputFormat> mapOutputFormatClass){
        context.setMapOutputFormatClass(mapOutputFormatClass);
    }

    public void setMapperClass(Class<Mapper> mapperClass){
        context.setMapperClass(mapperClass);
    }

    public void setPartitionerClass(Class<Partitioner> partitionerClass){
        context.setPartitionerClass(partitionerClass);
    }

    public void setSortClass(Class<Sort> sortClass){
        context.setSortClass(sortClass);
    }

    public void setReduceInputFormatClass(Class<ReduceInputFormat> reduceInputFormatClass){
        context.setReduceInputFormatClass(reduceInputFormatClass);
    }

    public void setReduceOutputFormatClass(Class<ReduceOutputFormat> reduceOutputFormatClass){
        context.setReduceOutputFormatClass(reduceOutputFormatClass);
    }

    public void setReducerClass(Class<Reducer> reducerClass){
        context.setReducerClass(reducerClass);
    }

    public void setInputDir(String inputDir){
        context.setInputDir(inputDir);
    }

    public void setOutputDir(String outputDir){
        context.setOutputDir(outputDir);
    }

    public void setPartitionNum(int partitionNum){
        context.setPartitionNum(partitionNum);
    }

    public int getPartitionNum(){
        return context.getPartitionNum();
    }
}
