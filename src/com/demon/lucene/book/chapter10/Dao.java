package com.demon.lucene.book.chapter10;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;

/**
 * 
 * @author xuliang
 * @since 2019年9月24日 下午2:11:16
 *
 */
public class Dao {

    private Connection conn;
    
    @Test
    public void getConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://test.db.uuuwin.com:4417/es_test";
            String user = "race_dba_s4417";
            String passwd = "s4417.race.uw";
            conn = DriverManager.getConnection(url, user, passwd);
            if(conn != null){
                System.out.println("mysql 连接成功！");
            }else{
                System.out.println("mysql 连接失败！");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void mysqlToES(String indexName) throws IOException{
        String count = "select count(*) from news";
        int totalNum = 0;
        try{
            PreparedStatement pstm = conn.prepareStatement(count);
            ResultSet resultSet = pstm.executeQuery();
            resultSet.next();
            totalNum = resultSet.getInt(1);
            System.out.println(totalNum);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int begin = 0;
        int size = 500;
        while(totalNum > 0 && begin < totalNum){
            String sql = "select * from news limit " + begin + ", " + size;
            RestHighLevelClient client = ESUtils.getClient();
            try {
                PreparedStatement pstm = conn.prepareStatement(sql);
                ResultSet resultSet = pstm.executeQuery();
                Map<String, Object> map = new HashMap<>();
                while(resultSet.next()){
                    int id = resultSet.getInt("id");
                    map.put("id", id);
                    map.put("title", resultSet.getString("title"));
                    map.put("key_word", resultSet.getString("key_word"));
                    map.put("content", resultSet.getString("content"));
                    map.put("url", resultSet.getString("url"));
                    map.put("reply", resultSet.getInt("reply"));
                    map.put("source", resultSet.getString("source"));
                    String postdate = resultSet.getTimestamp("postdate").toString();
                    map.put("postdate", postdate.substring(0, postdate.length() - 2));
                    System.out.println(map);
                    IndexRequest request = new IndexRequest(indexName);
                    request.id(""+id).source(map);
                    IndexResponse mapResp = client.index(request, ESUtils.REQUEST_OPTIONS_DEFAULT);
                    System.out.println("index news, id:" + mapResp.getId() + " result:" + mapResp.getResult());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            begin += size;
        }
    }
    
}
