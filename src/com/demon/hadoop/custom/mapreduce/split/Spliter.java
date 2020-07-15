package com.demon.hadoop.custom.mapreduce.split;

import com.alibaba.fastjson.JSON;
import com.demon.hadoop.custom.mapreduce.common.Context;
import com.demon.hadoop.custom.mapreduce.task.InputSplit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 切分器
 */
public class Spliter {

    public List<InputSplit> getSplits(Context context, String path){
        List<InputSplit> splits = null;
        if(splits == null){
            splits = new ArrayList<>();
            File f = new File(path);

            if(f.isFile()){
                splits.add(new InputSplit(f.getName(), f.getPath()));
            }else{
                // 暂不支持级联
                File[] files = f.listFiles();
                for(File file: files){
                    splits.add(new InputSplit(file.getName(), file.getPath()));
                }
            }
        }
        return splits;
    }

    public List<InputSplit> getSplits(String path, List<InputSplit> list){
        if(list == null){
            list = new ArrayList<>();
        }
        File f = new File(path);
        if(f.isFile()){
            list.add(new InputSplit(f.getName(), f.getPath()));
        }else{
            File[] files = f.listFiles();
            for(File file: files){
                if(file.isFile()){
                    list.add(new InputSplit(file.getName(), file.getPath()));
                }else{
                    getSplits(file.getPath(), list);
                }
            }
        }
        return list;
    }

    public static void main(String[] args) {
        List<InputSplit> splits = new Spliter().getSplits("/Users/xuliang/data/fs/dfs", null);
        System.out.println(JSON.toJSONString(splits, true));
    }
}
