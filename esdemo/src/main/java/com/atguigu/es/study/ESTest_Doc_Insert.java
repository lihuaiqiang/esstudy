package com.atguigu.es.study;

import com.atguigu.es.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

public class ESTest_Doc_Insert {
    public static void main(String[] args) throws Exception {

        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // 文档数据插入
        IndexRequest request = new IndexRequest();
        request.index("user").id("1007");
        User user = new User();
        user.setName("贾德松");
        user.setSex("男");
        user.setAge(30);

        //向 ES 中插入数据，必须将数据转换成 JSON 格式
        ObjectMapper mapper = new ObjectMapper();
        String string = mapper.writeValueAsString(user);
        request.source(string, XContentType.JSON);

        IndexResponse response = esClient.index(request, RequestOptions.DEFAULT);

        // 响应状态
        System.out.println("索引操作 ：" + response.getResult());

        esClient.close();
    }
}
