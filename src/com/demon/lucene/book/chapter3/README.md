## 项目需求

1. 能够对文件名进行检索
2. 能够对文件内容进行检索
3. 能够下载检索到的文件
4. 能够实现关键字的高亮


## 架构设计

![](https://github.com/windfish/img/blob/master/notes-img/搜索/f3a15d93125fc1afa9c411d33868ea0238c.jpg?raw=true)

* controller 搜索和下载 http 服务
* model 文件对象
* service 创建索引和html 关键字替换工具
* WebContent/files 原始文件
* WebContent/indexdir 索引文件
* WebContent/lucene.jsp 搜索系统主页
* WebContent/result.jsp 搜索结果展示页面
* WebContent/error.jsp 搜索错误页面


## 文件解析

确定要解析的文件类型 --> 实例化对应的文件类型的解析器Parser --> 提取文件内容

```
// 解析 PDF 文档
PDFParser parser = new PDFParser();
// 解析 MS Office 文档
OOXMLParser ooxmlParser = new OOXMLParser();
// 解析文本文件
TXTParser txtParser = new TXTParser();
// 解析HTML 文件
HtmlParser htmlParser = new HtmlParser();
// 解析XML 文件
XMLParser xmlParser = new XMLParser();
// 解析class 文件
ClassParser classParser = new ClassParser();
```

Tika 自动解析文档的过程：
* 首先，传入一个文件到Tika，文件类型可以是任意的，Tika 使用自身的类型检查机制来检查文件类型
* Tika 提供了一个解析器库，包含多种类型的解析器。根据文件类型来选择合适的解析器
* 将文档传入解析器中，进行文档内容的提取、元数据提取



