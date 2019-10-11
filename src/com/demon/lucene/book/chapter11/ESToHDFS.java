package com.demon.lucene.book.chapter11;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.elasticsearch.hadoop.mr.EsInputFormat;
import org.junit.Before;
import org.junit.Test;

/**
 * 读取索引到 HDFS
 * @author xuliang
 * @since 2019年10月11日 下午3:12:37
 *
 */
public class ESToHDFS {

    @Before
    public void before(){
        System.setProperty("HADOOP_USER_NAME", "xuliang");
        System.out.println(System.getProperty("HADOOP_USER_NAME"));
    }
    
    public static class MyMapperIndexToHDFS extends Mapper<Writable, Writable, NullWritable, Text> {
        @Override
        protected void map(Writable key, Writable value, Mapper<Writable, Writable, NullWritable, Text>.Context context)
                throws IOException, InterruptedException {
            Text text = new Text();
            text.set(value.toString());
            context.write(NullWritable.get(), text);
        }
    }
    
    @Test
    public void ESIndexToHDFS() throws IOException, ClassNotFoundException, InterruptedException{
        Configuration conf = new Configuration();
        conf.set("es.nodes", "192.168.11.248:9200"); // 配置ES 的IP 和端口
        conf.set("es.resource", "blog"); // 设置索引到ES 的索引名
        conf.set("es.output.json", "true"); // 指定输出的文件类型为json
        
        Job job = Job.getInstance(conf, "hadoop es write test");
        job.setMapperClass(MyMapperIndexToHDFS.class);
        job.setNumReduceTasks(1);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setInputFormatClass(EsInputFormat.class);
        FileOutputFormat.setOutputPath(job, new Path("hdfs://192.168.11.248:9000/work/blog_csdn"));
        job.waitForCompletion(true);
    }
    
    public static class MyMapperQueryToHDFS extends Mapper<Writable, Writable, Text, Text> {
        @Override
        protected void map(Writable key, Writable value, Mapper<Writable, Writable, Text, Text>.Context context)
                throws IOException, InterruptedException {
            context.write(new Text(key.toString()), new Text(value.toString()));
        }
    }
    
    @Test
    public void ESQueryToHDFS() throws IOException, ClassNotFoundException, InterruptedException{
        Configuration conf = new Configuration();
        conf.set("es.nodes", "192.168.11.248:9200"); // 配置ES 的IP 和端口
        conf.set("es.resource", "blog"); // 设置索引到ES 的索引名
        conf.set("es.output.json", "true"); // 指定输出的文件类型为json
        conf.set("es.query", "?q=title:git");
        
        Job job = Job.getInstance(conf, "query es to HDFS");
        job.setMapperClass(MyMapperQueryToHDFS.class);
        job.setNumReduceTasks(1);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setInputFormatClass(EsInputFormat.class);
        FileOutputFormat.setOutputPath(job, new Path("hdfs://192.168.11.248:9000/work/es_query_to_HDFS"));
        job.waitForCompletion(true);
    }
    
    /**
     * 遇到的问题：
     * 
     * 1. org.apache.hadoop.security.AccessControlException: Permission denied: user=Administrator, access=WRITE, inode="/work":xuliang:supergroup:drwxr-xr-x
     * hadoop 访问hdfs 时会进行权限验证，使用免验证的用户，需要设置 HADOOP_USER_NAME 环境变量
     * System.setProperty("HADOOP_USER_NAME", "xuliang");
     * 
     * hadoop 获取用户名的过程是这样的
     * 取用户名的过程是这样的：
     * 1> 读取HADOOP_USER_NAME系统环境变量，如果不为空，那么拿它作username，如果为空
     * 2> 读取HADOOP_USER_NAME这个java环境变量，如果为空
     * 3> 从com.sun.security.auth.NTUserPrincipal或者com.sun.security.auth.UnixPrincipal的实例获取username。
     * 4> 如果以上尝试都失败，那么抛出异常LoginException("Can’t find user name")
     */
    
}
