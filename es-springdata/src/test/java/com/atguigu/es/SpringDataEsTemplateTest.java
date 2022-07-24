package com.atguigu.es;

import com.atguigu.es.bean.Product;
import com.atguigu.es.bean.UserInfoEntity;
import com.atguigu.es.util.PageHelper;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
        product2.setId(1001L);
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
        //List<Product> productList1 = esTemplate.queryForList(query, Product.class, IndexCoordinates.of("product"));
        List<SearchHit<Product>> searchHits = search.getSearchHits();
        List<Product> productList = searchHits.stream().map(i -> i.getContent()).collect(Collectors.toList());
        for (Product userInfoEntity : productList) {
            System.out.println("查询全部的数据" + userInfoEntity);
        }
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
        document.append("userOrder", 20);
        UpdateQuery updateQuery = UpdateQuery.builder(String.valueOf(27L)).withDocument(document).build();
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
        boolean exists = esTemplate.exists(String.valueOf(1L), Product.class);
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
            product.setId(Long.valueOf(i + 30));
            product.setTitle("比亚迪" + i);
            product.setCategory("新能源汽车");
            product.setPrice(10.0 + i);
            product.setImages("http://www.atguigu/xm.jpg");
            product.setCode((i + 1) + "0001000" + (i + 1));
            product.setPositionOrder(new BigDecimal("0." + product.getCode()));
            product.setUserOrder(new BigDecimal(product.getId() - 1));
            productList.add(product);
        }
        esTemplate.save(productList);
    }

    public String dealOrder(String order){
        if(order.length() == 1)
            order ="00" + order;
        if(order.length() == 2)
            order ="0" + order;
        return order;
    }

    /**
     * 批量新增
     */
    @Test
    public void saveAll2() {
        List<UserInfoEntity> userInfoList = new ArrayList<>();
        //79500 79900 80000  85000 87000 87400 87450  可以
        //87480 87500  不可以
        for (int i = 1; i <= 50500; i++) {
            UserInfoEntity userInfoEntity = new UserInfoEntity();
            userInfoEntity.setId(String.valueOf(i));
            userInfoEntity.setMainPosition(false);
            userInfoEntity.setName("张三" + i + 10);
            if (i % 2 == 0) {
                userInfoEntity.setGender("男");
            } else {
                userInfoEntity.setGender("女");
            }
            userInfoEntity.setOrgCode("100" + i + 10);
            userInfoList.add(userInfoEntity);
        }
//        Long statTime = System.currentTimeMillis();
//        esTemplate.save(userInfoList);
//        System.out.println("定时任务UserTask处理结束,耗时:{}" + (System.currentTimeMillis() - statTime) / 1000);
        List<List<UserInfoEntity>> splistList = splistList(userInfoList, 10000);
        for (List<UserInfoEntity> userInfoEntityList : splistList) {
            System.out.println(userInfoEntityList.size());
        }
    }

    public static <T> List<List<T>> splistList(List<T> list, int subNum) {
        List<List<T>> tNewList = new ArrayList<List<T>>();
        int priIndex = 0;
        int lastPriIndex = 0;
        int insertTimes = list.size() / subNum;
        List<T> subList = new ArrayList<>();
        for (int i = 0; i <= insertTimes; i++) {
            priIndex = subNum * i;
            lastPriIndex = priIndex + subNum;
            if (i == insertTimes) {
                subList = list.subList(priIndex, list.size());
            } else {
                subList = list.subList(priIndex, lastPriIndex);
            }
            if (subList.size() > 0) {
                tNewList.add(subList);
            }
        }
        return tNewList;
    }

    @Test
    public void findBySort() {
        PrefixQueryBuilder prefixQueryBuilder = QueryBuilders.prefixQuery("title", "小米");
        FieldSortBuilder sortBuilder = new FieldSortBuilder("positionOrder").order(SortOrder.DESC);
        FieldSortBuilder sortBuilder2 = new FieldSortBuilder("userOrder").order(SortOrder.ASC);
        FieldSortBuilder sortBuilder3 = new FieldSortBuilder("code.keyword").order(SortOrder.DESC);
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(prefixQueryBuilder)
                //.withPageable(pageRequest)
                //.withSourceFilter(sourceFilter)
                .withSort(sortBuilder2)//PageRequest中加了排序规则，这里的排序就不生效了
                .withSort(sortBuilder)//PageRequest中加了排序规则，这里的排序就不生效了
                .withSort(sortBuilder3)//PageRequest中加了排序规则，这里的排序就不生效了
                .build();
        //分页查询
        AggregatedPage<Product> aggregatedPage = esTemplate.queryForPage(query, Product.class, IndexCoordinates.of("product"));
        for (Product product : aggregatedPage) {
            System.out.println(product);
        }
    }

    //分页查询、排序
    @Test
    public void findByPageable() {
        //多字段排序
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "price"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "id"));
//        orders.add(new Sort.Order(Sort.Direction.ASC, "id.keyword"));
        Sort sort = Sort.by(orders);

        //设置排序(排序方式，正序还是倒序，排序的 id)
