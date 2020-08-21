# Elasticsearch 结构

![](https://github.com/windfish/img/blob/master/notes-img/搜索/628e84aff978e30343eaf8108d8547c9689.jpg?raw=true)

* Gateway 是Elasticsearch 用来存储索引的文件系统，支持多种文件类型。
    Local FileSystem 是存储在本地的文件系统，Shared FileSystem 是共享存储，也可以使用Hadoop 的HDFS 分布式存储，也可以存储在Amazon S3 云服务上
* Gateway 的上层是一个分布式的Lucene 框架，Elasticsearch 的底层API 是由Lucene 提供的，每一个节点上都有一个Lucene 引擎的支持
* Lucene 上层是Elasticsearch 模块，包括索引模块、搜索模块、映射解析模块等
* Elasticsearch 上层是Discovery、Scripting和第三方插件。
    Discovery 是节点发现模块，不同机器上的Elasticsearch 节点要组成集群需要进行消息通信，集群内部需要选举master 节点，这些工作都由Discovery 模块完成。
    Scripting 用来支持JavaScript、Python 等多种语言，可以在查询语句中嵌入，使用Script 语句性能稍低
* 再上面是Elasticsearch 的传输模块和JMX，传输模块支持Thrift、Memcached、HTTP，默认使用HTTP 传输。JMX 是Java 的管理框架，用来管理Elasticsearch 应用
* 最上层是提供给用户的接口，可以通过RESTful API 和Elasticsearch 集群进行交互


# 应用场景

* 站内搜索
* NoSQL 数据库
* 日志分析，由实时日志分析平台ELK（由Elasticsearch、Logstash、Kiabana 上开源工具组成），能够对日志进行集中的收集、存储、搜索、分析、监控和可视化


# Elasticsearch 核心概念

* 集群
一个或多个安装Elasticsearch 的服务节点组织在一起就是集群，共同持有整个数据，并一起提供索引和搜索服务。一个集群由一个唯一的名字标识，称为cluster name，集群名称默认“elasticsearch”。
具有相同集群名称的节点才会组成一个集群，集群名称可以在配置文件中指定
* 节点
一个节点是集群中的一个服务器，它存储数据，参与集群的索引和搜索功能。
一个节点通过配置集群名称的方式来加入一个指定的集群，但有时这种机制并不可靠，不如在每个节点上配置节点名称在启动时进行被动发现来的安全稳定。
* 索引
索引的数据结构仍是倒排索引，一个索引由一个名字来标识（必须全部是小写字母的），当要对这个索引中的文档进行索引、搜索、更新和删除时，都要用到这个名字。
* 类型
在一个索引中，可以定义一种或多种类型。一个类型是索引的一个逻辑上的分类或分区，通常会为具有一组共同字段的文档定义一个类型。
* 文档
一个文档是一个可被索引的基础信息单元，文档都是JSON 格式
* 分片
一个索引可以存储超出单个节点硬件限制的大量数据。比如，一个索引占据1TB 的磁盘空间，但任一节点都没这么大的磁盘空间，为了解决这个问题，Elasticsearch 提供将索引划分为多份的能力，每一份叫做分片。
分片的特点：允许水平分隔/扩展内容容量；允许在分片（位于多个节点上）上进行分布式、并行的操作，进而提高性能和吞吐量，至于分片怎么分布，如何聚合回搜索，完全由Elasticsearch 管理。
* 副本
Elasticsearch 允许创建分片的一份或多份拷贝，这些拷贝称为复制分片，或直接叫副本。
副本的特点：在分片/节点失败时，保证高可用性，副本不与主分片置于同一节点；扩展搜索量/吞吐量，因为可以在所有副本上并行进行。


_注意_：Elasticsearch 中，提到某个索引下的某个类型的某个文档，和关系型数据库中提到某个数据库中的某张表的某条数据是等价的。


# 安装运行elasticsearch

下载 elasticsearch-7.2.1，直接解压
* ./bin/elasticsearch 启动
* ./bin/elasticsearch -d 后台启动

HTTP 默认端口为9200，TCP 默认端口为9300，启动后可以通过 curl localhost:9200 访问，返回JSON 格式的对象

### 版本号

* elasticsearch-7.2.1-linux-x86_64.tar.gz
* elasticsearch-analysis-ik-7.2.1.zip
* elasticsearch-head-master.zip
* kibana-7.2.1-linux-x86_64.tar.gz
* node-v10.16.2-linux-x64.tar.xz

### 报错情况

* java.lang.UnsupportedOperationException: seccomp unavailable: CONFIG_SECCOMP not compiled into kernel, CONFIG_SECCOMP and CONFIG_SECCOMP_FILTER are needed
seccomp是linux kernel从2.6.23版本开始所支持的一种安全机制，内核版本 Linux version 2.6.32-573.el6.x86_64 会报错，需在elasticsearch.yml 文件末尾加上bootstrap.system_call_filter: false

* can not run elasticsearch as root
不能使用root 用户运行，建议使用单独的用户启动

* localhost:9200 可访问，本机和外机 IP:9200 不能访问
配置文件elasticsearch.yml 增加 network.host: 192.168.11.248，重启后发现还有报错
```
# max file descriptors [4096] for elasticsearch process is too low, increase to at least [65535]
su root
# 备份
cd /etc/security/
cp limits.conf limits.conf.bak
# 增加配置
vim limits.conf
# elasticsearch config start
* soft nofile 65536
* hard nofile 131072
* soft nproc 2048
* hard nproc 4096
# elasticsearch config end

# max number of threads [1024] for user [es] is too low, increase to at least [4096]
vim /etc/security/limits.d/90-nproc.conf
# 修改配置
* soft nproc 4096

# max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]
su root
sysctl -w vm.max_map_count=262144
# 验证
sysctl -a | grep "vm.max_map_count"

# the default discovery settings are unsuitable for production use; at least one of [discovery.seed_hosts, discovery.seed_providers, cluster.initial_master_nodes] must be configured
cluster.initial_master_nodes: ["node-1"]

```
通过上面的配置，重启成功了，此时，本机和外机使用IP:9200 可以访问，localhost 不能访问


# elasticsearch 基本配置

config 目录是存放配置文件的，elasticsearch.yml 是基本配置文件，jvm.options 是虚拟机参数配置文件，log4j2.properties 是日志配置文件

* cluster.name 配置Elasticsearch 集群名称，默认是“elasticsearch”；Elasticsearch 会自动发现在同一网段下的Elasticsearch 节点
* node.name 配置Elasticsearch 的节点名
* path.data 索引数据的存储路径，默认是根目录的data 文件夹
* path.logs 日志文件的存储路径，默认是根目录的log 文件夹


# 安装中文分词器

https://github.com/medcl/elasticsearch-analysis-ik/releases 版本号要与Elasticsearch 一致

* 将下载的包解压，放入Elasticsearch/plugins/ik 目录下，重启Elasticsearch，日志中 loaded plugin [analysis-ik] 表示插件安装成功。
* ik 的配置文件在elasticsearch-7.2.1/config/analysis-ik/IKAnalyzer.cfg.xml 或 elasticsearch-7.2.1/plugins/ik/config/IKAnalyzer.cfg.xml。ik 自定义词典需配置plugins/ik/config 的相对路路径。
* ik_max_word: 会将文本做最细粒度的拆分，比如会将“中华人民共和国国歌”拆分为“中华人民共和国,中华人民,中华,华人,人民共和国,人民,人,民,共和国,共和,和,国国,国歌”，会穷尽各种可能的组合，适合 Term Query
* ik_smart: 会做最粗粒度的拆分，比如会将“中华人民共和国国歌”拆分为“中华人民共和国,国歌”，适合 Phrase 查询

测试分词的请求：
```
# 创建索引
curl -XPUT http://192.168.11.248:9200/test
# 测试分词
curl -XGET "http://192.168.11.248:9200/test/_analyze" -H 'Content-Type: application/json' -d'
{
   "text":"洪荒之力","tokenizer": "ik_smart"
}'
```


# Head 插件

Head 插件是HTML 5 编写的，需要node.js 环境，是一个集群操作和管理工具，可以显示集群的拓扑结构，执行索引和节点级别的操作。
```
# 安装nodejs
# 下载 node-v10.16.2-linux-x64.tar.xz
yum install xz
cd /usr/local/
tar -xvf node-v10.16.2-linux-x64.tar.xz
mv node-v10.16.2-linux-x64 nodejs
ln -s /usr/local/nodejs/bin/node /usr/local/bin
ln -s /usr/local/nodejs/bin/npm /usr/local/bin

node -v
npm -v

# 安装 grunt-cli 基于命令的JavaScript 工程命令行构建工具
npm install -g grunt-cli
ln -s /usr/local/nodejs/bin/grunt /usr/local/bin/
grunt -version

# 下载安装head 插件源码  https://github.com/mobz/elasticsearch-head
cd elasticsearch-head-master
# 使用国内镜像安装
npm install -g cnpm --registry=https://registry.npm.taobao.org
```

安装成功之后，修改Elasticsearch 配置
```
# 开启HTTP 对外提供服务，使Head 插件能够访问Elasticsearch 集群
http.cors.enabled: true
http.cors.allow-origin: "*"
```

修改Head 插件配置文件，之后启动Head 插件
```
vim Gruntfile.js
connect: {
        server: {
                options: {
                        hostname: '192.168.10.100',
                        port: 9100,
                        base: '.',
                        keepalive: true
                }
        }
}

# 启动Head 插件
cd elasticsearch-head-master
grunt server
# 启动成功日志如下
Running "connect:server" (connect) task
Waiting forever...
Started connect web server on http://192.168.10.100:9100
```
访问http://192.168.10.100:9100/ 可以查看Elasticsearch 集群的情况
![](https://github.com/windfish/img/blob/master/notes-img/搜索/44bcc274225c37586c4024efa4b99a35931.jpg?raw=true)


# Kibana 工具

下载Kibana，kibana-7.2.1-linux-x86_64.tar.gz
```
sudo tar -zxvf kibana-7.2.1-linux-x86_64.tar.gz
# 修改配置
sudo vim config/kibana.yml

# Kibana 服务端口
server.port: 5601
# 绑定服务器地址，以便外部机器可以访问
server.host: "192.168.11.248"
# ES 地址
elasticsearch.hosts: ["http://192.168.11.248:9200"]

# 后台运行
nohup ./bin/kibana &
```
浏览器可以通过 http://192.168.11.248:5601 访问

### 遇到的问题
```
# [node-1] flood stage disk watermark [95%] exceeded on [AbQ6RdHsTleXuFvtCyBrLg]
# free: 214.6mb[3.2%], all indices on this node will be marked read-only
磁盘空间不足，导致Kibana 连不上ES 集群

Kibana 报错如下：
[elasticsearch] Request error, retrying 
GET http://192.168.10.100:9200/_nodes?filter_path=nodes.*.version%2Cnodes.*.http.publish_address%2Cnodes.*.ip => read ECONNRESET
[elasticsearch] Request error, retrying
PUT http://192.168.10.100:9200/_template/.management-beats => socket hang up
[elasticsearch] Request error, retrying
GET http://192.168.10.100:9200/_xpack => socket hang up
```



