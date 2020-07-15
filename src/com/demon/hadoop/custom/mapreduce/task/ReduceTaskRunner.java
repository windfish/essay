package com.demon.hadoop.custom.mapreduce.task;

import com.demon.hadoop.custom.mapreduce.common.Context;

import java.util.concurrent.Callable;

public class ReduceTaskRunner implements Callable<Boolean> {

    private int reduceTaskIdIndex;
    private String jobId;
    private ReduceTask reduceTask;
    private Context context;

    public ReduceTaskRunner(String jobId, int reduceTaskIdIndex, Context context) {
        this.reduceTaskIdIndex = reduceTaskIdIndex;
        this.jobId = jobId;
        this.context = context;
        this.reduceTask = new ReduceTask(jobId, reduceTaskIdIndex);
    }

    @Override
    public Boolean call() throws Exception {
        System.out.println("ReduceTask begin...");
        reduceTask.run(context, reduceTaskIdIndex);
        return true;
    }
}
