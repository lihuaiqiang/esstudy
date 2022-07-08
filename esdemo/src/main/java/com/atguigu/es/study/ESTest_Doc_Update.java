package com.atguigu.es.study;

import org.apache.http.HttpHost;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

public class ESTest_Doc_Update {
    public static void main(String[] args) throws Exception {

        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // 文档数据更新
        UpdateRequest request = new UpdateRequest();
        request.index("user").id("1003");

        //全量更新
        /*User user = new User();
        user.setName("张三");
        user.setSex("男");
        user.setAge(29);
        String string = new ObjectMapper().writeValueAsString(user);
        request.doc(string, XContentType.JSON);*/
        //上边的数据 索引相同、id相同，存到数据之后是全量更新。所以需要用下边的这个update执行部分数据的修改

        //部分数据更新
        request.doc(XContentType.JSON, "name", "孙兴慜");
        UpdateResponse response = esClient.update(request, RequestOptions.DEFAULT);

        // 响应状态
        System.out.println("索引操作 ：" + response.getResult());

        esClient.close();
    }
}
