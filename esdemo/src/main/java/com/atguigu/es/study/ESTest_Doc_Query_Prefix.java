package com.atguigu.es.study;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

/**
 * @author lihuaiqiang
 * @description
 * @date 2022/6/20 15:44
 */
public class ESTest_Doc_Query_Prefix {

    public static void main(String[] args) throws Exception {
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        //12、前缀查询
        SearchRequest request12 = new SearchRequest();
        request12.indices("user");
        SearchSourceBuilder builder12 = new SearchSourceBuilder();
        PrefixQueryBuilder prefixQueryBuilder = QueryBuilders.prefixQuery("id", "10001");
        builder12.query(prefixQueryBuilder);
        request12.source(builder12);
        SearchResponse response12 = esClient.search(request12, RequestOptions.DEFAULT);
        SearchHit[] hits12 = response12.getHits().getHits();
        System.out.println("前缀查询：" + response12.getHits().getTotalHits());
        for (SearchHit hit : hits12) {
            String source = hit.getSourceAsString();
            System.out.println("前缀查询：" + source);
        }

        esClient.close();
    }
}
