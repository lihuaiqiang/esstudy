package com.atguigu.es.study;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
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
public class ESTest_Doc_Query_Highlight {

    public static void main(String[] args) throws Exception {
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );


        //9、高亮查询
        SearchRequest request9 = new SearchRequest();
        request9.indices("user");
        SearchSourceBuilder builder9 = new SearchSourceBuilder();
        FuzzyQueryBuilder fuzzyQueryBuilder9 = QueryBuilders.fuzzyQuery("name", "孙");
        TermQueryBuilder termQueryBuilder1 = QueryBuilders.termQuery("name", "zhangsan");

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.field("name");

        builder9.highlighter(highlightBuilder);
        builder9.query(fuzzyQueryBuilder9);

        request9.source(builder9);
        SearchResponse response9 = esClient.search(request9, RequestOptions.DEFAULT);

        SearchHit[] hits9 = response9.getHits().getHits();

        for (SearchHit hit : hits9) {
            String source = hit.getSourceAsString();
            System.out.println("模糊查询：" + source);
        }
        esClient.close();
    }
}
