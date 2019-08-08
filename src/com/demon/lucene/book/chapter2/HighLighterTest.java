package com.demon.lucene.book.chapter2;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.demon.lucene.book.ik.IKAnalyzer8x;
import com.demon.lucene.book.utils.LuceneUtil;

/**
 * Lucene 高亮搜索
 * 
 * @author xuliang
 * @since 2019年8月8日 下午1:40:24
 *
 */
public class HighLighterTest {

    public static void main(String[] args) throws IOException, ParseException, InvalidTokenOffsetsException {
        String field = "content";
        String text = "美国";
        Path path = Paths.get(LuceneUtil.indexDir);
        Directory dir = FSDirectory.open(path);
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new IKAnalyzer8x();
        QueryParser parser = new QueryParser(field, analyzer);
        Query query = parser.parse(text);
        System.out.println("Query: " + query.toString());
        
        QueryScorer scorer = new QueryScorer(query);
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"color:red;\">", "</span>");
        Highlighter highlighter = new Highlighter(formatter, scorer);
        
        // 高亮分词器
        TopDocs tds = searcher.search(query, 10);
        for(ScoreDoc sd: tds.scoreDocs){
            Document doc = searcher.doc(sd.doc);
            System.out.println("id: " + doc.get("id"));
            System.out.println("title: " + doc.get("title"));
            System.out.println("content: " + doc.get("content"));
            Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
            highlighter.setTextFragmenter(fragmenter);
            String str = highlighter.getBestFragment(analyzer, field, doc.get(field));  // 获取高亮的片段
            System.out.println("高亮的片段： " + str);
            System.out.println("------------------------------");
        }
        
        dir.close();
        reader.close();
    }
    
}
