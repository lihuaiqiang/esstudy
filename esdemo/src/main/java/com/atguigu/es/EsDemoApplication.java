package com.atguigu.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author lihuaiqiang
 * @description
 * @date 2022/6/15 11:02
 * <p>
 * (exclude = DataSourceAutoConfiguration.class)：如果项目中的依赖中添加了数据源的依赖，配置文件中又没有配置数据源的情况下，
 * 可以排除数据源的加载
 */
@SpringBootApplication/*(exclude = DataSourceAutoConfiguration.class)*/
public class EsDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsDemoApplication.class, args);
    }
}
