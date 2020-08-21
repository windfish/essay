以下的命令都是利用 Kibana 的开发工具执行的

# 索引管理

索引名称不能出现大写字母
```
# 创建索引
PUT blog

# ES 默认给一个索引设置5个分片1个副本，索引的分片数一经指定不能修改，副本可以通过命令随时修改
# 可以在创建索引时指定分片数和副本数
PUT blog1
{
    "settings": {
        "number_of_shards": 3,
        "number_of_replicas": 0
    }
}
```
```
# 更新索引
PUT blog/_settings
{
    "number_of_replicas": 2
}
```

索引还可以设置读写权限：
* blocks.read_only: true 索引只允许读，不允许写或更新
* blocks.read: true 禁止对当前索引进行读操作
* blocks.write: true 禁止对当前索引进行写操作
```
PUT blog/_settings
{
    "blocks.write": true
}
# 设置后blog 索引不能写入数据
PUT blog/article/1
{
    "title": "Java 虚拟机"
}

# 索引恢复写入权限
PUT blog/_settings
{
    "blocks.write": false
}
```
```
# 查看索引配置
GET blog/_settings
# 查看多条索引配置
GET blog,blog1/_settings
# 查看索引索引
GET _all/_settings
```
```
# 删除索引
DELETE blog1
```

索引可以进行打开与关闭操作，关闭后的索引几乎不占用系统资源，关闭后的索引不能进行读写操作
```
# 关闭索引，也支持关闭多个索引
POST blog/_close
POST blog,blog1/_close
# 打开索引
POST blog_open
# 开关操作也支持通配符和_all
POST _all/_close
POST test*/_close
```

复制索引：_reindex API 可以把文档从源索引复制到目标索引，目标索引不会复制源索引中的配置信息，_reindex 操作之前需要设置目标索引的分片数、副本数等信息
```
POST _reindex
{
    "source": {"index": "blog"},
    "dest": {"index": "blog_new"}
}
# 可以加入type 和query 来限制文档
POST _reindex
{
    "source": {
        "index": "blog",
        "type": "article",
        "query": {
            "term": { "title": "git" }
        }
    },
    "dest": {
        "index": "blog_new"
    }
}
```

收缩索引：一个索引的分片初始化后是无法再修改的，但是可以使用shrink index API 提供的缩小索引分片数机制，把一个索引变成一个更少分片的索引。
收缩后的分片数必须是原始分片的因子，比如8个分片可以收缩为4、2、1，若分片数是素数（7、11等），则只能收缩为1。
收缩索引之前，索引中的每个分片都要在同一个节点上，并且被标记为只读，节点健康值为绿色的。

收缩索引的过程：
* 创建一个目标索引，设置与源索引相同，但目标索引的分片数较少
* 把源索引硬链接到目标索引，若文件系统不支持硬链接，那么所有端都会被复制到新索引中，这是一个耗费更多时间的过程
* 新的索引恢复使用
```
POST blog/_shrink/blog_new
{
    "settings": {
        "index.number_of_replicas": 0,
        "index.number_of_shards": 1,
        "index.codec": "best_compression"
    },
    "aliases": {
        "my_search_indices": {}
    }
}
```

索引别名：就是给一个索引或多个索引起另一个名字
```
# 新增别名
POST /_aliases
{
    "actions": [
        {"add": {"index": "test1", "alias": "alias1"}}
    ]
}
# 删除别名
POST /_aliases
{
    "actions": [
        {"remove": {"index": "test1", "alias": "alias1"}}
    ]
}
```


# 文档管理

