package com.demon.lucene.book.utils;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * 
 * @author xuliang
 * @since 2019年8月5日 下午4:57:27
 *
 */
public class LuceneUtil {

    public static String indexDir = "/data/lucene/book/chapter2";
    
    public static void printAnalyzer(Analyzer analyzer, String str) throws IOException{
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
