package com.demon.lucene.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 * 搜索Lucene 索引
 * @author xuliang
 * @since 2019年7月16日 下午4:33:29
 *
 */
public class SearchFiles {

    public static void main(String[] args) throws IOException, ParseException {
        String index = "/data/lucene/index";  // 索引目录
        String field = "contents";
        String queries = null;
        int repeat = 0;
        boolean raw = false;
        String queryString = null;
        int hitsPerPage = 2;
        
        if(args != null && args.length > 0 &&  args[0] != null){
            queries = args[0];
        }
        
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        
        BufferedReader in = null;
        if(queries != null){
            in = Files.newBufferedReader(Paths.get(queries), StandardCharsets.UTF_8);
        }else{
            in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        }
        
        QueryParser parser = new QueryParser(field, analyzer);
        while(true){
            if(queries == null && queryString == null){
                System.out.println("Enter query: ");
            }
            String line = queryString != null ? queryString : in.readLine();
            if(line == null || line.length() == -1){
                break;
            }
            line = line.trim();
            if(line.length() == 0){
                break;
            }
            
            Query query = parser.parse(line);
            System.out.println("search for: " + line);
            
            if(repeat > 0){
                Date start = new Date();
                for(int i=0;i<repeat;i++){
                    searcher.search(query, 100);
                }
                Date end = new Date();
                System.out.println("Time: "+(end.getTime()-start.getTime())+"ms");
            }
            
            doPagingSearch(in, searcher, query, hitsPerPage, raw, queries == null && queryString == null);
            
            if(queryString != null){
                break;
            }
        }
        reader.close();
    }
    
    /**
     * 分页搜索场景
     */
    public static void doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query, 
                    int hitsPerPage, boolean raw, boolean interactive) throws IOException {
        // 收集足够的文档，以显示5页
        TopDocs results = searcher.search(query, 5 * hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs;
        
        int totalHits = Math.toIntExact(results.totalHits.value);
        System.out.println("总匹配文档：" + totalHits);
        
        int start = 0;
        int end = Math.min(totalHits, hitsPerPage);
        while(true){
            if(end > hits.length){
                System.out.println(hits.length + " / " + totalHits + " total matching docs");
                System.out.println("查找更多（y/n）？");
                String line = in.readLine();
                if(line.length() == 0 || line.charAt(0) == 'n'){
                    continue;
                }
                
                hits = searcher.search(query, totalHits).scoreDocs;
            }
            
            end = Math.min(hits.length, start + hitsPerPage);
            for(int i=start;i<end;i++){
                if(raw){    // 输出原始格式
                    System.out.println("doc="+ hits[i].doc + " score=" + hits[i].score);
                    continue;
                }
                Document doc = searcher.doc(hits[i].doc);
                String path = doc.get("path");
                if(path != null){
                    System.out.println((i+1) + ". " + path);
                    String title = doc.get("title");
                    if(title != null){
                        System.out.println("Title: " + title);
                    }
                }else{
                    System.out.println((i+1) + ". no path for this doc");
                }
            }
            
            if(!interactive || end == 0){
                break;
            }
            if(totalHits >= end){
                boolean quit = false;
                while(true){
                    System.out.print("Press ");
                    if(start - hitsPerPage >= 0){
                        System.out.print("(p)revious page, ");
                    }
                    if(start + hitsPerPage < totalHits){
                        System.out.print("(n)ext page, ");
                    }
                    System.out.println("(q)uit or enter number to jump to a page.");
                    
                    String line = in.readLine();
                    if(line.length() == 0 || line.charAt(0) == 'q'){
                        quit = true;
                        break;
                    }
                    if(line.charAt(0) == 'p'){
                        start = Math.max(0, start - hitsPerPage);
                        break;
                    }else if(line.charAt(0) == 'n'){
                        if(start + hitsPerPage < totalHits){
                            start += hitsPerPage;
                        }
                        break;
                    }else{
                        int page = Integer.parseInt(line);
                        if((page - 1) * hitsPerPage < totalHits){
                            start = (page - 1) * hitsPerPage;
                            break;
                        }else{
                            System.out.println("no such page");
                        }
                    }
                    if(quit){
                        break;
                    }
                    end = Math.min(totalHits, start + hitsPerPage);
                }
            }
        }
    }
    
}
