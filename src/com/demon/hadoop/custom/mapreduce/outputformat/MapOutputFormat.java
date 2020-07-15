package com.demon.hadoop.custom.mapreduce.outputformat;

import com.demon.hadoop.custom.mapreduce.common.Context;
import com.demon.hadoop.custom.mapreduce.partitioner.Partitioner;
import com.demon.hadoop.custom.mapreduce.sort.Sort;
import com.demon.hadoop.custom.mapreduce.task.Record;
import com.demon.hadoop.custom.mapreduce.utils.PathUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MapOutputFormat {

    // Task 的输出
    private String outputPath;
    // 该Task 属于哪个Job
    private String jobId;
    // taskId
    private String taskId;

    private List<PrintWriter> writers;
    private List<List<Record>> recordsList;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    private Partitioner partitioner;
    private Sort sort;
    private int partitionNum;

    public MapOutputFormat(String tempOutputPath, Context context, String taskId) {
        // 初始化一些中间件
        init(context, taskId);

        String jobId = context.getJobId();

        // 创建临时目录，先判断是否存在
        String tempTaskDir = PathUtils.checkPath(tempOutputPath + "/" + jobId + "/" + this.getTaskId());
        File file = new File(tempTaskDir);
        if(!file.exists()){
            PathUtils.mkdir(tempTaskDir);
        }

        // 把临时目录保存起来，reduce 阶段拉取数据使用
        context.getMapTaskTempDirs().add(tempTaskDir);

        // 初始化输出流
        writers = new ArrayList<>(this.partitionNum);
        for(int i=0;i<this.partitionNum;i++){
            // map 完成时的临时文件名
            String perfectTempFilepath = tempTaskDir + "/" + PathUtils.generateMapOutputTempFilename(i) + ".txt";
            perfectTempFilepath = PathUtils.checkPath(perfectTempFilepath);

            PrintWriter pw = null;
            try {
                pw = new PrintWriter(new FileOutputStream(new File(perfectTempFilepath)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            writers.add(pw);
        }
    }

    private void init(Context context, String taskId){
        this.taskId = taskId;
        this.partitionNum = context.getPartitionNum();
        try {
            this.partitioner = context.getPartitionerClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        this.recordsList = new ArrayList<>(this.partitionNum);
        for(int i=0;i<this.partitionNum;i++){
            this.recordsList.add(new ArrayList<>());
        }
        try {
            this.sort = context.getSortClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void write(String key, int value){
        int partition = partitioner.getPartition(key, value, this.partitionNum);
        recordsList.get(partition).add(new Record(key, value));
    }

    public void print(Sort sort){
        // 先排序，再输出到文件
        for(int i=0;i<recordsList.size();i++){
            PrintWriter pw = writers.get(i);
            List<Record> records = recordsList.get(i);
            sort.dictSort(records);

            for(Record record: records){
                pw.println(record);
            }
        }
    }

    public void close(){
        print(this.sort);
        for(PrintWriter pw: writers){
            pw.close();
        }
    }
}
