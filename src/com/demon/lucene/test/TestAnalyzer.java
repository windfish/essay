package com.demon.lucene.test;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * lucene 分词器
 * @author xuliang
 * @since 2019年7月18日 下午3:55:02
 *
 */
public class TestAnalyzer {

    private static final String str = "火箭雷霆正式达成交易！威少哈登正式联手。火箭队在宣布完成威少交易的同时还晒出了威少身穿火箭队0号球衣的照片，并欢迎他的加盟。";
    
    public static void main(String[] args) throws IOException {
        // 标准分词器
        Analyzer analyzer = new StandardAnalyzer();
        System.out.println("标准分词： " + analyzer.getClass());
        logAnalyzer(analyzer);
        
        // 空格分词器
        analyzer = new WhitespaceAnalyzer();
        System.out.println("空格分词： " + analyzer.getClass());
        logAnalyzer(analyzer);
        
        // 简单分词器
        analyzer = new SimpleAnalyzer();
        System.out.println("简单分词： " + analyzer.getClass());
        logAnalyzer(analyzer);
        
        // 二分法分词器
        analyzer = new CJKAnalyzer();
        System.out.println("二分法分词： " + analyzer.getClass());
        logAnalyzer(analyzer);
        
        // 关键词分词器
        analyzer = new KeywordAnalyzer();
        System.out.println("关键词分词： " + analyzer.getClass());
        logAnalyzer(analyzer);
        
        // 停用词分词器
        analyzer = new StopAnalyzer(StopFilter.makeStopSet(new String[]{}, true));
        System.out.println("停用词分词： " + analyzer.getClass());
        logAnalyzer(analyzer);
    }
    
    private static void logAnalyzer(Analyzer analyzer) throws IOException{
        // 获取Reader
        StringReader reader = new StringReader(str);
        // 获取TokenStream
        TokenStream stream = analyzer.tokenStream(str, reader);
        // 刷新流
        stream.reset();
        // 获取字符词项
        CharTermAttribute attribute = stream.getAttribute(CharTermAttribute.class);
        // 遍历输出
        while(stream.incrementToken()){
            System.out.print(attribute.toString() + "|");
        }
        System.out.println("");
        System.out.println("--------------------------------------");
        analyzer.close();
    }
    
}
