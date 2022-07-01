package com.atguigu.es;

import com.atguigu.es.bean.Product;
import com.atguigu.es.bean.UserInfoEntity;
import com.atguigu.es.dao.ProductDao;
import com.atguigu.es.dao.UserInfoEntityDao;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author lihuaiqiang
 * @description
 * @date 2022/6/30 9:54
 * <p>
 * 文档操作测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringDataESProductDaoTest {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private UserInfoEntityDao userInfoEntityDao;

    /**
     * 新增
     */
    @Test
    public void saveTest() {
        Product product = new Product();
        product.setId(1001L);
        product.setTitle("比亚迪-唐");
        product.setCategory("新能源");
        product.setPrice(150066.88D);
        product.setImages("http://baidu.com/1.png");
        productDao.save(product);
    }

    /**
     * 查询所有
     */
    @Test
    public void getDocumentAll() {
        Iterable<Product> all = productDao.findAll();
        for (Product product : all) {
            System.out.println(product);
        }
    }

    /**
     * 根据ID查询
     */
    @Test
    public void getDocumentById() {
        Optional<Product> byId = productDao.findById(1001L);
        if (byId.isPresent()) {
            Product product = byId.get();
            System.out.println(product);
        }
    }

    /**
     * 修改
     */
    @Test
    public void update() {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("小米 2 手机");
        product.setCategory("手机");
        product.setPrice(1d);
        product.setImages("http://www.atguigu/xm.jpg");
        productDao.save(product);
    }

    /**
     * 删除
     */
    @Test
    public void delete() {
        Product product = new Product();
        product.setId(1001L);
        productDao.delete(product);
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
        productDao.saveAll(productList);
    }

    //分页查询
    @Test
    public void findByPageable() {
        //设置排序(排序方式，正序还是倒序，排序的 id)
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        int currentPage = 0;//当前页，第一页从 0 开始，1 表示第二页
        int pageSize = 5;//每页显示多少条
        //设置查询分页
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize, sort);
        //分页查询
        Page<Product> productPage = productDao.findAll(pageRequest);
        for (Product Product : productPage.getContent()) {
            System.out.println(Product);
        }
    }

    /**
     * 我自己测试的：分页查询
     */
    @Test
    public void findByPageable2() {
        //设置排序(排序方式，正序还是倒序，排序的 id)
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        int currentPage = 0;//当前页，第一页从 0 开始，1 表示第二页
        int pageSize = 4;//每页显示多少条
        //设置查询分页
        /*PageRequest pageRequest = PageRequest.of(currentPage, pageSize, sort);
        //分页查询
        Page<Product> productPage = productDao.findAll(pageRequest);
        for (Product Product : productPage.getContent()) {
            System.out.println(Product);
        }*/


        String keyword = "张";
        String describe = "";
        PageRequest pageable = PageRequest.of(currentPage, pageSize);
        // 创建查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(keyword)) {
            // 模糊查询 一定要ik中文
            MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("name", keyword);
            boolQueryBuilder.must(matchQuery);
        }
        if (!StringUtils.isEmpty(describe)) {
            MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("describe", describe);
            boolQueryBuilder.should(matchQuery);
        }
        // 调用查询接口
        Page<UserInfoEntity> page = userInfoEntityDao.search(boolQueryBuilder, pageable);
        // 记录总数
        long totalElements = page.getTotalElements();
        // 计算分页总数
        int totalPage = (int) ((page.getTotalElements() - 1) / pageable.getPageSize() + 1);
        System.out.println("totalElements：" + totalElements);
        System.out.println("totalPage：" + totalPage);
    }

    //============================================================================

    /**
     * term 查询
     * search(termQueryBuilder) 调用搜索方法，参数查询构建器对象
     */
    @Test
    public void termQuery() {
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", "小米");
        Iterable<Product> products = productDao.search(termQueryBuilder);
        for (Product product : products) {
            System.out.println("小米" + product);
        }
        /* 这种写法没能查询出数据
        MatchPhraseQueryBuilder phraseQuery = QueryBuilders.matchPhraseQuery("title", "米");
        Iterable<Product> search = productDao.search(phraseQuery);
        for (Product product : search) {
            System.out.println("米" + product);
        }*/
    }

    /**
     * term 查询加分页
     */
    @Test
    public void termQueryByPage() {
        int currentPage = 0;
        int pageSize = 5;
        //设置查询分页
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize);
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", " 小米");
        Iterable<Product> products = productDao.search(termQueryBuilder, pageRequest);
        for (Product product : products) {
            System.out.println(product);
        }
    }
}
