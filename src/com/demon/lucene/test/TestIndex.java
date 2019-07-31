package com.demon.lucene.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

/**
 * 
 * @author xuliang
 * @since 2019年7月23日 下午4:36:57
 *
 */
public class TestIndex {

    private String indexPath = "/data/lucene/test/";
    
    FieldType idType = new FieldType();
    FieldType titleType = new FieldType();
    FieldType contentType = new FieldType();
    {
        // 创建FieldType，指定字段的索引信息
        // 设置新闻id 索引文档，并存储
        idType.setIndexOptions(IndexOptions.DOCS);
        idType.setStored(true);
        
        // 设置新闻标题索引文档、频率、位移信息、偏移量，并存储
        titleType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        titleType.setStored(true);
        titleType.setTokenized(true);
        
        // 设置内容索引
        contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        contentType.setStored(true);
        contentType.setTokenized(true);
        contentType.setStoreTermVectors(true);
        contentType.setStoreTermVectorOffsets(true);
        contentType.setStoreTermVectorPayloads(true);
        contentType.setStoreTermVectorPositions(true);
    }
    
    @Test
    public void createIndex() throws IOException {
        News news1 = new News();
        news1.setId(1);
        news1.setTitle("习近平会见美国总统奥巴马");
        news1.setContent("国家主席习近平9月3日在杭州西湖国宾馆会见前米出席二十国集团领导人杭州峰会的美国总统奥巴马。");
        news1.setReply(865);
        
        News news2 = new News();
        news2.setId(2);
        news2.setTitle("北大迎来4380名学生，农村学生700多人近年最多");
        news2.setContent("昨天，北京大学迎来4380名来自全国各地及数十个国家的本科学生。其中，农村学生共700余名，为近年最多。");
        news2.setReply(3652);
        
        News news3 = new News();
        news3.setId(3);
        news3.setTitle("特朗普宣誓(Donald Trump)就凭美国第45任总统");
        news3.setContent("当地时间1月20日，唐纳德·特朗普在美国国会宣誓就职，正式成为美国第45任总统。");
        news3.setReply(23);
        
        News news4 = new News();
        news4.setId(4);
        news4.setTitle("清华大学迎来4560名学生");
        news4.setContent("昨天，清华大学迎来4560名来自全国各地及数十个国家的本科学生。");
        news4.setReply(1256);
        
        // 实例化分词器
        Analyzer analyzer = new IKAnalyzer(true);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        // 设置打开方式，OpenMode.CREATE 代表创建索引时，不管是否已存在，都替换。
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        
        // 获取路径
        Directory dir = null;
        Path path = Paths.get(indexPath);
        if(!Files.isReadable(path)){
            System.out.println("dir " + path.toAbsolutePath() + " not exists or can not read.");
            System.exit(1);
        }
        dir = FSDirectory.open(path);
        
        // 实例化IndexWriter
        IndexWriter indexWriter = new IndexWriter(dir, config);
        
        // 构建Document 对象
        Document doc1 = new Document();
        doc1.add(new Field("id", String.valueOf(news1.getId()), idType));
        doc1.add(new Field("title", news1.getTitle(), titleType));
        doc1.add(new Field("content", news1.getContent(), contentType));
        doc1.add(new IntPoint("reply", news1.getReply()));
        doc1.add(new StoredField("reply_display", news1.getReply()));
        
        Document doc2 = new Document();
        doc2.add(new Field("id", String.valueOf(news2.getId()), idType));
        doc2.add(new Field("title", news2.getTitle(), titleType));
        doc2.add(new Field("content", news2.getContent(), contentType));
        doc2.add(new IntPoint("reply", news2.getReply()));
        doc2.add(new StoredField("reply_display", news2.getReply()));
        
        Document doc3 = new Document();
        doc3.add(new Field("id", String.valueOf(news3.getId()), idType));
        doc3.add(new Field("title", news3.getTitle(), titleType));
        doc3.add(new Field("content", news3.getContent(), contentType));
        doc3.add(new IntPoint("reply", news3.getReply()));
        doc3.add(new StoredField("reply_display", news3.getReply()));
        
        Document doc4 = new Document();
        doc4.add(new Field("id", String.valueOf(news4.getId()), idType));
        doc4.add(new Field("title", news4.getTitle(), titleType));
        doc4.add(new Field("content", news4.getContent(), contentType));
        doc4.add(new IntPoint("reply", news4.getReply()));
        doc4.add(new StoredField("reply_display", news4.getReply()));
        
        // 生成索引
        indexWriter.addDocument(doc1);
        indexWriter.addDocument(doc2);
        indexWriter.addDocument(doc3);
        indexWriter.addDocument(doc4);
        
        indexWriter.commit();
        indexWriter.close();
        dir.close();
        
    }
    
    @Test
    public void crudIndex(){
        deleteDoc("title", "美国");
        update(new News(2, "北京大学开学迎来4380名新生", "昨天，北京大学迎来4380名来自全国各地及数十个国家的本科新生。其中，农村学生共700余名，为近年最少", 2312));
    }
    
    /**
     * 根据字段和关键字删除索引
     * @param field 字段
     * @param text 关键字
     */
    private void deleteDoc(String field, String text){
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        Path path = Paths.get(indexPath);
        Directory directory;
        try {
            directory = FSDirectory.open(path);
            IndexWriter indexWriter = new IndexWriter(directory, config);
            
            indexWriter.deleteDocuments(new Term(field, text));
            indexWriter.commit();
            indexWriter.close();
            directory.close();
            System.out.println("删除完成！field：" + field + " text：" + text);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void update(News news){
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        Path path = Paths.get(indexPath);
        Directory directory;
        try {
            directory = FSDirectory.open(path);
            IndexWriter indexWriter = new IndexWriter(directory, config);
            
            Document doc = new Document();
            doc.add(new Field("id", String.valueOf(news.getId()), idType));
            doc.add(new Field("title", news.getTitle(), titleType));
            doc.add(new Field("content", news.getContent(), contentType));
            indexWriter.updateDocument(new Term("title", "北大"), doc);
            indexWriter.commit();
            indexWriter.close();
            directory.close();
            System.out.println("更新完成！");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
