package com.demon.lucene.book.chapter2;

/**
 * 新闻的实体类
 * 
 * @author xuliang
 * @since 2019年8月6日 下午5:03:05
 *
 */
public class News {

    private int id;
    private String title;
    private String content;
    private int reply;
    
    public News() {
    }

    public News(int id, String title, String content, int reply) {
        super();
        this.id = id;
        this.title = title;
        this.content = content;
        this.reply = reply;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getReply() {
        return reply;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }
    
}
