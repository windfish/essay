package com.demon.lucene.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * 创建Lucene 索引
 * @author xuliang
 * @since 2019年7月16日 下午2:00:16
 *
 */
public class IndexFiles {

    public static void main(String[] args) {
        String indexPath = "/data/lucene/index";  // 索引目录
        String docsPath = "/data/lucene/data";   // 文件目录
        boolean create = true;
        
        final Path docDir = Paths.get(docsPath);
        if(!Files.isReadable(docDir)){
            System.out.println("document directory " + docDir.toAbsolutePath() + " does not exist or not readable.");
            System.exit(1);
        }
        
        Date start = new Date();
        try{
            System.out.println("Indexing directory " + indexPath);
            
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            
            if(create){
                // 新建索引文件，删除任何索引
                config.setOpenMode(OpenMode.CREATE);
            }else{
                // 将新文档添加到现有索引中
                config.setOpenMode(OpenMode.CREATE_OR_APPEND);
            }
            
            // 可以增加RAM 缓冲区，增加索引性能
            // config.setRAMBufferSizeMB(256.0);
            
            // 
            IndexWriter writer = new IndexWriter(dir, config);
            indexDocs(writer, docDir);
            
            writer.close();
            
            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " mills");
            
        }catch (IOException e) {
            System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }
    }
    
    /**
     * 索引给定文件，若给定的是目录，则递归其下的目录和文件
     * @param writer 索引创建器
     * @param path 目录或文件
     * @throws IOException
     */
    static void indexDocs(final IndexWriter writer, Path path) throws IOException{
        if(Files.isDirectory(path)){
            Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try{
                        indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                    }catch (IOException e) {
                        System.out.println("无法索引文件：" + file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }else{
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
        }
    }
    
    /**
     * 索引单个文件
     * @param writer 索引创建器
     * @param file 文件
     * @param lastModified 最后修改时间
     */
    static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException{
        try(InputStream stream = Files.newInputStream(file)){
            // 构造一个空的文档
            Document doc = new Document();
            
            // 将文件路径构造名为“path”的Field，添加进文档中
            Field pathField = new StringField("path", file.toString(), Field.Store.YES);
            doc.add(pathField);
            
            // 将文件的最后修改时间，添加进文档中
            Field modifiedField = new LongPoint("modified", lastModified);
            doc.add(modifiedField);
            
            // 将文件内容添加到文档中。指定一个阅读器Reader，以便文件的文本被标记化并编入索引，但不存储
            Field textField = new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
            doc.add(textField);
            
            if(writer.getConfig().getOpenMode() == OpenMode.CREATE){
                // 新索引
                System.out.println("添加文档：" + file);
                writer.addDocument(doc);
            }else{
                // 现有索引，根据路径来精确更新文档
                System.out.println("更新文档：" + file);
                writer.updateDocument(new Term("path", file.toString()), doc);
            }
        }
    }
    
}
