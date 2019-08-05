package com.demon.lucene.book.chapter2;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * 标准分词器
 * @author xuliang
 * @since 2019年8月5日 下午3:02:10
 *
 */
public class StdAnalyzer {

    private static String strCh = "中华人民共和国简称中国，是一个有13亿人口的国家";
    private static String strEn = "Dogs can not achieve a place, eyes can reach.";
    
    public static void main(String[] args) throws IOException {
        System.out.println("StandardAnalyzer 中文分词：");
        stdAnalyzer(strCh);
        System.out.println("StandardAnalyzer 英文分词：");
        stdAnalyzer(strEn);
    }
    
    public static void stdAnalyzer(String str) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        StringReader reader = new StringReader(str);
        TokenStream tokenStream = analyzer.tokenStream(str, reader);
        tokenStream.reset();
        CharTermAttribute attribute = tokenStream.getAttribute(CharTermAttribute.class);
        System.out.println("分词结果：");
        while(tokenStream.incrementToken()){
            System.out.print(attribute.toString() + "|");
        }
        System.out.println("");
        analyzer.close();
    }
    
}
