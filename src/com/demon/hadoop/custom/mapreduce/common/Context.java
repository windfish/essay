package com.demon.hadoop.custom.mapreduce.common;

import com.demon.hadoop.custom.mapreduce.inputformat.MapInputFormat;
import com.demon.hadoop.custom.mapreduce.inputformat.ReduceInputFormat;
import com.demon.hadoop.custom.mapreduce.mapper.Mapper;
import com.demon.hadoop.custom.mapreduce.outputformat.MapOutputFormat;
import com.demon.hadoop.custom.mapreduce.outputformat.ReduceOutputFormat;
import com.demon.hadoop.custom.mapreduce.partitioner.Partitioner;
import com.demon.hadoop.custom.mapreduce.reducer.Reducer;
import com.demon.hadoop.custom.mapreduce.sort.Sort;
import com.demon.hadoop.custom.mapreduce.utils.PropertiesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Context {

    private String jobId;

    private Class<MapInputFormat> mapInputFormatClass;
    private Class<Mapper> mapperClass;
    private Class<MapOutputFormat> mapOutputFormatClass;

    private Class<Partitioner> partitionerClass;
    private Class<Sort> sortClass;

    private Class<ReduceInputFormat> reduceInputFormatClass;
    private Class<Reducer> reducerClass;
    private Class<ReduceOutputFormat> reduceOutputFormatClass;

    private String inputDir;
    private String outputDir;

    private Properties properties;

    private List<String> mapTaskTempDirs;

    private int partitionNum;

    public int getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(int partitionNum) {
        this.partitionNum = partitionNum;
    }

    public Context() {
        this.properties = PropertiesUtils.loadConfig();
        this.mapTaskTempDirs = new ArrayList<>();
    }

    public List<String> getMapTaskTempDirs() {
        return mapTaskTempDirs;
    }

    public void setMapTaskTempDirs(List<String> mapTaskTempDirs) {
        this.mapTaskTempDirs = mapTaskTempDirs;
    }

    public String getConfig(String key){
        return properties.getProperty(key);
    }

    public void setConfig(String key, String value){
        properties.setProperty(key, value);
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Class<MapInputFormat> getMapInputFormatClass() {
        return mapInputFormatClass;
    }

    public void setMapInputFormatClass(Class<MapInputFormat> mapInputFormatClass) {
        this.mapInputFormatClass = mapInputFormatClass;
    }

    public Class<Mapper> getMapperClass() {
        return mapperClass;
    }

    public void setMapperClass(Class<Mapper> mapperClass) {
        this.mapperClass = mapperClass;
    }

    public Class<MapOutputFormat> getMapOutputFormatClass() {
        return mapOutputFormatClass;
    }

    public void setMapOutputFormatClass(Class<MapOutputFormat> mapOutputFormatClass) {
        this.mapOutputFormatClass = mapOutputFormatClass;
    }

    public Class<Partitioner> getPartitionerClass() {
        return partitionerClass;
    }

    public void setPartitionerClass(Class<Partitioner> partitionerClass) {
        this.partitionerClass = partitionerClass;
    }

    public Class<Sort> getSortClass() {
        return sortClass;
    }

    public Class<ReduceInputFormat> getReduceInputFormatClass() {
        return reduceInputFormatClass;
    }

    public void setReduceInputFormatClass(Class<ReduceInputFormat> reduceInputFormatClass) {
        this.reduceInputFormatClass = reduceInputFormatClass;
    }

    public Class<Reducer> getReducerClass() {
        return reducerClass;
    }

    public void setReducerClass(Class<Reducer> reducerClass) {
        this.reducerClass = reducerClass;
    }

    public Class<ReduceOutputFormat> getReduceOutputFormatClass() {
        return reduceOutputFormatClass;
    }

    public void setReduceOutputFormatClass(Class<ReduceOutputFormat> reduceOutputFormatClass) {
        this.reduceOutputFormatClass = reduceOutputFormatClass;
    }

    public void setSortClass(Class<Sort> sortClass) {
        this.sortClass = sortClass;
    }

    public String getInputDir() {
        return inputDir;
    }

    public void setInputDir(String inputDir) {
        this.inputDir = inputDir;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }
}
