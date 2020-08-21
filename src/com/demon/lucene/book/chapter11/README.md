# 免密登录

1. 生成密钥
```
ssh-keygen -t rsa -P ""
```

2. .ssh 目录下会生成一对密钥，将公钥文件id_rsa.pub 复制到本机的authorized_keys 文件中
```
cat id_rsa.pub >> authorized_keys
``` 

3. 测试登录
```
ssh localhost
```


# Hadoop 伪分布式模式

1. 修改 HADOOP_OPTS
#export HADOOP_OPTS="$HADOOP_OPTS -Djava.net.preferIPv4Stack=true"
export HADOOP_OPTS="$HADOOP_OPTS -Djava.net.preferIPv4Stack=true -Djava.security.krb5.realm= -Djava.security.krb5.kdc="

2.  core-site.xml 配置HDFS 地址和端口
```
<configuration>
  <property>
    <name>hadoop.tmp.dir</name>
    <value>/data/platform/tools/hadoop-2.7.7/hdfs/tmp</value>
    <description>a base tmp dir</description>
  </property>
  <property>
    <name>fs.default.name</name>
    <value>hdfs://192.168.11.248:9000</value>
  </property>
</configuration>
```

3. 修改 mapred-site.xml 配置JobTracker 的地址和端口
```
<configuration>
  <property>
    <name>mapred.job.tracker</name>
    <value>192.168.11.248:9010</value>
  </property>
</configuration>
```

4. 修改 hdfs-site.xml 配置HDFS 的副本数，因为是伪分布式，其实是单机，所以置为1
```
# 通常为3，但只有一台主机，设为1
<configuration>
  <property>
    <name>dfs.replication</name>
    <value>1</value>
  </property>
</configuration>
```

5. 格式化namenode
```
./bin/hadoop namenode -format
```

6. 启动hadoop
```
./sbin/start-all.sh
# start-all.sh 和先执行start-dfs.sh 再执行start-yarn.sh 是一样的
# 注意目录权限
```

7. 查看进程启动情况
```
[xuliang@s248 hadoop-2.7.7]$ jps
30631 SecondaryNameNode
30792 ResourceManager
30330 NameNode
4411 Elasticsearch
30459 DataNode
30894 NodeManager
31247 Jps
```

8. 访问HDFS 的50070 端口
![](https://github.com/windfish/img/blob/master/notes-img/搜索/4856fe2f46eeb671d389fa750689e11ce24.jpg?raw=true)


# HDFS 常用命令
```
 # 查看HDFS 根目录下的文件
 1059  ./bin/hadoop fs -ls /
 # 在HDFS 根目录新建一个文件夹
 1060  ./bin/hadoop fs -mkdir /work
 1061  ./bin/hadoop fs -ls /
 # 递归创建多级文件夹
 1062  ./bin/hadoop fs -mkdir -p /a/b/c
 1063  ./bin/hadoop fs -ls /
 1064  ./bin/hadoop fs -ls /a
 # 上传本地文件到HDFS
 1066  echo "test hdfs" >> a.txt
 1067  ll a.txt 
 1068  ./bin/hadoop fs -put a.txt /work
 # 检测文件是否存在，echo 返回0表示文件存在
 1069  ./bin/hadoop fs -test -e /work/a.txt
 1071  echo $?
 # 查看HDFS 的文件内容
 1074  ./bin/hadoop fs -cat /work/a.txt
 # 删除HDFS 上的文件
 ./bin/hadoop fs -rm /work/a.txt
 # 删除HDFS 上的文件夹
 ./bin/hadoop fs -rmr /work/a
 # 追加到文件末尾，b.txt 内容追加到a.txt 的末尾
 ./bin/hadoop fs -appendToFile b.txt /work/a.txt
```

