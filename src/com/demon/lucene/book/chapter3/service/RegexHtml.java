package com.demon.lucene.book.chapter3.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author xuliang
 * @since 2019年8月14日 下午1:58:05
 *
 */
public class RegexHtml {

    /**
     * 去除字符串中的HTML 标签
     */
    public String delHtmlTag(String line) {
        String regex_html = "<[^>]+>";
        Pattern r = Pattern.compile(regex_html);
        Matcher m = r.matcher(line);
        line = m.replaceAll("");
        return line;
    }
    
    public static void main(String[] args) {
        System.out.println(new RegexHtml().delHtmlTag("易宝<span>对外</span>接口.doc"));
    }
    
}
