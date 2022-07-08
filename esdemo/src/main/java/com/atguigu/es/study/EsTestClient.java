package com.atguigu.es.study;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author lihuaiqiang
 * @description
 * @date 2022/6/15 17:47
 */
public class EsTestClient {

    public static void main(String[] args) throws Exception {
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );
        restHighLevelClient.close();
    }
}
