package com.demon.lucene.book.chapter1;

import java.util.Arrays;
import java.util.List;

/**
 * 依次计算词项频率tf、文档频率df、逆文档频率idf、词频-逆文档频率tf-idf
 * 
 * @author xuliang
 * @since 2019年8月1日 下午2:41:03
 *
 */
public class TfIdfCalc {

    /**
     * 计算词项频率tf，tf = 单词在文档中出现的次数 / 文档的总次数
     * @param doc 文档
     * @param term 词项
     */
    public double tf(List<String> doc, String term){
        double termFrequency = 0;
        for(String word: doc){
            if(word.equalsIgnoreCase(term)){
                termFrequency++;
            }
        }
        return termFrequency / doc.size();
    }
    
    /**
     * 计算文档频率df，表示文档集中包含某个词的所有文档数目
     * @param docs 文档集
     * @param term 词项
     */
    public int df(List<List<String>> docs, String term){
        int n = 0;
        if(term != null && !"".equals(term)){
            for(List<String> doc: docs){
                for(String word: doc){
                    if(word.equalsIgnoreCase(term)){
                        n++;
                        break;
                    }
                }
            }
        }else{
            System.out.println("term 不能为null 或空串");
        }
        return n;
    }
    
    /**
     * 计算逆文档频率idf，idf = log(文档集总文档数  / (包含某个词的文档数 + 1))，+1是为了防止所有文档都不包含某个词而导致分母为0的情况
     * @param docs 文档集
     * @param term 词项
     */
    public double idf(List<List<String>> docs, String term){
        return Math.log(docs.size() / (double)(df(docs, term) + 1));
    }
    
    /**
     * 词频-逆文档频率tf-idf，tf-idf = 词频tf * 逆文档频率idf
     * @param doc 文档
     * @param docs 文档集
     * @param term 词项
     */
    public double tfidf(List<String> doc, List<List<String>> docs, String term){
        return tf(doc, term) * idf(docs, term);
    }
    
    public static void main(String[] args) {
        List<String> doc1 = Arrays.asList("人工", "智能", "成为", "互联网", "大会", "焦点");
        List<String> doc2 = Arrays.asList("谷歌", "推出", "开源", "人工", "智能", "系统", "工具");
        List<String> doc3 = Arrays.asList("互联网", "的", "未来", "在", "人工", "智能");
        List<String> doc4 = Arrays.asList("谷歌", "开源", "机器", "学习", "工具");
        
        List<List<String>> docs = Arrays.asList(doc1, doc2, doc3, doc4);
        
        String term = "谷歌";
        
        TfIdfCalc calc = new TfIdfCalc();
        System.out.println(calc.tf(doc2, term));
        System.out.println(calc.df(docs, term));
        System.out.println("TF-IDF("+term+")： " + calc.tfidf(doc2, docs, term));
    }
    
}
