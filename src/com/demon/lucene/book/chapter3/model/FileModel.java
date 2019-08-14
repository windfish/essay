package com.demon.lucene.book.chapter3.model;

/**
 * 文件对象
 * @author xuliang
 * @since 2019年8月13日 下午2:17:12
 *
 */
public class FileModel {

    private String title;       // 文件标题
    private String content;     // 文件内容
    
    public FileModel() {
    }
    
    public FileModel(String title, String content) {
        super();
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
}
