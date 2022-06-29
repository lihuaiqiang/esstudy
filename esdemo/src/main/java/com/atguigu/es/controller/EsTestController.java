package com.atguigu.es.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lihuaiqiang
 * @description
 * @date 2022/6/15 11:07
 */
@RestController
public class EsTestController {

    @GetMapping("/hello")
    public String test () {
        return "Hello Es !";
    }
}
