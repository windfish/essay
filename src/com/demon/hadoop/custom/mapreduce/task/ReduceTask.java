package com.demon.hadoop.custom.mapreduce.task;

import com.demon.hadoop.custom.mapreduce.common.Context;
import com.demon.hadoop.custom.mapreduce.inputformat.ReduceInputFormat;
import com.demon.hadoop.custom.mapreduce.outputformat.ReduceOutputFormat;
import com.demon.hadoop.custom.mapreduce.reducer.Reducer;
import com.demon.hadoop.custom.mapreduce.utils.PathUtils;

import java.io.*;

public class ReduceTask {

    private int reduceTaskIdIndex;
    private String jobId;

    public ReduceTask(String jobId, int i) {
        this.reduceTaskIdIndex = i;
        this.jobId = jobId;
    }

    public void run(Context context, int reduceTaskIdIndex){
        ReduceInputFormat reduceInputFormat = null;
        ReduceOutputFormat reduceOutputFormat = null;
        Reducer reducer = null;
        try{
            reducer = context.getReducerClass().newInstance();
            reduceInputFormat = context.getReduceInputFormatClass().newInstance();
            reduceOutputFormat = context.getReduceOutputFormatClass().newInstance();

            // 获取结果文件输出的路径
            String outFile = initLastOutputFile(context, reduceTaskIdIndex);

            PrintWriter printWriter = null;
            try {
                printWriter = new PrintWriter(new FileOutputStream(new File(outFile)));
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            reduceOutputFormat.setPrintWriter(printWriter);

        }catch (Exception e){
            e.printStackTrace();
        }

        // 合并map 的结果文件
        System.out.println("开始合并");
        String reduceTempFile = reduceInputFormat.mergeMapTempFiles(context, reduceTaskIdIndex);

        // 初始化reduceTask 合并后写文件的输出流
        try {
            reduceOutputFormat.setReader(new BufferedReader(new FileReader(new File(reduceTempFile))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 执行业务逻辑
        reducer.run(reduceInputFormat, reduceOutputFormat);
    }

    private String initLastOutputFile(Context context, int reduceTaskIdIndex) {
        String outputDir = context.getOutputDir();
        return outputDir + "/" + PathUtils.generateReduceOutputTempFilename(reduceTaskIdIndex);
    }
}
