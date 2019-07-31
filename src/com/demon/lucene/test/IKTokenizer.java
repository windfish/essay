package com.demon.lucene.test;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/**
 * <pre>
 * 使用IK 分词器，需要修改IKTokenizer 以兼容lucene 版本
 * Lucene 8.1.1 的API 有变化，需要TokenStream 的实现类为final 修饰的，否则在juint 下会报以下错误：
 * 
 * java.lang.AssertionError: TokenStream implementation classes or at least their incrementToken() implementation must be final
    at org.apache.lucene.analysis.TokenStream.assertFinal(TokenStream.java:117)
 * 
 * </pre>
 * 
 * @author xuliang
 * @since 2019年7月18日 下午5:31:03
 * 
 */
public final class IKTokenizer extends Tokenizer {

    /** IK 分词器实现 */
    private IKSegmenter ikSegmenter;
    /** 词元文本属性 */
    private final CharTermAttribute attribute;
    /** 词元位移属性 */
    private final OffsetAttribute offsetAttribute;
    /** 词元分类属性 */
    private final TypeAttribute typeAttribute;
    
    /** 记录最后一个词的结束位置 */
    private int endPosition;
    
    public IKTokenizer(boolean useSmart) {
        this.attribute = addAttribute(CharTermAttribute.class);
        this.offsetAttribute = addAttribute(OffsetAttribute.class);
        this.typeAttribute = addAttribute(TypeAttribute.class);
        ikSegmenter = new IKSegmenter(input, useSmart);
    }

    @Override
    public boolean incrementToken() throws IOException {
        // 清除所有的词元属性
        clearAttributes();
        Lexeme next = ikSegmenter.next();
        if(next != null){
            // 将Lexeme 转化为 Attribute
            attribute.append(next.getLexemeText());     // 设置词元文本
            attribute.setLength(next.getLength());      // 设置词元长度
            offsetAttribute.setOffset(next.getBeginPosition(), next.getEndPosition());      // 设置词元位移
            typeAttribute.setType(next.getLexemeTypeString());
            typeAttribute.setType(next.getLexemeText());
            endPosition = next.getEndPosition();
            return true;    // 表示还有下一个词元
        }
        return false;
    }
    
    @Override
    public void reset() throws IOException {
        super.reset();
        ikSegmenter.reset(input);
    }
    
    @Override
    public void end() throws IOException {
        int i = correctOffset(this.endPosition);
        offsetAttribute.setOffset(i, i);
    }
    
}
