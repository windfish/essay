package com.demon.hadoop.custom.mapreduce.task;

import com.demon.hadoop.custom.mapreduce.common.Context;

import java.util.concurrent.Callable;

public class MapTaskRunner implements Callable<Boolean> {

    private InputSplit split;
    private Context context;
    private MapTask mapTask;

    public MapTaskRunner(InputSplit split, Context context) {
        this.split = split;
        this.context = context;
        this.mapTask = new MapTask();
    }

    @Override
    public Boolean call() throws Exception {
        System.out.println("MapTask begin...");
        mapTask.run(this.split, this.context);
        return true;
    }
}
