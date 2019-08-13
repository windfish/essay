package com.demon.lucene.book.chapter2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;

import com.demon.lucene.book.ik.IKAnalyzer8x;
import com.demon.lucene.book.utils.LuceneUtil;

/**
 * 高频词提取
 * 
 * 使用IndexReader 的getTermVector 获取文档的某一个字段的Terms，从terms 中获取tf 词频，拿到词项的tf 后放到map 中降序排序，取出Top-N
 * 
 * @author xuliang
 * @since 2019年8月8日 下午3:54:35
 *
 */
public class HighFrequencyWordsTest {

    private Analyzer analyzer = new IKAnalyzer8x();
    
    @Test
    public void testHighFrequencyWords() throws IOException{
        createIndex();
        getTopTerms();
    }
    
    /**
     * 创建索引
     */
    public void createIndex() throws IOException{
        File file = new File("src/com/demon/lucene/book/chapter2/testfile.txt");
        String text = fileToString(file);
        
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(OpenMode.CREATE);
        Directory dir = FSDirectory.open(Paths.get(LuceneUtil.indexDir));
        IndexWriter indexWriter = new IndexWriter(dir, config);
        
        FieldType type = new FieldType();
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);    // 索引时保存文档、词项频率、位置信息、偏移信息
        type.setStored(true);
        type.setStoreTermVectors(true);     // 存储词项量
        type.setTokenized(true);    // 词条化
        
        Document doc = new Document();
        Field content = new Field("content", text, type);
        doc.add(content);
        
        indexWriter.addDocument(doc);
        indexWriter.close();
        dir.close();
    }
    
    /**
     * 获取热词
     * @throws IOException 
     */
    public void getTopTerms() throws IOException{
        Directory dir = FSDirectory.open(Paths.get(LuceneUtil.indexDir));
        IndexReader reader = DirectoryReader.open(dir);
        
        // 通过getTermVector 获取文档的词项集合，只有一个文档，DocID 为0
        Terms terms = reader.getTermVector(0, "content");
        // 遍历词项
        TermsEnum termsEnum = terms.iterator();
        Map<String, Integer> map = new HashMap<>();
        BytesRef thisTerm;
        while((thisTerm = termsEnum.next()) != null){
            String termText = thisTerm.utf8ToString();  // 词项
            System.out.println("词项：" + termText);
            // totalTermFreq 获取词项频率
            map.put(termText, (int) termsEnum.totalTermFreq());
        }
        // 按频率降序排序
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(map.entrySet());
        Collections.sort(sortedList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        getTopN(sortedList, 10);
    }
    
    public void getTopN(List<Map.Entry<String, Integer>> list, int n){
        System.out.println("高频词排序：");
        for(int i=0;i<n;i++){
            Entry<String, Integer> entry = list.get(i);
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }
    
    public String fileToString(File file){
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str = null;
            while((str = br.readLine()) != null){
                sb.append(str);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    
}
