package com.demon.lucene.book.chapter10;

import java.io.IOException;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.Test;

/**
 * 
 * @author xuliang
 * @since 2019年9月24日 下午2:44:23
 *
 */
public class DataToES {

    private String indexName = "news";
    
    @Test
    public void createIndex() throws IOException {
        // 2. 设置mapping，创建索引
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("id");
                {
                    builder.field("type", "long");
                }
                builder.endObject();
                builder.startObject("title");
                {
                    builder.field("type", "text");
                    builder.field("analyzer", "ik_max_word");
                    builder.field("search_analyzer", "ik_max_word");
                }
                builder.endObject();
                builder.startObject("key_word");
                {
                    builder.field("type", "text");
                    builder.field("analyzer", "ik_max_word");
                    builder.field("search_analyzer", "ik_max_word");
                }
                builder.endObject();
                builder.startObject("content");
                {
                    builder.field("type", "text");
                    builder.field("analyzer", "ik_max_word");
                    builder.field("search_analyzer", "ik_max_word");
                }
                builder.endObject();
                builder.startObject("url");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("reply");
                {
                    builder.field("type", "long");
                }
                builder.endObject();
                builder.startObject("source");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("postdate");
                {
                    builder.field("type", "date");
                    builder.field("format", "yyyy-MM-dd HH:mm:ss");
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        ESUtils.createIndex(indexName, 3, 0, builder);
    }
    
    @Test
    public void dataToES() throws IOException{
        Dao dao = new Dao();
        dao.getConnection();
        dao.mysqlToES(indexName);
    }
    
}
