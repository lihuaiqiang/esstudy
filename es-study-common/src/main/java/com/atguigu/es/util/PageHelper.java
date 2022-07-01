package com.atguigu.es.util;

//import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lihuaiqiang
 * @description
 * @date 2022/7/1 16:04
 */
@Data
public class PageHelper<T> implements Serializable {

    /**
     * 总记录数
     */
    private Integer total;
    /**
     * 每页记录数
     */
    private Integer size;
    /**
     * 总页数
     */
    private Integer pages;
    /**
     * 当前页数
     */
    private Integer current;
    /**
     * 列表数据
     */
    private List<T> records;

    /**
     * 分页
     *
     * @param totalCount 总记录数
     * @param pageSize   每页记录数
     * @param currPage   当前页数
     */
    public PageHelper(int totalCount, int pageSize, int currPage) {
        this.total = totalCount;
        this.size = pageSize;
        this.current = currPage;
        this.pages = (int) Math.ceil((double) totalCount / pageSize);
    }


    /**
     * 分页
     *
     * @param list       列表数据
     * @param totalCount 总记录数
     * @param pageSize   每页记录数
     * @param currPage   当前页数
     */
    public PageHelper(List<T> list, int totalCount, int pageSize, int currPage) {
        this.records = list;
        this.total = totalCount;
        this.size = pageSize;
        this.current = currPage;
        this.pages = (int) Math.ceil((double) totalCount / pageSize);
    }

    /**
     * 分页
     */
//    public PageHelper(IPage<T> page) {
//        this.records = page.getRecords();
//        this.total = (int) page.getTotal();
//        this.size = (int) page.getSize();
//        this.current = (int) page.getCurrent();
//        this.pages = (int) page.getPages();
//    }
}
