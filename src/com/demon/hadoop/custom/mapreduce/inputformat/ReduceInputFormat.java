package com.demon.hadoop.custom.mapreduce.inputformat;

import com.demon.hadoop.custom.mapreduce.common.Context;
import com.demon.hadoop.custom.mapreduce.sort.Sort;
import com.demon.hadoop.custom.mapreduce.task.Record;
import com.demon.hadoop.custom.mapreduce.utils.PathUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReduceInputFormat {

    // 用来做合并输出的流
    private PrintWriter printWriter;

    private List<Record> records;

    public String mergeMapTempFiles(Context context, int reduceTaskIdIndex){
        this.records = new ArrayList<>();

        int partitionNum = context.getPartitionNum();
        String jobId = context.getJobId();

        String reduceTaskTempDir = context.getConfig("mapreduce_temp") + "/" + jobId + "/reduce_task_part" + reduceTaskIdIndex;
        reduceTaskTempDir = PathUtils.checkPath(reduceTaskTempDir);
        File reduceTempDir = new File(reduceTaskTempDir);
        if(!reduceTempDir.exists()){
            PathUtils.mkdir(reduceTaskTempDir);
        }
        String reduceTaskTempFile = reduceTaskTempDir + "/result_temp.txt";

        try {
            // 用来做shuffle 之后reduce 拉取数据做合并的数据流
            this.printWriter = new PrintWriter(new FileOutputStream(new File(reduceTaskTempFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 处理输入和输出路径
        List<String> mapTaskTempDirs = context.getMapTaskTempDirs();
        // 把不同的mapTask 的相同分区的结果数据拉取到一起，然后合并成一个有序的文件
        for(int i=0;i<mapTaskTempDirs.size();i++){
            File file = new File(mapTaskTempDirs.get(i));
            File[] tempFiles = file.listFiles();

            for(File f: tempFiles){
                String part_index = f.getName().replace("part_map_", "").replace(".txt", "");
                if(Integer.parseInt(part_index) == reduceTaskIdIndex){
                    readFileData(f);
                }
            }
        }

        // 数据写到磁盘
        print(context);
        this.printWriter.close();
        return reduceTaskTempFile;
    }

    private void readFileData(File f) {
        BufferedReader reader = null;
        // 读取map 结果
        try {
            reader = new BufferedReader(new FileReader(f));
            String line = null;
            while((line = reader.readLine()) != null){
                String[] key_value = line.split("\t");
                this.records.add(new Record(key_value[0], Integer.parseInt(key_value[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void print(Context context){
        // 先排序
        Sort sort = null;
        try {
            sort = context.getSortClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        sort.dictSort(this.records);

        // 再写入磁盘
        for(Record r: this.records){
            this.printWriter.println(r.getKey() + "\t" + r.getValue());
        }
    }
}
