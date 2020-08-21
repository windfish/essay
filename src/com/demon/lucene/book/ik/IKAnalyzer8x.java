package com.demon.lucene.book.ik;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

/**
 * 
 * @author xuliang
 * @since 2019年8月5日 下午3:57:29
 *
 */
public class IKAnalyzer8x extends Analyzer {

    private boolean useSmart;
    
    public IKAnalyzer8x() {
        this(false);    // 默认细粒度切分算法
    }
    
    public IKAnalyzer8x(boolean useSmart) {
        super();
        this.useSmart = useSmart;
    }
    
    // 重写最新版本的createComponents，构造分词组件
    @Override
    protected TokenStreamComponents createComponents(String arg0) {
        Tokenizer _IKTokenizer = new IKTokenizer8x(this.useSmart);
        return new TokenStreamComponents(_IKTokenizer);
    }

    public boolean isUseSmart() {
        return useSmart;
    }

    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }
    
}
