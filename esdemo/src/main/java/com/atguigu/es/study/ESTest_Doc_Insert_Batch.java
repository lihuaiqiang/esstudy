package com.atguigu.es.study;

import com.atguigu.es.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

public class ESTest_Doc_Insert_Batch {
    public static void main(String[] args) throws Exception {

        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // 文档数据批量插入
        BulkRequest request = new BulkRequest();
        IndexRequest request1 = new IndexRequest();
        request1.index("user").id("1001");
        User user = new User();
        user.setName("zahngsan");
        user.setSex("男");
        user.setAge(30);

        IndexRequest request2 = new IndexRequest();
        request2.index("user").id("1002");
        User user2 = new User();
        user2.setName("lisi");
        user2.setSex("女");
        user2.setAge(20);

        //向 ES 中插入数据，必须将数据转换成 JSON 格式
        ObjectMapper mapper = new ObjectMapper();
        String string = mapper.writeValueAsString(user);
        String string2 = mapper.writeValueAsString(user2);
        request1.source(string, XContentType.JSON);
        request2.source(string2, XContentType.JSON);

        request.add(request1).add(request2);
        BulkResponse response = esClient.bulk(request, RequestOptions.DEFAULT);

        // 响应状态
        System.out.println("索引操作 ：" + response.getIngestTook());

        esClient.close();
    }
}
