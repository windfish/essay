# 分词器 Analyzer

Analyzer 是一个抽象类，切分词的具体规则由子类实现。内部通过TokenStream 实现，Tonkenizer 类和TokenFilter 类是TokenStream 的两个子类。
* Tokenizer 处理单个字符组成的字符流，读取Reader 对象中的数据，处理后转换成词汇单元
* TokenFilter完成文本过滤器的功能，但在使用过程中必须注意不同过滤器的使用顺序

#### Lucene 提供的分词器

* StopAnalyzer： 停用词分词器，能过滤词汇中特定的字符串和词汇，并且完成大写转小写的功能
* StandardAnalyzer： 标准分词器，根据空格和符号来完成分词，还可以完成数字、字母、email 地址、IP地址以及中文字符的分析处理，还可以支持过滤词表
* WhitespaceAnalyzer： 空格分词器，使用空格作为间隔符的词汇分词器
* SimpleAnalyzer： 简单分词器，基于西方文字符词汇分析的分词器，处理词汇单元时，以非字母字符作为分割符号。输出的词汇单元完成小写字符转换，去掉标点符号等分割符
* CJKAnalyzer： 二分法分词器，内部调用CJKTokenizer 分词器，对中文进行分词，同时使用StopFilter 过滤器完成过滤功能，可以实现中文的多元切分和停用词过滤
* KeywordAnalyzer： 关键词分词器，把整个输入作为一个单独词汇单元，方便特殊类型的文本进行索引。针对邮政编码、地址等文本信息使用关键词分词器进行索引建立非常方便


# Lucene 索引

### Lucene 字段类型

文档是Lucene 索引的最小单位，比文档更小的单位是字段，字段是文档的组成部分，由三部分组成：名称（name）、类型（type）和取值（value）。

字段的取值一般为文本类型（字符串、字符串流等）、二进制类型和数值类型

字段类型主要有：
* TextField 会把改字段的内容索引并词条化，但是不保存词向量。例如，包含整篇文档内容的body 字段，通常使用TextField 类型进行索引
* StringField 只会对该字段的内容进行索引，并不词条化，也不保存词向量。例如，字段是国家名称，字段名为“country”，以国家“阿尔及利亚”为例，只索引不词条化最合适
* IntPoint 适合索引值为int 类型的字段。IntPoint 是为了快速过滤的，如果需要展示出来要另存一个字段。例如，商品数量使用“productCount”字段存储，过滤时，可以直接使用该字段获取结果，但是要展示商品数量，则需要另外存储一个字段
* LongPoint 适合索引值为long 类型的字段。用法与IntPoint 类似
* FloatPoint 适合索引值为float 类型的字段。用法与IntPoint 类似
* DoublePoint 适合索引值为double 类型的字段。用法与IntPoint 类似
* SortedDocValuesField 适合索引值为文本内容并且需要按值进行排序的字段
* SortedSetDocValuesField 适合索引值为文本内容并且需要按值进行分组、聚合等操作的字段
* NumericDocValuesField 存储单个数值类型的DocValues 字段，主要包括int、long、float、double
* SortedNumericDocValuesField 存储数值类型的有序数组列表的DocValues 字段
* StoredField 适合索引只需要保存字段值不进行其他操作的字段

DocValues 是Lucene 4.X 之后新增的重要特性，在构建索引时额外建立一个有序的基于docment=>field/value（文档到字段级别）的映射列表，减轻了在排序和分组时对内存的依赖。

### 建立索引

* 建立索引文档需要依靠IndexWriter 对象，创建IndexWriter 对象需提供两个参数，一个IndexWriterConfig 对象，该对象可以设置创建索引使用哪种分词器，另一个是索引保存的路径
* IndexWriter 对象的addDocument() 方法用于添加文档，该方法的参数是Document 对象。一次可以添加多个Document，最后调用commit()生成索引
* IndexWriterConfig 对象的setOpenMode() 方法设置索引的打开方式，OpenMode.CREATE 表示先清空索引再重新创建，OpenMode.CREATE_OR_APPEND 表示不存在则新建，已存在则附加
* Directory 对象用于表示索引的位置，把索引路径和IndexWriterConfig 对象传入IndexWriter()方法，实例化IndexWriter 对象，之后通过IndexWriter 对象进行文档操作
* 文档是Lucene 索引和搜索的基本单位，Document 表示文档，比文档更小的单位是域，也称为字段，一个文档可以有多个域
* FieldType 对象用于指定域的索引信息，例如是否解析、是否存储、是否保存词项频率、位移信息等，setIndexOptions() 方法可以设定域的索引选项
```
public enum IndexOptions {
   NONE,    // 不索引
   DOCS,    // 只索引文档，词项频率和位移信息不保存
   DOCS_AND_FREQS,  // 只索引文档和词项频率，位移信息不保存
   DOCS_AND_FREQS_AND_POSITIONS,    // 索引文档、词项频率和位移信息
   DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS;    // 索引文档、词项频率、位移信息和偏移量
}
```
* FieldType 对象提供方法设置相对增量和位移信息
```
setStored(boolean) 参数默认值false，设置为true 表示存储字段值
setTokenized(boolean) 设置为true 表示使用配置的分词器对字段值进行词条化
setStoreTermVectors(boolean) 设置为true 表示保存词向量
setStoreTermVectorPositions(boolean) 设置为true 表示保存词项在词向量中的位移信息
setStoreTermVectorOffsets(boolean) 设置为true 表示保存词项在词向量中的偏移信息
```


# Lucene 搜索

* 用户输入查询关键词后，Lucene 对关键词进行分析处理，构建Query 对象
* Lucene 搜索文档需要实例化一个IndexSearch 对象，使用search()方法完成搜索过程，搜索结果会保存在TopDocs 类型的文档集合中
* QueryParser 解析用户输入的工具，可以扫描用户输入的字符串生成Query 对象，可以搜索单个字段
* MultiFieldQueryParser 可查询多个字段
* TermQuery 词项搜索，先构建一个Term 对象，然后使用Term 对象构造一个TermQuery 对象
* BooleanQuery 布尔搜索，是一种组合的Query，在使用时可以把各种Query 对象添加进去并标明他们之间的逻辑关系
* RangeQuery 范围搜索，表示在某范围内的搜索条件，实现一个开始词条到一个结束词条的搜索功能
* PrefixQuery 前缀搜索，先定义个词条Term，该Term 包含要查找的字段名和关键字前缀，然后构造一个PrefixQuery 对象
* PhraseQuery 多关键字搜索，使用情景：输入的关键字并非一个简单的单词，而是有多个关键字，关键字要么紧密相连，要么之间还插有其他无关的内容
* FuzzyQuery 模糊搜索，可以简单的识别两个相近的词语，例如：由于拼写错误，“Trump”拼成“Trmp”或“Tramp”，仍能搜索到正确的结果
* WildcardQuery 通配符搜索





