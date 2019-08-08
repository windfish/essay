package com.demon.lucene.book.chapter2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import com.demon.lucene.book.ik.IKAnalyzer8x;
import com.demon.lucene.book.utils.LuceneUtil;

/**
 * 构建索引
 * @author xuliang
 * @since 2019年8月7日 上午10:25:34
 *
 */
public class CRUDIndex {

//    private String indexDir = "/data/lucene/book/chapter2";
    
    // 创建IK 分词器
    private Analyzer analyzer = new IKAnalyzer8x();
    
    private FieldType idType = null;
    private FieldType titleType = null;
    private FieldType contentField = null;
    {
        // 设置新闻ID索引并存储
        idType = new FieldType();
        idType.setIndexOptions(IndexOptions.DOCS);
        idType.setStored(true);
        
        // 设置新闻标题索引文档、词项频率、位移信息和偏移量，存储并词条化
        titleType = new FieldType();
        titleType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        titleType.setStored(true);
        titleType.setTokenized(true);
        
        contentField = new FieldType();
        contentField.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        contentField.setStored(true);
        contentField.setTokenized(true);
        contentField.setStoreTermVectors(true);
        contentField.setStoreTermVectorPositions(true);
        contentField.setStoreTermVectorOffsets(true);
        contentField.setStoreTermVectorPayloads(true);
    }
    
    /**
     * 创建索引
     */
    @Test
    public void create() {
        // 创建三个News 对象
        News news1 = new News();
        news1.setId(1);
        news1.setTitle("习近平会见美国总统奥巴马，学习国外经验");
        news1.setContent("国家主席习近平9月3日在杭州西湖国宾馆会见前来出席二十国集团领导人杭州峰会的美国总统奥巴马。");
        news1.setReply(672);
        
        News news2 = new News();
        news2.setId(2);
        news2.setTitle("北大迎来4380名新生，农村学生700多人近年最多");
        news2.setContent("昨天，北京大学迎来4380名来自全国各地及数十个国家的本科新生。其中，农村学生共700余名，为近年最多。");
        news2.setReply(995);
        
        News news3 = new News();
        news3.setId(3);
        news3.setTitle("特朗普（Donald Trump）宣誓就任美国第45任总统");
        news3.setContent("当地时间1月20日，唐纳德·特朗普在美国国会宣誓就职，正式成为美国第45任总统。");
        news3.setReply(1872);
        
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        Directory dir = null;
        IndexWriter indexWriter = null;
        // 索引目录
        Path path = Paths.get(LuceneUtil.indexDir);
        // 开始时间
        Date start = new Date();
        try{
            if(!Files.isReadable(path)){
                System.out.println("index dir " + path.toAbsolutePath() + " does not exist or can not readable, please check the path.");
                System.exit(1);
            }
            dir = FSDirectory.open(path);
            indexWriter = new IndexWriter(dir, config);

            Document doc1 = new Document();
            doc1.add(new Field("id", String.valueOf(news1.getId()), idType));
            doc1.add(new Field("title", news1.getTitle(), titleType));
            doc1.add(new Field("content", news1.getContent(), contentField));
            doc1.add(new IntPoint("reply", news1.getReply()));
            doc1.add(new StoredField("reply_display", news1.getReply()));
            
            Document doc2 = new Document();
            doc2.add(new Field("id", String.valueOf(news2.getId()), idType));
            doc2.add(new Field("title", news2.getTitle(), titleType));
            doc2.add(new Field("content", news2.getContent(), contentField));
            doc2.add(new IntPoint("reply", news2.getReply()));
            doc2.add(new StoredField("reply_display", news2.getReply()));
            
            Document doc3 = new Document();
            doc3.add(new Field("id", String.valueOf(news3.getId()), idType));
            doc3.add(new Field("title", news3.getTitle(), titleType));
            doc3.add(new Field("content", news3.getContent(), contentField));
            doc3.add(new IntPoint("reply", news3.getReply()));
            doc3.add(new StoredField("reply_display", news3.getReply()));
            
            indexWriter.addDocument(doc1);
            indexWriter.addDocument(doc2);
            indexWriter.addDocument(doc3);
            indexWriter.commit();
            indexWriter.close();
            dir.close();
            
        }catch(IOException e){
            e.printStackTrace();
        }
        Date end = new Date();
        System.out.println("索引文档用时：" + (end.getTime() - start.getTime()) + " ms");
        
    }
    
    /**
     * 删除索引
     */
    @Test
    public void delete(){
        deleteDoc("title", "美国");
    }
    
