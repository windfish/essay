package com.demon.lucene.book.chapter2;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;

import com.demon.lucene.book.ik.IKAnalyzer8x;
import com.demon.lucene.book.utils.PrintUtil;

/**
 * 自定义词典测试
 * @author xuliang
 * @since 2019年8月5日 下午4:58:06
 *
 */
public class ExtDicTest {

    private static String str = "纪录片《厉害了我的国》，没有哗众取宠的炒作，没有商业喧哗的吵闹，却深受观众的喜爱和欢迎。";
    
    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new IKAnalyzer8x(true);
        PrintUtil.printAnalyzer(analyzer, str);
    }
    
}
