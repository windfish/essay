package com.demon.lucene.test;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

/**
 * 使用IK 分词器，需要修改IKAnalyzer 以兼容lucene 版本
 * @author xuliang
 * @since 2019年7月22日 下午4:35:58
 *
 */
public class IKAnalyzer extends Analyzer {

    private boolean useSmart;

    public boolean isUseSmart() {
        return useSmart;
    }

    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }
    
    public IKAnalyzer() {
        this(true);     // 默认细粒度切分算法
    }
    
    public IKAnalyzer(boolean useSmart) {
        super();
        this.useSmart = useSmart;
    }
    
    @Override
    protected TokenStreamComponents createComponents(String s) {
        Tokenizer tokenizer = new IKTokenizer(this.useSmart);
        return new TokenStreamComponents(tokenizer);
    }
    
}