    private void deleteDoc(String field, String key){
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        Path path = Paths.get(LuceneUtil.indexDir);
        Directory dir;
        try{
            dir = FSDirectory.open(path);
            IndexWriter indexWriter = new IndexWriter(dir, config);
            indexWriter.deleteDocuments(new Term(field, key));
            indexWriter.commit();
            indexWriter.close();
            dir.close();
            System.out.println("删除完成！");
            
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 更新索引，本质上是先删除索引，再新建
     */
    @Test
    public void update(){
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        Path path = Paths.get(LuceneUtil.indexDir);
        Directory dir;
        try{
            dir = FSDirectory.open(path);
            IndexWriter indexWriter = new IndexWriter(dir, config);
            Document doc = new Document();
            doc.add(new Field("id", "2", idType));
            doc.add(new Field("title", "北京大学开学迎来4380名新生", titleType));
            doc.add(new Field("content", "昨天，北京大学迎来4380名来自全国各地及数十个国家的本科新生。其中，农村学生共700余名，为近年最多。", contentField));
            indexWriter.updateDocument(new Term("title", "北大"), doc);
            indexWriter.commit();
            indexWriter.close();
            dir.close();
            
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void query() throws IOException, ParseException{
        String field = "title";
        Path path = Paths.get(LuceneUtil.indexDir);
        Directory dir = FSDirectory.open(path);
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        // QueryParser 解析用户输入的工具，可以扫描用户输入的字符串生成Query 对象
        QueryParser parser = new QueryParser(field, analyzer);
        parser.setDefaultOperator(Operator.AND);
        Query query = parser.parse("农村学生");     // 查询关键词
        System.out.println("QueryParser Query: " + query.toString());
        // 返回前10条
        TopDocs tds = searcher.search(query, 10);
        for(ScoreDoc sd: tds.scoreDocs){
            Document doc = searcher.doc(sd.doc);
            System.out.println("DocID: " + sd.doc);     // 文档ID，是Lucene 为索引的每个文档做的标记
            System.out.println("id: " + doc.get("id"));     // 文档内部的id 字段
            System.out.println("title: " + doc.get("title"));
            System.out.println("score: " + sd.score);
        }
        System.out.println("----------------------------");
        
        // MultiFieldQueryParser 多域搜索，可以查询多个字段
        String[] fields = {"title", "content"};
        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(fields, analyzer);
        query = multiFieldQueryParser.parse("美国");
        System.out.println("MultiFieldQueryParser Query: " + query.toString());
        tds = searcher.search(query, 10);
        for(ScoreDoc sd: tds.scoreDocs){
            Document doc = searcher.doc(sd.doc);
            System.out.println("DocID: " + sd.doc);     // 文档ID，是Lucene 为索引的每个文档做的标记
            System.out.println("id: " + doc.get("id"));     // 文档内部的id 字段
            System.out.println("title: " + doc.get("title"));
            System.out.println("content: " + doc.get("content"));
            System.out.println("reply: " + doc.get("reply"));
            System.out.println("score: " + sd.score);
        }
        System.out.println("----------------------------");
        
        // TermQuery 词项搜索
        Term term = new Term("title", "美国");
        query = new TermQuery(term);
        System.out.println("TermQuery Query: " + query.toString());
        tds = searcher.search(query, 10);
        for(ScoreDoc sd: tds.scoreDocs){
            Document doc = searcher.doc(sd.doc);
            System.out.println("DocID: " + sd.doc);     // 文档ID，是Lucene 为索引的每个文档做的标记
            System.out.println("id: " + doc.get("id"));     // 文档内部的id 字段
            System.out.println("title: " + doc.get("title"));
            System.out.println("content: " + doc.get("content"));
            System.out.println("score: " + sd.score);
        }
        System.out.println("----------------------------");
        
        // BooleanQuery 布尔搜索，是一种组合的Query，在使用时可以把各种Query 对象添加进去并标明他们之间的逻辑关系
        // 查询title 字段中包含关键词“美国”而content 字段中不包含关键词“国会”的文档
        Query query1 = new TermQuery(new Term("title", "美国"));
        Query query2 = new TermQuery(new Term("content", "国会"));
        BooleanClause bc1 = new BooleanClause(query1, Occur.MUST);
        BooleanClause bc2 = new BooleanClause(query2, Occur.MUST_NOT);
        query = new BooleanQuery.Builder().add(bc1).add(bc2).build();
        System.out.println("BooleanQuery Query: " + query.toString());
        tds = searcher.search(query, 10);
        for(ScoreDoc sd: tds.scoreDocs){
            Document doc = searcher.doc(sd.doc);
            System.out.println("DocID: " + sd.doc);     // 文档ID，是Lucene 为索引的每个文档做的标记
            System.out.println("id: " + doc.get("id"));     // 文档内部的id 字段
            System.out.println("title: " + doc.get("title"));
            System.out.println("content: " + doc.get("content"));
            System.out.println("score: " + sd.score);
        }
        System.out.println("----------------------------");
        
        // RangeQuery 范围搜索，表示在某范围内的搜索条件，实现一个开始词条到一个结束词条的搜索功能
        query = IntPoint.newRangeQuery("reply", 500, 1000);
        System.out.println("RangeQuery Query: " + query.toString());
        tds = searcher.search(query, 10);
        for(ScoreDoc sd: tds.scoreDocs){
            Document doc = searcher.doc(sd.doc);
            System.out.println("DocID: " + sd.doc);     // 文档ID，是Lucene 为索引的每个文档做的标记
            System.out.println("id: " + doc.get("id"));     // 文档内部的id 字段
            System.out.println("title: " + doc.get("title"));
            System.out.println("content: " + doc.get("content"));
            System.out.println("reply: " + doc.get("reply"));       // 输出null，表明IntPoint 只用来快速过滤，要展示的话，需要另一个字段来存储
            System.out.println("reply_display: " + doc.get("reply_display"));
            System.out.println("score: " + sd.score);
        }
        System.out.println("----------------------------");
        
        // PrefixQuery 前缀搜索，先定义个词条Term，该Term 包含要查找的字段名和关键字前缀，然后构造一个PrefixQuery 对象
        query = new PrefixQuery(new Term("title", "学"));
        System.out.println("PrefixQuery Query: " + query.toString());
        tds = searcher.search(query, 10);
        for(ScoreDoc sd: tds.scoreDocs){
            Document doc = searcher.doc(sd.doc);
            System.out.println("DocID: " + sd.doc);     // 文档ID，是Lucene 为索引的每个文档做的标记
            System.out.println("id: " + doc.get("id"));     // 文档内部的id 字段
            System.out.println("title: " + doc.get("title"));
            System.out.println("score: " + sd.score);
        }
        System.out.println("----------------------------");
        
        // PhraseQuery 多关键字搜索
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term("title", "美国"), 2);
        builder.add(new Term("title", "总统"), 3);
        builder.setSlop(0);     // 设定一个称为“坡度”的变量，来确定关键字之间是否允许或允许多少个无关词汇的存在，值必须大于0，默认0，不允许，其他值允许
        query = builder.build();
        System.out.println("PhraseQuery Query: " + query.toString());
        tds = searcher.search(query, 10);
        for(ScoreDoc sd: tds.scoreDocs){
            Document doc = searcher.doc(sd.doc);
            System.out.println("DocID: " + sd.doc);     // 文档ID，是Lucene 为索引的每个文档做的标记
            System.out.println("id: " + doc.get("id"));     // 文档内部的id 字段
            System.out.println("title: " + doc.get("title"));
            System.out.println("score: " + sd.score);
        }
        System.out.println("----------------------------");
        
        // FuzzyQuery 模糊搜索，可以简单的识别两个相近的词语
        query = new FuzzyQuery(new Term("title", "Tramp"));
        System.out.println("FuzzyQuery Query: " + query.toString());
        tds = searcher.search(query, 10);
        for(ScoreDoc sd: tds.scoreDocs){
            Document doc = searcher.doc(sd.doc);
            System.out.println("DocID: " + sd.doc);     // 文档ID，是Lucene 为索引的每个文档做的标记
            System.out.println("id: " + doc.get("id"));     // 文档内部的id 字段
            System.out.println("title: " + doc.get("title"));
            System.out.println("score: " + sd.score);
        }
        System.out.println("----------------------------");
        
        // WildcardQuery 通配符搜索
        query = new WildcardQuery(new Term("title", "学?"));
        System.out.println("FuzzyQuery Query: " + query.toString());
        tds = searcher.search(query, 10);
        for(ScoreDoc sd: tds.scoreDocs){
            Document doc = searcher.doc(sd.doc);
            System.out.println("DocID: " + sd.doc);     // 文档ID，是Lucene 为索引的每个文档做的标记
            System.out.println("id: " + doc.get("id"));     // 文档内部的id 字段
            System.out.println("title: " + doc.get("title"));
            System.out.println("score: " + sd.score);
        }
        System.out.println("----------------------------");
        
        dir.close();
        reader.close();
    }
    
}
