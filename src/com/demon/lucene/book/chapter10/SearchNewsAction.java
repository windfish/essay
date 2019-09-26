package com.demon.lucene.book.chapter10;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author xuliang
 * @since 2019年9月25日 下午3:54:41
 *
 */
@Controller
@RequestMapping("/news")
public class SearchNewsAction {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @RequestMapping("/SearchNews")
    public ModelAndView search(String query, int pageNum, HttpServletRequest request) throws IOException{
        logger.info("search:{} pageNum:{}", query, pageNum);
        request.setCharacterEncoding("UTF-8");
        
        ModelAndView mav = null;
        if(query == null || "".equals(query)){
            logger.info("参数错误");
            mav = new ModelAndView("news/error");
        }else{
            mav = new ModelAndView("news/result");
            mav.addObject("queryBack", query);
            mav.addObject("currentPage", pageNum);
            searchNews(query, pageNum, mav);
        }
        return mav;
    }
    
    private int pageSize = 10;
    private void searchNews(String query, int pageNum, ModelAndView mav) throws IOException{
        long start = System.currentTimeMillis();
        RestHighLevelClient client = ESUtils.getClient();
        SearchRequest searchRequest = new SearchRequest("news");
        
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field("title"); // 高亮字段
        highlightTitle.highlighterType("unified"); // 高亮类型
        highlightBuilder.field(highlightTitle);
        HighlightBuilder.Field highlightContent = new HighlightBuilder.Field("content");
        highlightBuilder.field(highlightContent);
        searchSourceBuilder.highlighter(highlightBuilder);
        
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(query, "title", "content");
        searchSourceBuilder.query(multiMatchQueryBuilder);
        searchSourceBuilder.from((pageNum-1) * pageSize);
        searchSourceBuilder.size(pageSize);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, ESUtils.REQUEST_OPTIONS_DEFAULT);
        
        SearchHits hits = searchResponse.getHits();
        List<Map<String, Object>> newsList = new ArrayList<>();
        for(SearchHit hit: hits){
            Map<String, Object> news = hit.getSourceAsMap();
            HighlightField hTitle = hit.getHighlightFields().get("title");
            if(hTitle != null){
                Text[] fragments = hTitle.fragments();
                String hTitleStr = "";
                for(Text text: fragments){
                    hTitleStr += text;
                }
                news.put("title", hTitleStr);
            }
            HighlightField hContent = hit.getHighlightFields().get("content");
            if(hContent != null){
                Text[] fragments = hContent.fragments();
                String hContentStr = "";
                for(Text text: fragments){
                    hContentStr += text;
                }
                news.put("content", hContentStr);
            }
            newsList.add(news);
        }
        long end = System.currentTimeMillis();
        
        mav.addObject("newslist", newsList);
        mav.addObject("totalHits", hits.getTotalHits().value + "");
        mav.addObject("totalTime", (end - start) + "");
        logger.info("totalHits:{}", hits.getTotalHits().value);
    }
    
}
