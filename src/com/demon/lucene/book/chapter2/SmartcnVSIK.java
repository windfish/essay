package com.demon.lucene.book.chapter2;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import com.demon.lucene.book.ik.IKAnalyzer8x;

/**
 * 中文分词器对比
 * @author xuliang
 * @since 2019年8月5日 下午4:03:05
 *
 */
public class SmartcnVSIK {

    private String str1 = "公路局正在治理解放大道路面积水问题。";
    private String str2 = "IKAnalyzer 是一个开源的，基于Java 语言开发的轻量级的中文分词工具包。";
    
    @Test
    public void test() throws IOException{
        Analyzer analyzer = null;
        System.out.println("句子一：" + str1);
        System.out.println("SmartChineseAnalyzer 分词结果：");
        analyzer = new SmartChineseAnalyzer();
        printAnalyzer(analyzer, str1);
        System.out.println("IKAnalyzer 分词结果：");
        analyzer = new IKAnalyzer8x(true);
        printAnalyzer(analyzer, str1);
        System.out.println("-------------------------------");
        System.out.println("句子二：" + str2);
        System.out.println("SmartChineseAnalyzer 分词结果：");
        analyzer = new SmartChineseAnalyzer();
        printAnalyzer(analyzer, str2);
        System.out.println("IKAnalyzer 分词结果：");
        analyzer = new IKAnalyzer8x(true);
        printAnalyzer(analyzer, str2);
    }
    
    public void printAnalyzer(Analyzer analyzer, String str) throws IOException{
        StringReader reader = new StringReader(str);
        TokenStream tokenStream = analyzer.tokenStream(str, reader);
        tokenStream.reset();
        CharTermAttribute attribute = tokenStream.getAttribute(CharTermAttribute.class);
        while(tokenStream.incrementToken()){
            System.out.print(attribute.toString() + "|");
        }
        System.out.println("");
        analyzer.close();
    }
    
}
