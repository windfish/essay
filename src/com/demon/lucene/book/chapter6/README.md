# Elasticsearch 搜索机制

![](https://github.com/windfish/img/blob/master/notes-img/搜索/0c45389d7b58abf00808dea1465644e130b.jpg?raw=true)

```
# 创建索引并设置settings 和mapping
# 与书中的有点区别，ES 7.0中，默认去掉了自定义type_name（也就是“IT”），默认的type_name 是_doc；若要自定义类型名，则需include_type_name is set to true.
PUT books?include_type_name=true
{
  "settings":{
    "number_of_replicas": 1,
    "number_of_shards": 3
  },
  "mappings": {
    "IT":{
    "properties":{
      "id":{
        "type":"long"
      },
      "title":{
        "type":"text",
        "analyzer":"ik_max_word"
      },
      "language":{
        "type":"keyword"
      },
      "author":{
        "type":"keyword"
      },
      "price":{
        "type":"double"
      },
      "publish time":{
        "type":"date",
        "format":"yyyy-mm-dd"
      },
      "description":{
        "type":"text",
        "analyzer":"ik_max_word"
      }
    }
    }
  }
}
# 使用默认type_name 的命令如下
PUT books1
{
  "settings":{
    "number_of_replicas": 1,
    "number_of_shards": 3
  },
  "mappings": {
    "properties":{
      "id":{
        "type":"long"
      },
      "title":{
        "type":"text",
        "analyzer":"ik_max_word"
      },
      "language":{
        "type":"keyword"
      },
      "author":{
        "type":"keyword"
      },
      "price":{
        "type":"double"
      },
      "publish time":{
        "type":"date",
        "format":"yyyy-mm-dd"
      },
      "description":{
        "type":"text",
        "analyzer":"ik_max_word"
      }
    }
  }
}

# 执行bulk 批量导入命令把文档写入ES
curl -H "Content-Type: application/json" -XPOST "http://192.168.11.248:9200/_bulk?pretty" --data-binary @books.json

# 需注意，book.json 最后要留一行空白行
```
```
# 查询所有文档 match_all query
GET books/_search

# term 查询用来查询指定字段中包含给定单词的文档，term 查询不被解析，查询的是词项，只有查询词和文档中的词精确匹配才会被搜索到，应用场景为查询任命、地名等需要精确匹配的需求
GET books/_search
{
    "query": {
        "term": {"title": "思想"}
    }
}

# ES 提供结果分页的两个属性：from 和size，from 指定返回结果的尅是位置，默认0；size 指定一次返回结果所包含的最大文档数量
GET books/_search
{
    "from": 0,
    "size": 100,
    "query": {
        "term": {"title": "思想"}
    }
}

# 查询默认返回文档包含的所有字段，也支持只返回某些字段
GET books/_search
{
    "_source": ["title", "author"],
    "query": {
        "term": {"title": "java"}
    }
}

# 默认查询不返回版本号的，设置version 为true 就可返回版本号
GET books/_search
{
    "version": true,
    "query": {
        "term": {"title": "java"}
    }
}

# ES 提供了基于最小评分的过滤机制，可以通过min_score 设置最低评分数
GET books/_search
{
    "min_score": 0.6,
    "query": {
        "term": {"title": "java"}
    }
}

# 高亮显示查询关键字
GET books/_search
{
    "query": {
        "term": {"title": "编程"}
    },
    "highlight": {
        "fields": {
            "title"： {}
        }
    }
}
```

# 全文搜索

### match query
match 查询会解析查询语句，对查询语句进行分词，分词后任何一个词项被匹配，文档就会被搜索到；若想要匹配所以关键词的文档，可以用and 操作符连接
```
GET books/_search
{
    "query": {
        "match": {"title": "java编程思想"}
    }
}

GET books/_search
{
    "query": {
        "match": {
          "title": {
            "query": "java编程思想",
            "operator": "and"
          }
        }
    }
}
```

### match_phrase query
match_phrase 会把query 内容分词，分词器可以自定义，同时文档还需满足：分词后所有词项都出现在该字段中；字段中的词项顺序要一致
```
PUT test/_doc/10
{
  "foo": "I just said hello world."
}
PUT test/_doc/11
{
  "foo": "hello world"
}
PUT test/_doc/12
{
  "foo": "world hello"
}
# 只有前两个会被匹配
GET test/_search
{
  "query":{
    "match_phrase": {"foo":"hello world"}
  }
}
```

### match_phrase_prefix query
match_phrase_prefix 与match_phrase 类似，只不过支持最后一个词项前缀匹配
```
GET test/_search
{
  "query":{
    "match_phrase_prefix": {"foo":"hello w"}
  }
}
```

### multi_match query
multi_match 是match 的升级，用于搜索多个字段
```
GET books/_search
{
  "query": {
    "multi_match": {
      "query": "java编程",
      "fields": ["title", "description"]
    }
  }
}
# 支持要搜索的字段名称使用通配符
GET books/_search
{
  "query": {
    "multi_match": {
      "query": "java编程",
      "fields": ["title", "*_name"]
    }
  }
}
# 使用指数符指定搜索字段的权重，指定关键字出现在title 中的权重是出现在description 中的三倍
GET books/_search
{
  "query": {
    "multi_match": {
      "query": "java编程",
      "fields": ["title^3", "description"]
    }
  }
}
```

### common_terms query
common_terms 查询是一种在不牺牲性能的情况下替代停用词提高搜索准确率和召回率的方案。
其解决方法是：把query 分词后的词项分为重要词项（低频分词）和不重要的词项（高频词，也就是停用词）。
搜索的时候，首先搜索和重要词项匹配的文档，这些文档是词项出现较少并且词项对评分影响较大的文档。
执行第二次查询，在第一次搜索的文档中，搜索对评分影响较小的高频词项；如果一个查询只包含高频词，那么会通过and 执行一个单独的查询，也就是会搜索所以的高频词项。
可以通过cutoff_frequency 来设置阈值确定是高频词还是低频词；词频之间可以用low_freq_operator、high_freq_operator 连接
```
GET /_search
{
  "query": {
    "common": {
      "body": {
        "query": "nelly the elephant as a cartoon",
        "cutoff_frequency": 0.001,
        "low_freq_operator": "and"
      }
    }
  }
}
```


# 词项查询
全文搜索在执行之前会分析查询字符串，词项搜索时对倒排索引中存储的词项进行精确搜索。
通常用于结构化数据，如数字、日期、枚举类型

### term query 和 terms query
terms 查询是term 查询的升级版，可以用来查询文档中包含多个词的文档
```
GET books/_search
{
  "query":{
    "terms":{
      "title":["java", "python"]
    }
  }
}
```

### range query
range 查询用于匹配在某一范围内的数值类型、日期类型或字符串型字段的文档。
range 查询只能搜索一个字段，不能作用于多个字段，支持的参数有以下几种：
* gt 大于，查询范围最小值，不包含临界值
* gte 大于等于，和gt 的区别在于包含临界值
* lt 小于，查询范围的最大值，不包含临界值
* lte 小于等于，和lt 的区别在于包含临界值

```
GET books/_search
{
  "query":{
    "range":{
      "price": {
        "gt": 50,
        "lte": 70
      }
    }
  }
}
GET books/_search
{
  "query":{
    "range":{
      "publish time": {
        "gte": "2016-01-01",
        "lte": "2016-12-31",
        "format": "yyyy-mm-dd"
      }
    }
  }
}
```

### exists query
exists 查询会返回字段中至少有一个非空值的文档
```
GET books/_search
{
  "query":{
    "exists":{
      "field": "author"
    }
  }
}
```

### prefix query
prefix 查询用于查询某个字段中以给定前缀开始的文档
```
GET books/_search
{
  "query":{
    "prefix":{
      "description": "win"
    }
  }
}
```

### wildcard query
wildcard 查询支持单字符通配符和多字符通配符，?用来匹配单个字符，*用来匹配零个或多个字符。
```
GET books/_search
{
  "query":{
    "wildcard":{
      "author": "张若*"
    }
  }
}
```

### regexp query
正则表达式查询
```
GET books/_search
{
  "query":{
    "regexp":{
      "language": "py[a-z].+"
    }
  }
}
```

### fuzz query
编辑距离又称Levenshtein 距离，指两个字符串之间，由一个转变为另一个所需的最少编辑次数，允许的编辑操作包括替换一个字符、插入一个字符、删除一个字符。
fuzzy 查询就是通过计算词项与文档的编辑距离来得到结果的，适用于模糊查询的场景
```
GET books/_search
{
  "query":{
    "fuzzy":{
      "language": "pythno"
    }
  }
}
```

### type query
用于查询具有指定类型的文档
```
GET books/_search
{
  "query":{
    "type":{
      "value": "IT"
    }
  }
}
```

### ids query
查询具有指定id 的文档，类型是可选的，可以省略，也可以接受一个数组
```
GET books/_search
{
  "query":{
    "ids":{
      "type": "IT",
      "values": [1, 2, 5]
    }
  }
}
```


# 复合查询

### constant_score query
包装一个其他类型的查询，并返回匹配过滤器中的查询条件，且返回的文档评分为指定的评分
```
GET books/_search
{
  "query":{
    "constant_score":{
      "filter": {
        "term": {
          "title": "java"
        }
      },
      "boost": 1.1
    }
  }
}
```

### bool query
可以把任意多个简单查询组合在一起，使用must、should、must_not、filter 选项来表示简单查询之间的逻辑，每个选项可以出现0到多次，含义如下：
* must 文档必须匹配must 选项下的查询条件，相当于逻辑运算AND
* should 文档可以匹配should 选项下的查询条件，也可以不匹配，相当于逻辑运算OR
* must_not 与must 相反，匹配查询条件的文档不会返回
* filter 和must 一样，匹配查询条件的文档才会返回，但是不评分，只过滤
```
GET books/_search
{
  "query":{
    "bool":{
      "minimum_should_match": 1, 
      "must": [{
        "match": {"title": "java"}
      }],
      "should": [
        {"match": {
          "description": "虚拟机"
        }}
      ],
      "must_not": [
        {"range": {
          "price": {
            "gte": 70
          }
        }}
      ]
    }
  }
}
```

### function_score query
可以修改查询的文档得分，用户需定义一个查询和一至多个评分函数，评分函数会对查询到的每个文档分别计算得分
```
# 索引所有文档，文档的最大得分为5，每个文档得分随机生成，权重的计算模式为相乘模式
GET books/_search
{
  "query":{
    "function_score": {
      "query": {"match_all": {}},
      "boost": 5, 
      "random_score": {},
      "boost_mode": "multiply"
    }
  }
}
# 自定义评分公式，把price 值的十分之一的开方作为文档的得分
GET books/_search
{
  "query":{
    "function_score": {
      "query": {
        "match": {
          "title": "java"
        }
      },
      "script_score": {
        "script": {
          "inline": "Math.sqrt(doc['price'].value/10)"
        }
      }
    }
  }
}
```

### boosting query
用于需要对两个查询的评分进行调整的场景
```
# positive 中的查询的评分保持不变，negative 中的查询会降低文档评分，negative_boost 指明negative 中的降低的权重值
# publish time <= 2018-01-01 的文档评分为原评分的0.2倍
GET books/_search
{
  "query":{
    "boosting": {
      "positive": {
        "match": {
          "title": "python"
        }
      },
      "negative": {
        "range": {
          "publish time": {
            "lte": "2018-01-01"
          }
        }
      },
      "negative_boost": 0.2
    }
  }
}
```


# 位置索引
geo_point 类型，维度在前，经度在后

```
PUT geo?include_type_name=true
{
  "mappings": {
    "city": {
      "properties": {
        "name":{
          "type": "keyword"
        },
        "location": {
          "type": "geo_point"
        }
      }
    }
  }
}
# 批量导入
curl -H "Content-Type: application/json" -XPOST "http://192.168.11.248:9200/_bulk?pretty" --data-binary @geo.json
```

### geo_distance query
可以查找在一个中心点指定范围内的地理点文档
```
# 查询天津200km 以内的城市
GET geo/_search
{
  "query": {
    "bool": {
      "must": [
        {"match_all": {}}
      ],
      "filter": {
        "geo_distance": {
          "distance": "200km",
          "location": {
            "lat": 39.085100000,
            "lon": 117.1993700000
          }
        }
      }
    }
  }
}
# 按各城市离北京的距离排序
GET geo/_search
{
  "query": {
    "match_all": {}
  },
  "sort": [
    {
      "_geo_distance": {
        "location": {
          "lat": 39.9088145109,
          "lon": 116.3973999023
        }, 
        "unit": "km"
      }
    }
  ]
}
```

### geo_bounding_box query
用于查找落入指定的矩形内的地理坐标，查询由两个点确定一个矩形
```
# 银川和南昌确定的矩形区域内的城市
GET geo/_search
{
  "query": {
    "bool": {
      "must": [
        {"match_all": {}}
      ],
      "filter": {
        "geo_bounding_box": {
          "location": {
            "top_left": {
              "lat": 38.4864400000,
              "lon": 106.2324800000
            },
            "bottom_right": {
              "lat": 28.6820200000,
              "lon": 115.8579400000
            }
          }
        }
      }
    }
  }
}
```

### geo_polygon query
用于查询在指定多边形内的地理点
```
# 呼和浩特、重庆、上海构成的三角形区域内的城市
GET geo/_search
{
  "query": {
    "bool": {
      "must": [
        {"match_all": {}}
      ],
      "filter": {
        "geo_polygon": {
          "location": {
            "points": [
              {
                "lat": 40.8414900000,
                "lon": 115.7519900000
              },
              {
                "lat": 29.5647100000,
                "lon": 106.5507300000
              },
              {
                "lat": 31.2303700000,
                "lon": 121.4737000000
              }
            ]
          }
        }
      }
    }
  }
}
```

### geo_shape query
用于查询geo_shape 类型的地理数据，地理形状直接的关系有相交、包含、不相交三种。
geo_shape 类型的字段，经度在前，维度在后
```
# 创建一个新的索引
PUT geoshape?include_type_name=true
{
  "mappings": {
    "city": {
      "properties": {
        "name":{
          "type": "keyword"
        },
        "location": {
          "type": "geo_shape"
        }
      }
    }
  }
}
# 写入西安-郑州 连成的线
POST geoshape/city/1
{
  "name": "西安-郑州",
  "location": {
    "type": "linestring",
    "coordinates": [
      [108.9398400000,34.3412700000],
      [113.6587142944,34.7447157466]
    ]
  }
}
# 查询银川和南京组成的矩形内的shape
GET geoshape/_search
{
  "query": {
    "bool": {
      "must": [
        {"match_all": {}}
      ],
      "filter": {
        "geo_shape": {
          "location": {
            "shape": {
              "type": "envelope",
              "coordinates": [
                [106.23248,38.48644],
                [115.95794,28.68202]
              ]
            },
            "relation": "within"
          }
        }
      }
    }
  }
}
```

# 特殊查询

### more_like_this query
可以查询和提供文本类似的文档，通常用于近似文本的推荐等场景
```
GET books/_search
{
  "query": {
    "more_like_this": {
      "fields": [
        "title", "description"
      ],
      "like": "java virtual machine",
      "min_term_freq": 1,
      "max_query_terms": 12
    }
  }
}
```
可选参数及取值：
* fields 要匹配的字段，默认是_all 字段
* like 要匹配的文本
* min_term_freq 文档中词项的最低频率，低于此频率的文档会被忽略
* max_query_terms 文档中能包含的最大词项数

### script query
支持使用脚本进行查询
```
# 查询价格大于70 的文档
GET books/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "script": {
            "script": {
              "inline": "doc['price'].value > 70",
              "lang": "painless"
            }
          }
        }
      ]
    }
  }
}
```


# 搜索高亮

### 自定义高亮片段
在高亮属性中给需要高亮的字段加上pre_tags 和post_tags
```
GET books/_search
{
  "query": {
    "match": {
      "title": "javascript"
    }
  },
  "highlight": {
    "fields": {
      "title": {
        "pre_tags": ["<strong>"],
        "post_tags": ["</strong>"]
      }
    }
  }
}
```

### 多字段高亮
require_field_match 设置为false；默认为true，只会高亮匹配的字段
```
GET books/_search
{
  "query": {
    "match": {
      "title": "javascript"
    }
  },
  "highlight": {
    "require_field_match": "false", 
    "fields": {
      "title": {
        "pre_tags": ["<strong>"],
        "post_tags": ["</strong>"]
      },
      "description": {}
    }
  }
}
```

### ES 高亮器
* 默认的highlight 是最基本的高亮器，需要对_source 中的文档进行二次分析，速度是最慢的，优点是不需要额外的存储空间
* postings-highlighter 高亮器不需要二次分析，但需要在字段映射中设置index_options 值为offsets，即保存字段的偏移量
* fast-vector-highlighter 高亮器速度最快，但需要在字段的映射中设置term_vector 参数取值为with_positions_offsets，即保存关键词的位置和偏移信息，占用的存储空间最大
```
PUT /example
{
  "mappings": {
    "properties": {
      "comment": {
        "type": "text",
        "index_options": "offsets"
      }
    }
  }
}
PUT /example
{
  "mappings": {
    "properties": {
      "comment": {
        "type": "text",
        "term_vector": "with_positions_offsets"
      }
    }
  }
}
```


# 搜索排序
```
# 默认排序，是按评分降序排序
GET books/_search
{
  "query": {
    "term": {
      "title": "java"
    }
  }
}
# 多字段排序
GET books/_search
{
  "sort": [
    {
      "price": {
        "order": "desc"
      },
      "year": {
        "order": "asc"
      }
    }
  ]
}
# ES 是在每一个分片上单独打分的，分片的数量会影响打分的结果；分词器也会影响打分的结果
```










