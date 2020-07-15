package com.demon.hadoop.custom.mapreduce.reducer;

import com.demon.hadoop.custom.mapreduce.inputformat.ReduceInputFormat;
import com.demon.hadoop.custom.mapreduce.outputformat.ReduceOutputFormat;

import java.util.List;

public class Reducer {

    protected void setUp(ReduceOutputFormat reduceOutputFormat){

    }

    /**
     * 将词的个数累加，然后输出
     */
    protected void reduce(String key, List<Integer> values, ReduceOutputFormat reduceOutputFormat){
        int total = 0;
        for(int v: values){
            total += v;
        }
        reduceOutputFormat.write(key, total);
    }

    protected void cleanUp(ReduceOutputFormat reduceOutputFormat){
        reduceOutputFormat.close();
    }

    public void run(ReduceInputFormat reduceInputFormat, ReduceOutputFormat reduceOutputFormat){
        setUp(reduceOutputFormat);
        while (reduceOutputFormat.nextKeyValue()){
            String key = reduceOutputFormat.getCurrentKey();
            List<Integer> values = reduceOutputFormat.getCurrentValues();

            reduce(key, values, reduceOutputFormat);
        }
        cleanUp(reduceOutputFormat);
    }
}
