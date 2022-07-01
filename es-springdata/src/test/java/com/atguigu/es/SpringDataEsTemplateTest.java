package com.atguigu.es;

import com.atguigu.es.bean.Product;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lihuaiqiang
 * @description
 * @date 2022/6/30 10:56
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringDataEsTemplateTest {

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    /**
     * 数据保存
     */
    @Test
    public void saveTest() {
        Product product = new Product();
        product.setId(1002L);
        product.setTitle("比亚迪-汉");
        product.setCategory("新能源");
        product.setPrice(150.88D);
        product.setImages("http://baidu.com/1.png");
        //这个方式也可以实现保存
        esTemplate.save(product);

        Product product2 = new Product();
        product2.setId(1L);
        product2.setTitle("小米2手机");
        product2.setCategory("手机");
        product2.setPrice(1d);
        product2.setImages("http://www.atguigu/xm.jpg");
        esTemplate.save(product2);
    }

    /**
     * 查询全部的数据
     */
    @Test
    public void getDocumentAll() {
        NativeSearchQuery query = new NativeSearchQueryBuilder().build();
        SearchHits<Product> search = esTemplate.search(query, Product.class);
        List<SearchHit<Product>> searchHits = search.getSearchHits();
        List<Product> productList = searchHits.stream().map(i -> i.getContent()).collect(Collectors.toList());
        System.out.println(search.getTotalHits());
    }

    /**
     * 根据ID查询
     */
    @Test
    public void getDocumentById() {
        if (esTemplate.exists(String.valueOf(1001L), Product.class)) {
            Product product = esTemplate.get(String.valueOf(1001L), Product.class);
            System.out.println("product：" + product);
        }
        if (esTemplate.indexOps(Product.class).exists()) {
            IndexOperations indexOperations = esTemplate.indexOps(Product.class);
            System.out.println(indexOperations);
        }
    }

    /**
     * 修改
     */
    @Test
    public void update() {
        Product product = new Product();
        product.setId(9L);
        //product.setTitle("小米 2 手机");
        //product.setCategory("手机");
        product.setPrice(10d);
        //product.setImages("http://www.atguigu/xm.jpg");
        Document document = Document.create();
        document.append("title", "张三");
        UpdateQuery updateQuery = UpdateQuery.builder(String.valueOf(1L)).withDocument(document).build();
        esTemplate.update(updateQuery, IndexCoordinates.of("product"));
    }

    /**
     * 删除
     */
    @Test
    public void delete() {
        Product product = new Product();
        product.setId(1001L);
        String delete = esTemplate.delete(product);
        System.out.println(delete);
    }

    /**
     * 批量新增
     */
    @Test
    public void saveAll() {
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setId(Long.valueOf(i));
            product.setTitle("[" + i + "]小米手机");
            product.setCategory("手机");
            product.setPrice(1999.0 + i);
            product.setImages("http://www.atguigu/xm.jpg");
            productList.add(product);
        }
        esTemplate.save(productList);
    }

    //分页查询
    @Test
    public void findByPageable() {
        //设置排序(排序方式，正序还是倒序，排序的 id)
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        int currentPage = 1;//当前页，第一页从 0 开始，1 表示第二页
        int pageSize = 5;//每页显示多少条
        //设置查询分页
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize, sort);
        PageRequest pageRequest2 = PageRequest.of(currentPage, pageSize);

        //设置查询哪些特定的字段，不查询哪些特定的字段 "title", "category"
        String[] includes = new String[]{};
        String[] excludes = new String[]{};
        SourceFilter sourceFilter = new FetchSourceFilterBuilder().withIncludes(includes).withExcludes(excludes).build();

        FieldSortBuilder sortBuilder = new FieldSortBuilder("price").order(SortOrder.DESC);
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withPageable(pageRequest)
                .withSourceFilter(sourceFilter)
                .withSort(sortBuilder)//PageRequest中加了排序规则，这里的排序就不生效了
                .build();
        //分页查询
        AggregatedPage<Product> aggregatedPage = esTemplate.queryForPage(query, Product.class, IndexCoordinates.of("product"));
        for (Product product : aggregatedPage) {
            System.out.println(product);
        }
        int number = aggregatedPage.getNumber();
        int size = aggregatedPage.getSize();
        int numberOfElements = aggregatedPage.getNumberOfElements();
        int totalPages = aggregatedPage.getTotalPages();
        long totalElements = aggregatedPage.getTotalElements();
        List<Product> products = aggregatedPage.toList();
        List<Product> content = aggregatedPage.getContent();
        /*Page<Product> productPage = productDao.findAll(pageRequest);
        for (Product product : productPage.getContent()) {
            System.out.println(product);
        }*/
    }

    /**
     * 测试模糊查询：下边的方式没有查询出数据，必须写“小米”才行
     */
    @Test
    public void fuzzyQuery() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("title", "小米");//必须写“小米”才行
        boolQueryBuilder.must(matchPhraseQueryBuilder);
        FieldSortBuilder sortBuilder = new FieldSortBuilder("price").order(SortOrder.DESC);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                //.withSort(sortBuilder)
                .build();
        SearchHits<Product> search = esTemplate.search(nativeSearchQuery, Product.class);
        List<SearchHit<Product>> searchHits = search.getSearchHits();
        List<Product> collect = searchHits.stream().map(e -> e.getContent()).collect(Collectors.toList());
        List<Product> productList = search.get().map(i -> i.getContent()).collect(Collectors.toList());
        for (Product product : collect) {
            System.out.println(product);
        }
    }

    /**
     * 测试模糊查询：下边的方式没有查询出数据
     */
    @Test
    public void fuzzyQuery2() {
        /*SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("title","米"));*/
        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("title", "小米");

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(fuzzyQueryBuilder).build();

        SearchHits<Product> search = esTemplate.search(nativeSearchQuery, Product.class);
        List<SearchHit<Product>> searchHits = search.getSearchHits();
        List<Product> collect = searchHits.stream().map(e -> e.getContent()).collect(Collectors.toList());
        for (Product product : collect) {
            System.out.println("模糊查询：" + product);
        }
    }

    @Test
    public void multiQueryParam() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("title", "张"));
        boolQueryBuilder.should(QueryBuilders.rangeQuery("price").gt(200));
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        SearchHits<Product> search = esTemplate.search(nativeSearchQuery, Product.class);
        List<Product> productList = search.getSearchHits().stream().map(e -> e.getContent()).collect(Collectors.toList());
        for (Product product : productList) {
            System.out.println(product);
        }
    }
}
