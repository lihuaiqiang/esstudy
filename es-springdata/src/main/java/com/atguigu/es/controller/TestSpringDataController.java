package com.atguigu.es.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lihuaiqiang
 * @description
 * @date 2022/6/30 7:54
 */
@RestController
public class TestSpringDataController {

    @GetMapping("/hello")
    public String test () {
        return "Hello Es SpringData!";
    }
}
