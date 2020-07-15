package com.demon.hadoop.custom.mapreduce.utils;

import java.io.File;
import java.text.DecimalFormat;

public class PathUtils {

    public static String checkPath(String path){
        return path.replace("\\", "/").replace("//", "/");
    }

    public static String generateMapOutputTempFilename(int partitionNum){
        return "part_map_" + validatePartitionNum(partitionNum);
    }

    public static String generateReduceOutputTempFilename(int partitionNum){
        return "part_reduce_" + validatePartitionNum(partitionNum);
    }

    private static String validatePartitionNum(int partitionNum){
        DecimalFormat decimalFormat = new DecimalFormat("00000");
        return decimalFormat.format(partitionNum);
    }

    public static void mkdir(String path){
        File current = new File(path);
        File parent = current.getParentFile();
        if(!parent.exists()){
            mkdir(current.getParent());
        }
        current.mkdir();
    }

    public static void delete(String path){
        File file = new File(path);
        if (file.isFile()){
            file.delete();
        }else{
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    delete(f.getAbsolutePath());
                }
            }
            file.delete();
        }
    }

}
