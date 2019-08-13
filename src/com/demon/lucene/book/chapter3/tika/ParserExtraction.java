package com.demon.lucene.book.chapter3.tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

/**
 * 使用Parser 接口提取文档内容
 * @author xuliang
 * @since 2019年8月12日 下午3:01:31
 *
 */
public class ParserExtraction {

    public static void main(String[] args) throws IOException, SAXException, TikaException {
        File fileDir = new File("/data/lucene/book/chapter3/");
        if(!fileDir.exists()){
            System.out.println("文件夹不存在，请检查！");
            System.exit(0);
        }
        File[] files = fileDir.listFiles();
        // 创建内容处理器对象
        BodyContentHandler handler = new BodyContentHandler();
        // 创建元数据对象
        Metadata metadata = new Metadata();
        FileInputStream fileInputStream = null;
        // 自动检测分词器
        Parser parser = new AutoDetectParser();
        ParseContext context = new ParseContext();
        for(File f: files){
            fileInputStream = new FileInputStream(f);
            parser.parse(fileInputStream, handler, metadata, context);
            System.out.println(f.getName() + ":\n" + handler.toString());
        }
    }
    
}