//        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        int currentPage = 0;//当前页，第一页从 0 开始，1 表示第二页
        int pageSize = 100;//每页显示多少条
        //设置查询分页
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize, sort);

        //设置查询哪些特定的字段，不查询哪些特定的字段 "title", "category"
        String[] includes = new String[]{};
        String[] excludes = new String[]{};
        SourceFilter sourceFilter = new FetchSourceFilterBuilder().withIncludes(includes).withExcludes(excludes).build();

        FieldSortBuilder sortBuilder = new FieldSortBuilder("price").order(SortOrder.DESC);
        FieldSortBuilder sortBuilder2 = new FieldSortBuilder("id").order(SortOrder.ASC);
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                //.withPageable(pageRequest)
                //.withSourceFilter(sourceFilter)
                .withSort(sortBuilder)//PageRequest中加了排序规则，这里的排序就不生效了
                .withSort(sortBuilder2)//PageRequest中加了排序规则，这里的排序就不生效了
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

        MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("title", "张三");//必须写“小米”才行
        boolQueryBuilder.must(matchPhraseQueryBuilder);
        FieldSortBuilder sortBuilder2 = new FieldSortBuilder("id").order(SortOrder.DESC);
        FieldSortBuilder sortBuilder = new FieldSortBuilder("price").order(SortOrder.DESC);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withSort(sortBuilder)
                .withSort(sortBuilder2)
                .build();
        nativeSearchQuery.setTrackTotalHits(true);
        SearchHits<Product> search = esTemplate.search(nativeSearchQuery, Product.class);
        List<SearchHit<Product>> searchHits = search.getSearchHits();
        List<Product> collect = searchHits.stream().map(e -> e.getContent()).collect(Collectors.toList());
        List<Product> productList = search.get().map(i -> i.getContent()).collect(Collectors.toList());
        for (Product product : collect) {
            System.out.println(product);
        }
    }

    /**
     * 测试多字段排序
     */
    @Test
    public void multiFieldsSort() {
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "positionOrder.keyword"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "order.keyword"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "id.keyword"));
        Sort sort = Sort.by(orders);
        int currentPage = 0;//当前页，第一页从 0 开始，1 表示第二页
        int pageSize = 10;//每页显示多少条
        //设置查询分页
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize, sort);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("name", "老绊");//必须写“小米”才行
        boolQueryBuilder.must(matchPhraseQueryBuilder);
        boolQueryBuilder.must(QueryBuilders.termQuery("searchCode", "0000100001"));
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(pageRequest)
                .build();
        nativeSearchQuery.setTrackTotalHits(true);
        SearchHits<UserInfoEntity> search = esTemplate.search(nativeSearchQuery, UserInfoEntity.class);
        List<SearchHit<UserInfoEntity>> searchHits = search.getSearchHits();
        List<UserInfoEntity> collect = searchHits.stream().map(e -> e.getContent()).collect(Collectors.toList());
        List<UserInfoEntity> productList = search.get().map(i -> i.getContent()).collect(Collectors.toList());
        for (UserInfoEntity product : collect) {
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

    /**
     * 测试前缀查询：下边的方式没有查询出数据
     */
    @Test
    public void prefixQuery() {
        /*SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("title","米"));*/
        PrefixQueryBuilder prefixQueryBuilder = QueryBuilders.prefixQuery("code", "10001");

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(prefixQueryBuilder).build();

        SearchHits<Product> search = esTemplate.search(nativeSearchQuery, Product.class);
        boolean b = search.hasSearchHits();
        List<SearchHit<Product>> searchHits = search.getSearchHits();
        List<Product> collect = searchHits.stream().map(e -> e.getContent()).collect(Collectors.toList());
        for (Product product : collect) {
            System.out.println("前缀查询：" + product);
        }
    }

    /**
     * 多条件前缀查询：下边的方式没有查询出数据
     */
    @Test
    public void prefixQuery2() {
        /*SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("title","米"));*/
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        PrefixQueryBuilder prefixQueryBuilder = QueryBuilders.prefixQuery("orgCode", "0000100001");
        PrefixQueryBuilder prefixQueryBuilder2 = QueryBuilders.prefixQuery("orgCode", "000010000100179");
        boolQueryBuilder.should(prefixQueryBuilder);
        boolQueryBuilder.should(prefixQueryBuilder2);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(prefixQueryBuilder)
                .build();
        Long statTime = System.currentTimeMillis();
        SearchHits<UserInfoEntity> search = esTemplate.search(nativeSearchQuery, UserInfoEntity.class);
        System.out.println(("定时任务UserTask处理结束,耗时:{}" + (System.currentTimeMillis() - statTime) ));
        boolean b = search.hasSearchHits();
        List<SearchHit<UserInfoEntity>> searchHits = search.getSearchHits();
        List<UserInfoEntity> collect = searchHits.stream().map(e -> e.getContent()).collect(Collectors.toList());
        for (UserInfoEntity product : collect) {
            System.out.println("前缀查询：" + product);
        }
    }

    /**
     * 测试根据前缀删除数据
     */
    @Test
    public void deleteByPrefixQuery() {
        System.out.println("=============删除之前查询==============");
        NativeSearchQuery query = new NativeSearchQueryBuilder().build();
        SearchHits<Product> search = esTemplate.search(query, Product.class);
        List<SearchHit<Product>> searchHits = search.getSearchHits();
        List<Product> productList = searchHits.stream().map(i -> i.getContent()).collect(Collectors.toList());
        for (Product product : productList) {
            System.out.println("删除之前的数据" + product);
        }
        System.out.println("删除之后的数据" + search.getTotalHits());

        System.out.println("=============删除操作==============");

        PrefixQueryBuilder prefixQueryBuilder2 = QueryBuilders.prefixQuery("code", "20001");
        NativeSearchQuery nativeSearchQuery2 = new NativeSearchQueryBuilder().withQuery(prefixQueryBuilder2).build();
        esTemplate.delete(nativeSearchQuery2, Product.class, esTemplate.getIndexCoordinatesFor(Product.class));

        System.out.println("=============删除之后查询==============");

        NativeSearchQuery query2 = new NativeSearchQueryBuilder().build();
        SearchHits<Product> search2 = esTemplate.search(query2, Product.class);
        List<SearchHit<Product>> searchHits2 = search2.getSearchHits();
        List<Product> productList2 = searchHits2.stream().map(i -> i.getContent()).collect(Collectors.toList());
        for (Product product : productList2) {
            System.out.println("删除之后的数据" + product);
        }
        System.out.println("删除之后的数据" + search2.getTotalHits());
    }

    /**
     * 测试条件查询之后再进行删除
     */
    @Test
    public void deleteBySearchQuery() {
        System.out.println("=============删除之前查询==============");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("searchCode", "000010006600003");
        boolQueryBuilder.must(termQueryBuilder);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        SearchHits<UserInfoEntity> search = esTemplate.search(nativeSearchQuery, UserInfoEntity.class);
        List<SearchHit<UserInfoEntity>> searchHits = search.getSearchHits();
        List<UserInfoEntity> userInfoEntityList = searchHits.stream().map(i -> i.getContent()).collect(Collectors.toList());
        for (UserInfoEntity userInfoEntity : userInfoEntityList) {
            System.out.println("删除之前的数据" + userInfoEntity);
        }
        System.out.println("删除之前的数据" + search.getTotalHits());

        System.out.println("UserInfoEntity索引是否存在：" + esTemplate.indexOps(UserInfoEntity.class).exists());
        System.out.println("=============删除操作==============");

        BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery();
        TermQueryBuilder termQueryBuilder2 = QueryBuilders.termQuery("searchCode", "000010006600003");
        boolQueryBuilder2.must(termQueryBuilder2);
        NativeSearchQuery nativeSearchQuery2 = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder2).build();
        esTemplate.delete(nativeSearchQuery2, UserInfoEntity.class, esTemplate.getIndexCoordinatesFor(UserInfoEntity.class));

        System.out.println("=============删除之后查询==============");
        BoolQueryBuilder boolQueryBuilder3 = QueryBuilders.boolQuery();
        TermQueryBuilder termQueryBuilder3 = QueryBuilders.termQuery("searchCode", "000010006600003");
        boolQueryBuilder3.must(termQueryBuilder3);
        NativeSearchQuery nativeSearchQuery3 = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder3).build();
        SearchHits<UserInfoEntity> search3 = esTemplate.search(nativeSearchQuery3, UserInfoEntity.class);
        List<SearchHit<UserInfoEntity>> searchHits3 = search3.getSearchHits();
        List<UserInfoEntity> userInfoEntityList3 = searchHits3.stream().map(i -> i.getContent()).collect(Collectors.toList());
        for (UserInfoEntity userInfoEntity : userInfoEntityList3) {
            System.out.println("删除之后的数据" + userInfoEntity);
        }
        System.out.println("删除之后的数据" + search3.getTotalHits());
    }

    /**
     * 根据条件删除数据之前，先判断是否有数据
     */
    @Test
    public void deleteEsBySearchQuery2() {
        System.out.println("UserInfoEntity索引是否存在：" + esTemplate.indexOps(UserInfoEntity.class).exists());
        if (esTemplate.indexOps(UserInfoEntity.class).exists()) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("searchCode", "0000100066010007700499001");
            boolQueryBuilder.must(termQueryBuilder);
            NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
            SearchHits<UserInfoEntity> search = esTemplate.search(nativeSearchQuery, UserInfoEntity.class);
            System.out.println("数据总数：" + search.getTotalHits());
            if (search.hasSearchHits()) {
                List<SearchHit<UserInfoEntity>> searchHits3 = search.getSearchHits();
                List<UserInfoEntity> userInfoEntityList3 = searchHits3.stream().map(i -> i.getContent()).collect(Collectors.toList());
                for (UserInfoEntity userInfoEntity : userInfoEntityList3) {
                    System.out.println("查询到的数据：" + userInfoEntity);
                }
                esTemplate.delete(nativeSearchQuery, UserInfoEntity.class, esTemplate.getIndexCoordinatesFor(UserInfoEntity.class));
            }
        }
    }

    /**
     * 借鉴了poc中铁建项目中李林的写法：测试模糊查询
     */
    @Test
    public void fuzzyQuery3() {
        int currentPage = 0;//当前页，第一页从 0 开始，1 表示第二页
        int pageSize = 5;//每页显示多少条
        String userName = "三";
        String orgCode = "100100100";
        PageRequest page = PageRequest.of(0, 4);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(userName)) {
            MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("name", userName);
//            MatchQueryBuilder matchQueryBuilder = (MatchQueryBuilder) matchPhraseQueryBuilder;
            boolQueryBuilder.must(matchPhraseQueryBuilder);
        }
        FieldSortBuilder sortBuilder = new FieldSortBuilder("id.keyword").order(SortOrder.DESC);
        //boolQueryBuilder.must(QueryBuilders.termQuery("searchCode", orgCode));
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withSort(sortBuilder)
                .withPageable(page)
                .build();
        searchQuery.setTrackTotalHits(true);
        SearchHits<UserInfoEntity> search = esTemplate.search(searchQuery, UserInfoEntity.class);
        List<SearchHit<UserInfoEntity>> searchHits = search.getSearchHits();
        Long totalHits = search.getTotalHits();
        List<UserInfoEntity> list = searchHits.stream().map(i -> i.getContent()).collect(Collectors.toList());
        for (UserInfoEntity userInfoEntity : list) {
            System.out.println("userInfoEntity：" + userInfoEntity);
        }
        PageHelper<UserInfoEntity> pageHelper = new PageHelper<UserInfoEntity>(list, totalHits.intValue(), pageSize, currentPage + 1);
    }

    /**
     * 多参数查询
     */
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

    /**
     * 多参数查询
     */
    @Test
    public void multiQueryParam2() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        List<String> orgCodeList = Arrays.asList("0000100001040007702399001", "0000100001040007702388002");
        boolQueryBuilder.must(QueryBuilders.termsQuery("searchCode", orgCodeList));
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        SearchHits<UserInfoEntity> search = esTemplate.search(nativeSearchQuery, UserInfoEntity.class);
        List<UserInfoEntity> productList = search.getSearchHits().stream().map(e -> e.getContent()).collect(Collectors.toList());
        for (UserInfoEntity product : productList) {
            System.out.println(product);
        }
    }

    /**
     * 多参数查询
     */
    @Test
    public void queryParam() {
        //BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //boolQueryBuilder.must(QueryBuilders.matchQuery("title", "张"));
        //boolQueryBuilder.should(QueryBuilders.rangeQuery("price").gt(200));
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", "张三7");
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(termQueryBuilder)
                .build();
        SearchHits<Product> search = esTemplate.search(nativeSearchQuery, Product.class);
        List<Product> productList = search.getSearchHits().stream().map(e -> e.getContent()).collect(Collectors.toList());
        for (Product product : productList) {
            System.out.println(product);
        }
    }
}
