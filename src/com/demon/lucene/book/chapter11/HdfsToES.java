package com.demon.lucene.book.chapter11;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.elasticsearch.hadoop.mr.EsOutputFormat;

/**
 * 从HDFS 到 Elasticsearch
 * @author xuliang
 * @since 2019年10月10日 下午2:19:46
 *
 */
public class HdfsToES {

    public static class MyMapper extends Mapper<Object, Text, NullWritable, Text> {
        @Override
        protected void map(Object key, Text value, Mapper<Object, Text, NullWritable, Text>.Context context)
                throws IOException, InterruptedException {
            System.out.println(value.toString());
            byte[] line = value.toString().trim().getBytes();
            Text blog = new Text(line);
            context.write(NullWritable.get(), blog);
        }
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        System.out.println(System.getenv("HADOOP_HOME"));
        
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.map.tasks.speculative.execution", false); // 关闭mapper 阶段的执行推测
        conf.setBoolean("mapred.reduce.tasks.speculative.execution", false); // 关闭reduce 阶段的执行推测
        conf.set("es.nodes", "192.168.11.248:9200"); // 配置ES 的IP 和端口
        conf.set("es.resource", "blog"); // 设置索引到ES 的索引名
        conf.set("es.mapping.id", "id"); // 设置文档id，这个参数id 是文档中的id 字段
        conf.set("es.input.json", "yes"); // 指定输入的文件类型为json
        
        Job job = Job.getInstance(conf, "hadoop es write test");
        job.setMapperClass(HdfsToES.MyMapper.class);
        job.setInputFormatClass(TextInputFormat.class); // 设置输入流为文本类型
        job.setOutputFormatClass(EsOutputFormat.class); // 设置输出为EsOutputFormat 类型
        job.setMapOutputKeyClass(NullWritable.class); // 设置Map 的输出key 类型为NullWritable
        job.setMapOutputValueClass(Text.class); // 设置Map 的输出value 类型为BytesWritable
        
        FileInputFormat.setInputPaths(job, new Path("hdfs://192.168.11.248:9000/work/blog.json"));
        job.waitForCompletion(true);
    }
    
    /**
     * 遇到的问题：
     * 
     * 1. Could not locate Hadoop executable: xxx\bin\winutils.exe
     * 下载 WINUTILS.EXE，并放到Hadoop的bin目录，下载地址：https://github.com/steveloughran/winutils
     * 
     * 2. Caused by: java.io.FileNotFoundException: HADOOP_HOME and hadoop.home.dir are unset
     * 下载hadoop 安装包，并添加环境变量 HADOOP_HOME，值为 D:\work\hadoop-2.7.7
     * 
     * 3. java.io.IOException: Cannot initialize Cluster. Please check your configuration for mapreduce.framework.name and the correspond server addresses.
     * 缺少 hadoop-mapreduce-client-common
     * 
     * 4. java.lang.NoClassDefFoundError: org/apache/commons/httpclient/HttpConnectionManager
     * httpclient 版本过高，需要commons-httpclient 包
     * 
     * 5. java.lang.NoClassDefFoundError: org/w3c/dom/ElementTraversal
     * 引入 xml-apis
     * 
     * 6. java.lang.UnsatisfiedLinkError:org.apache.hadoop.io.nativeio.NativeIO$Windows.access0(Ljava/lang/String;I)Z
     * WINUTILS.EXE 同目录下的 hadoop.dll，也需要放入hadoop 的bin 目录下和C:\Windows\System32 下
     * 
     * 7. java.lang.Exception: org.elasticsearch.hadoop.serialization.EsHadoopSerializationException: org.codehaus.jackson.JsonParseException: Unexpected character ('b' (code 98)): expected a valid value (number, String, array, object, 'true', 'false' or 'null')
 at [Source: [B@5cdff749; line: 1, column: 3]
     * Mapper<Object, Text, NullWritable, Text>  
     * job.setMapOutputValueClass(Text.class);
     * Don't use byteswritable for mapvalue, just use text.
     */
    
}