```
# 新建文档
PUT blog/article/1
{
    "id": 1,
    "title": "Git 简介",
    "posttime": "2019-08-19",
    "content": "Git 是一款免费、开源的分布式版本控制系统"
}
# 若不指定docID，则使用POST 命令，docID 会自动生成
POST blog/article
{
    "id": 1,
    "title": "Git 简介",
    "posttime": "2019-08-19",
    "content": "Git 是一款免费、开源的分布式版本控制系统"
}
```
```
# 获取文档
GET blog/article/1
# 检查一个文档是否存在，文档存在，返回“200 - OK”，不存在，返回“404 - Not Found”
HEAD blog/article/1

# Multi GET API 可以获取多个文档
# 根据索引名、类型名、docID 一次性获取多个文档，返回一个文档数组
GET _mget
{
    "docs": [
        {"_index": "blog", "_type": "article", "_id": 1},
        {"_index": "blog1", "_type": "article", "_id": 2}
    ]
}
# 同一个index 下不同type 的文档，可以简写：
GET blog/_mget
{
    "docs": [
        {"_type": "article", "_id": 1},
        {"_type": "other", "_id": 2}
    ]
}
# 如果index 和type 都相同，可以简写：
GET blog/article/_mget
{
    "docs": [
        {"_id": 1},
        {"_id": 2}
    ]
}
# 进一步简化
GET blog/article/_mget
{
    "ids": [ 1, 2 ]
}
```

文档被索引之后，如果要更新，那么Elasticsearch 内部首先找到这个文档，删除旧的文档内容执行更新，更新后再索引新的文档
```
# 先索引一个文档
PUT test/type1/1
{
  "counter": 1,
  "tags": ["red"]
}
# 更新文档，counter 字段值加4
POST test/type1/1/_update
{
    "script": {
        "inline": "ctx._source.counter += params.count",
        "lang": "painless",
        "params": {
            "count": 4
        }
    }
}
# inline 是执行的脚本，ctx 是脚本语言中的一个执行对象，painless 是Elasticsearch 内置的一种脚本语言，params 是参数集合
# ctx 对象除了可以访问_source，还可以访问_index、_type、_id、_version、_routing、_parent 等字段

# 数组类型的字段，增加一个值
POST test/type1/1/_update
{
    "script": {
        "inline": "ctx._source.tags.add(params.tag)",
        "lang": "painless",
        "params": {
            "tag": "blue"
        }
    }
}
# 给文档增加一个字段
POST test/type1/1/_update
{
    "script": "ctx._source.new_field = \"value_of_new_field\""
}
# 移除一个字段
POST test/type1/1/_update
{
    "script": "ctx._source.remove(\"new_field\")"
}
# 删除tags 数组中含有red 的文档，ctx.op = \"delete\" 表示删除文档，ctx.op = \"none\" 表示什么都不做
POST test/type1/1/_update
{
    "script": {
        "inline": "if(ctx._source.tags.contains(params.tag)){ctx.op = \"delete\"} else {ctx.op = \"none\"}",
        "lang": "painless",
        "params": {"tag": "red"}
    }
}

# upset 操作，作用是当文档不存在时，upset 会新建文档；文档存在，则正常秩序script 脚本
POST test/type1/1/_update
{
    "script": {
        "inline": "ctx._source.counter += params.count",
        "lang": "painless",
        "params": {
            "count": 4
        }
    },
    "upsert": {
        "counter": 1
    }
}
```
```
# 查询更新文档，使用Update By Query API
POST blog/_update_by_query
{
    "script": {
        "inline": "ctx._source.category = params.category",
        "lang": "painless",
        "params": { "category": "git" }
    },
    "query": {
        "term": {"title": "git"}
    }
}
```

### 版本控制

Elasticsearch 是一个分布式系统，当文档创建、更新、删除，新版本的文档必须要复制到集群中的其他节点，并且复制请求是并行发送的，ES 使用乐观锁机制来保证文档的版本正确。
ES 使用_version 字段确保更新有序进行，文档每被修改一次，文档编号就会增加一次。
ES 分为内部版本控制和外部版本控制，内部版本控制要求每次操作请求，版本号一致才能操作成功；外部版本控制要求外部文档版本比内部版本高时才能操作成功。

### 路由机制

ES 的路由机制是通过哈希算法，将具有相同哈希值的文档放置到同一个主分区中，分片位置计算方法：
```
shard = hash(routing) % number_of_primary_shards
```
routing 值可以是任意字符串，ES 默认将文档的id 值作为routing 值。

一个有50个分片的索引，在集群上执行一次查询的过程是：
* 查询请求首先被集群中的一个节点接收
* 接收到这个请求的节点，将这个请求广播到这个索引的每个分片上
* 每个分片执行完搜索查询并返回结果
* 结果在通道节点上合并、排序并返回给用户








