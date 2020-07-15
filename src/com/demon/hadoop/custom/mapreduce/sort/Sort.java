package com.demon.hadoop.custom.mapreduce.sort;

import com.demon.hadoop.custom.mapreduce.task.Record;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 排序器
 */
public class Sort {

    /**
     * 字典排序
     */
    public void dictSort(List<Record> records){
        Collections.sort(records, new Comparator<Record>() {
            @Override
            public int compare(Record o1, Record o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
    }
}
