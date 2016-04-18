package com.demon.hadoop;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.junit.Before;
import org.junit.Test;

/**
 * 使用java操作HDFS
 */
public class HdfsUtil {
	
	public static void main(String[] args) throws IOException {
		/*从hdfs下载文件*/
		/*
		 * 遇到的问题：
		 * 1、Wrong FS: hdfs://192.168.222.200:9000/
		 * 默认fs拿到本地文件系统的客户端，不认识hdfs文件系统，所以需要设置配置文件
		 * 可以将core-site.xml和hdfs-site.xml拷贝到程序所在目录，创建配置文件对象时会自动读取
		 */
		
		//读取配置文件
		Configuration conf = new Configuration();
		//获取hdfs客户端
		FileSystem fs = FileSystem.get(conf);
		//连接hdfs，并打开输入流
		Path path = new Path("hdfs://192.168.222.200:9000/hadoop-2.6.4-src.tar.gz");
		FSDataInputStream in = fs.open(path);
		FileOutputStream out = new FileOutputStream("D:\\hadoop-src.tar.gz");
		IOUtils.copy(in, out);
	}
	
	private FileSystem fs = null;
	
	@Before
	public void init() throws IOException{
		//读取classpath下的xxx-site.xml 配置文件，并解析其内容，封装到conf中
		Configuration conf = new Configuration();
		//也可以在代码中，手动设置conf的参数属性，会覆盖读取的配置文件信息
		//conf.set("fs.defaultFS", "hdfs://vm-ubuntu:9000/");
		//根据配置信息，获取文件系统的客户端操作实例对象
		fs = FileSystem.get(conf);
	}
	
	/**
	 * 上传文件
	 */
	@Test
	public void upload() throws IOException{
		/*比较底层的写法*/
		/*Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Path dst = new Path("hdfs://vm-ubuntu:9000/testDir/bak.txt");
		FSDataOutputStream os = fs.create(dst);
		FileInputStream is = new FileInputStream("d:/bak.txt");
		IOUtils.copy(is, os);*/
		
		/*hadoop封装好的API*/
		Path src = new Path("d:/bak.txt");
		Path dst = new Path("hdfs://vm-ubuntu:9000/testDir/bak2.txt");
		fs.copyFromLocalFile(src, dst);
	}
	
	/**
	 * 下载文件
	 */
	@Test
	public void download() throws IOException{
		fs.copyToLocalFile(new Path("hdfs://vm-ubuntu:9000/testDir/bak2.txt"), new Path("d:/bak222.txt"));
	}
	
	/**
	 * 查看目录下文件信息
	 */
	@Test
	public void listFiles() throws FileNotFoundException, IllegalArgumentException, IOException{
		//列举文件夹下文件的信息，支持递归查找
		RemoteIterator<LocatedFileStatus> iterator = fs.listFiles(new Path("/"), true);
		while(iterator.hasNext()){
			LocatedFileStatus file = iterator.next();
			System.out.println("path: " + file.getPath() + ", size: "+file.getBlockSize());
		}
	}
	
	/**
	 * 创建目录
	 */
	@Test
	public void mkdir() throws IllegalArgumentException, IOException{
		//创建目录，支持级联创建
		fs.mkdirs(new Path("/aa/bb/cc"));
	}
	
	/**
	 * 删除文件或目录
	 */
	@Test
	public void rm() throws IllegalArgumentException, IOException{
		//删除目录和文件，true支持级联删除
		fs.delete(new Path("/aa"), true);
	}

}
