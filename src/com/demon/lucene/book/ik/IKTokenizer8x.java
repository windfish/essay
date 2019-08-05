package com.demon.lucene.book.ik;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/**
 * Lucene 8.1.1 的API 有变化，需要TokenStream 的实现类为final 修饰的，否则在juint 下会报以下错误：
 * <pre>
 * java.lang.AssertionError: TokenStream implementation classes or at least their incrementToken() implementation must be final
 *   at org.apache.lucene.analysis.TokenStream.assertFinal(TokenStream.java:117)
 * </pre>
 * @author xuliang
 * @since 2019年8月5日 下午3:34:22
 *
 */
public final class IKTokenizer8x extends Tokenizer {
    
    /** IK 分词器实现 */
    private IKSegmenter _IKImpl;
    /** 词元文本属性 */
    private final CharTermAttribute charTermAttribute;
    /** 词元位移属性 */
    private final OffsetAttribute offsetAttribute;
    /** 词元分类属性，参考 org.wltea.analyzer.core.Lexeme 中的分类常量 */
    private final TypeAttribute typeAttribute;
    /** 记录最后一个词元的结束位置 */
    private int endPosition;
    
    public IKTokenizer8x(boolean useSmart) {
        super();
        offsetAttribute = addAttribute(OffsetAttribute.class);
        charTermAttribute = addAttribute(CharTermAttribute.class);
        typeAttribute = addAttribute(TypeAttribute.class);
        _IKImpl = new IKSegmenter(input, useSmart);
    }

    @Override
    public boolean incrementToken() throws IOException {
        // 清除所有的词元属性
        clearAttributes();
        Lexeme nextLexeme = _IKImpl.next();
        if(nextLexeme != null){
            // 将Lexeme 转化为Attributes
            charTermAttribute.append(nextLexeme.getLexemeText());   // 设置词元文本
            charTermAttribute.setLength(nextLexeme.getLength());    // 设置词元长度
            offsetAttribute.setOffset(nextLexeme.getBeginPosition(), nextLexeme.getEndPosition());  // 设置词元位移
            endPosition = nextLexeme.getEndPosition();  // 记录最后分词位置
            typeAttribute.setType(nextLexeme.getLexemeTypeString());    // 记录词元分类
//            System.out.println(nextLexeme.getLexemeType() + "---" + nextLexeme.getLexemeTypeString() + "---" + nextLexeme.getLexemeText() + "---" + typeAttribute.type());
            return true;    // 告知还有下一个词元
        }
        return false;   // 告知词元输出完毕
    }
    
    @Override
    public void reset() throws IOException {
        super.reset();
        _IKImpl.reset(input);
    }
    
    @Override
    public void end() throws IOException {
        int finalOffset = correctOffset(endPosition);
        offsetAttribute.setOffset(finalOffset, finalOffset);
    }

}
