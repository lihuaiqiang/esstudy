package com.atguigu.es.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @author lihuaiqiang
 * @description
 * @date 2022/7/1 11:32
 */
@Document(indexName = "user", shards = 5, replicas = 1)
@Data
public class UserInfoEntity implements Serializable {

    private static final long serialVersionUID = 722121518440817645L;

    //    @Field(type = FieldType.Keyword)
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private Boolean mainPosition; //用户所在岗位是否为主岗位

    /**
     * ik_smart：会将文本做最粗粒度的拆分
     * ik_max_word：会将文本做最细粒度的拆分，经自测，改成这个输入“张”或者“三”都可以查询出来数据
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String name;  //姓名

    @Field(type = FieldType.Keyword)
    private String gender; //性别 1 男性 2 女性

    @Field(type = FieldType.Keyword)
    private String catagory;  //人员类别

    @Field(type = FieldType.Keyword)
    private String positionStatus; //岗位状态

    private String order;  //	人员在岗位内排序号

    private String belongPostions;  //	所属组织

    private String sub; // 用户信息providerId|id

    @Field(type = FieldType.Keyword)
    private String providerId; //用户所在二级集团单位id

    /**
     * 岗位信息
     */
    @Field(type = FieldType.Keyword)
    private String positionName;
    /**
     * 部门名称
     */
    @Field(type = FieldType.Keyword)
    private String departmentName;
    /**
     * 单位名称
     */
    @Field(type = FieldType.Keyword)
    private String companyName;

    @Field(type = FieldType.Keyword)
    private String locationName; //组织节点全称

    @Field(type = FieldType.Keyword)
    private String orgCode;

    @Field(type = FieldType.Keyword)
    private String searchCode;
}

