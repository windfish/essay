package com.demon.lucene.book.chapter3.tika;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Tika 提取PDF 文件内容
 * @author xuliang
 * @since 2019年8月9日 下午3:26:29
 *
 */
public class TikaParsePdf {

    @Test
    public void pdfParse() throws IOException, SAXException, TikaException{
        String filePath = "/data/lucene/book/chapter3/java开发工程师-杨鑫祎-拉勾招聘.pdf";
        
        // 创建内容处理器对象，BodyContentHandler 用于提取文件的文本内容
        BodyContentHandler handler = new BodyContentHandler();
        // 创建元数据对象，Metadata 获取文件的属性
        Metadata metadata = new Metadata();
        // 读入文件
        // FileInputStream 不支持随机访问读取文件，TikaInputStream 可以更高效的处理文件
//        File file = new File(filePath);        
//        FileInputStream inputStream = new FileInputStream(file);
        TikaInputStream tikaInputStream = TikaInputStream.get(Paths.get(filePath));
        // 创建内容解析器对象
        ParseContext parseContext = new ParseContext();
        // 实例化PDFParser 对象
        PDFParser parser = new PDFParser();
        // 解析文件
        parser.parse(tikaInputStream, handler, metadata, parseContext);
        // 遍历文件属性
        System.out.println("文件属性信息：");
        for(String name: metadata.names()){
            System.out.println(name + ":" + metadata.get(name));
        }
        // 打印pdf 文件中的内容
        System.out.println("pdf 文件内容：");
        System.out.println(handler.toString());
    }
    
}
