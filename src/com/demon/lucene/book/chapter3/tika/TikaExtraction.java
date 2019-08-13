package com.demon.lucene.book.chapter3.tika;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

/**
 * 使用Tika 对象提取文档内容
 * 
 * <br>
 * 若出现以下错误，则需升级依赖的 commons-compress 的版本
 * <br>
 * NoSuchMethodError: org.apache.commons.compress.archivers.ArchiveStreamFactory.detect
 * 
 * @author xuliang
 * @since 2019年8月12日 下午2:13:15
 *
 */
public class TikaExtraction {

    public static void main(String[] args) throws IOException, TikaException {
        Tika tika = new Tika();
        File fileDir = new File("/data/lucene/book/chapter3/");
        if(!fileDir.exists()){
            System.out.println("文件夹不存在，请检查！");
            System.exit(0);
        }
        File[] files = fileDir.listFiles();
        String fileContent;
        for(File file: files){
            fileContent = tika.parseToString(file);     // 自动解析
            System.out.println(file.getAbsolutePath());
            System.out.println("Extracted Content: " + fileContent);
        }
    }
    
}
