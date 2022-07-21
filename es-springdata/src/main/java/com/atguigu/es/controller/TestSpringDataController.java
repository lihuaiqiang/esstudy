package com.atguigu.es.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<Integer>();
        for(int i=0;i<10;i++){
            list.add(i);
        }
        System.out.println(list);
        //[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]

        //获取元素3-7
        List<Integer> subList = list.subList(3, 15);
        System.out.println(subList);
    }
}
