package com.demon.lucene.book.chapter3.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import com.demon.lucene.book.chapter3.model.FileModel;
import com.demon.lucene.book.ik.IKAnalyzer8x;

/**
 * 创建索引
 * @author xuliang
 * @since 2019年8月13日 下午2:19:51
 *
 */
public class CreateIndex {

    /**
     * 列出 WebContent/files 下所有文件
     */
    public static List<FileModel> extractFile() {
        List<FileModel> list = new ArrayList<>();
        File fileDir = new File("WebContent/files");
        File[] allFiles = fileDir.listFiles();
        for(File f: allFiles){
            FileModel fm = new FileModel(f.getName(), parserExtraction(f));
            list.add(fm);
        }
        return list;
    }
    
    /**
     * 使用 Tika 提取文档内容
     */
    public static String parserExtraction(File file){
        String fileContent = "";    // 文档内容
        BodyContentHandler handler = new BodyContentHandler();
        Parser parser = new AutoDetectParser();     // 自动解析器接口
        Metadata metadata = new Metadata();
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            ParseContext context = new ParseContext();
            parser.parse(inputStream, handler, metadata, context);
            fileContent = handler.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        }
        return fileContent;
    }
    
    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new IKAnalyzer8x();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(OpenMode.CREATE);
        
        Path indexPath = Paths.get("WebContent/indexdir");
        if(!Files.isReadable(indexPath)){
            System.out.println(indexPath.toAbsolutePath() + " 不存在或不可读，请检查！");
            System.exit(0);
        }
        
        FieldType fieldType = new FieldType();
        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        fieldType.setStored(true);
        fieldType.setTokenized(true);
        fieldType.setStoreTermVectors(true);
        fieldType.setStoreTermVectorPositions(true);
        fieldType.setStoreTermVectorOffsets(true);
        
        Directory dir = null;
        IndexWriter indexWriter = null;
        Date start = new Date();
        dir = FSDirectory.open(indexPath);
        indexWriter = new IndexWriter(dir, config);
        List<FileModel> fileList = extractFile();
        // 遍历fileList，添加索引
        for(FileModel f: fileList){
            Document doc = new Document();
            doc.add(new Field("title", f.getTitle(), fieldType));
            doc.add(new Field("content", f.getContent(), fieldType));
            indexWriter.addDocument(doc);
        }
        indexWriter.commit();
        indexWriter.close();
        dir.close();
        Date end = new Date();
        System.out.println("索引文档完成，共耗时：" + (end.getTime() - start.getTime()) + " 毫秒。");
    }
    
}
