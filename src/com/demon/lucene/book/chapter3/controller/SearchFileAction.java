package com.demon.lucene.book.chapter3.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.demon.lucene.book.chapter3.model.FileModel;
import com.demon.lucene.book.ik.IKAnalyzer8x;

/**
 * 基于Lucene 的文件搜索
 * @author xuliang
 * @since 2019年8月14日 上午10:49:43
 *
 */
@Controller
public class SearchFileAction {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @RequestMapping("/searchFile")
    public ModelAndView search(String query, HttpServletRequest request){
        logger.info("search: " + query);
        String indexPathStr = request.getSession().getServletContext().getRealPath("/indexdir");
        logger.info("index dir: {}", request.getSession().getServletContext().getRealPath("/indexdir"));

        ModelAndView mav = null;
        if(query == null || "".equals(query)){
            logger.info("参数错误");
            mav = new ModelAndView("error");
        }else{
            mav = new ModelAndView("result");
            mav.addObject("fileList", getTopDocs(query, indexPathStr, 10));
        }
        return mav;
    }
    
    private List<FileModel> getTopDocs(String key, String indexPathStr, int N){
        List<FileModel> list = new ArrayList<>();
        // 检索域
        String[] fields = {"title", "content"};
        Path indexPath = Paths.get(indexPathStr);
        Directory dir;
        try {
            dir = FSDirectory.open(indexPath);
            IndexReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new IKAnalyzer8x();
            MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);
            
            // 查询字符串
            Query query = parser.parse(key);
            TopDocs docs = searcher.search(query, N);

            // 定制高亮展示
            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"color:red;\">", "</span>");
            QueryScorer scorerTitle = new QueryScorer(query, fields[0]);
            Highlighter hlTitle = new Highlighter(formatter, scorerTitle);
            QueryScorer scorerContent = new QueryScorer(query, fields[1]);
            Highlighter hlContent = new Highlighter(formatter, scorerContent);
            
            for(ScoreDoc sd: docs.scoreDocs){
                Document doc = searcher.doc(sd.doc);
//                logger.info("title: {}", doc.get(fields[0]));
//                logger.info("content: {}", doc.get(fields[1]));
                // 获取title 高亮的片段
                Fragmenter titleFragmenter = new SimpleSpanFragmenter(scorerTitle);
                hlTitle.setTextFragmenter(titleFragmenter);
                String hl_title = hlTitle.getBestFragment(analyzer, fields[0], doc.get(fields[0]));
                // 获取content 高亮的片段
                Fragmenter contentFragmenter = new SimpleSpanFragmenter(scorerContent);
                hlContent.setTextFragmenter(contentFragmenter);
                String hl_content = hlContent.getBestFragment(analyzer, fields[1], doc.get(fields[1]));
                
                FileModel fm = new FileModel(hl_title != null ? hl_title : doc.get(fields[0]), 
                                    hl_content != null ? hl_content : doc.get(fields[1]));
                list.add(fm);
            }
            dir.close();
            reader.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InvalidTokenOffsetsException e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
}
