package com.demon.lucene.test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

/**
 * 
 * @author xuliang
 * @since 2019年7月31日 下午2:28:14
 *
 */
public class TestQuery {

    private String indexPath = "/data/lucene/test/";
    
    @Test
    public void testQuery() throws IOException, ParseException{
        String field = "title";
        String field2 = "content";
        Path path = Paths.get(indexPath);
        Directory dir = FSDirectory.open(path);
        // 获取索引流
        IndexReader indexReader = DirectoryReader.open(dir);
        // 构建IndexSearcher 对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Analyzer analyzer = new IKAnalyzer();
        // 查询解析器，QueryParser 搜索单个字段，MultiFieldQueryParser 搜索多个字段
        QueryParser parser = new QueryParser(field, analyzer);
        // Operator.AND 是查询解析器的操作，
        // 例如：输入关键词“农村学生”，会被分为两个词“农村”和“学生”，默认情况下会找到包含“农村”或“学生”的文档，现在的情况是找到同时包含“农村”“学生”的文档
        parser.setDefaultOperator(QueryParser.Operator.AND);
        
        Query query = parser.parse("农村学生");
        System.out.println("Query: " + query.toString());
        
        TopDocs docs = indexSearcher.search(query, 10);
        for(ScoreDoc sd: docs.scoreDocs){
            Document doc = indexSearcher.doc(sd.doc);
            System.out.println("Doc ID: " + sd.doc);
            System.out.println("id: " + doc.get("id"));
            System.out.println("title: " + doc.get("title"));
            System.out.println("content: " + doc.get("content"));
            System.out.println("doc score: " + sd.score);
            System.out.println("-------------------------------------");
        }
        System.out.println("+++++++++++++++++++++++++++++++++++++");
        
        MultiFieldQueryParser parser2 = new MultiFieldQueryParser(new String[]{field, field2}, analyzer);
        query = parser2.parse("农村学生");
        System.out.println("MultiFieldQuery: " + query.toString());
        
        TopDocs docs2 = indexSearcher.search(query, 10);
        for(ScoreDoc sd: docs2.scoreDocs){
            Document doc = indexSearcher.doc(sd.doc);
            System.out.println("Doc ID: " + sd.doc);
            System.out.println("id: " + doc.get("id"));
            System.out.println("title: " + doc.get("title"));
            System.out.println("content: " + doc.get("content"));
            System.out.println("doc score: " + sd.score);
            System.out.println("-------------------------------------");
        }
        
        indexReader.close();
        dir.close();
    }
    
}
