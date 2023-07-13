package com.eric.jsp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class IndexController {
    @Value("${application.index:Hello Eric}")
    private String index;

    @RequestMapping("index")
    public String getIndex() {
        System.out.printf("访问index2");
        return "index";
    }

    @RequestMapping("indexMap")
    public String getIndex(Map<String, Object> map) {
        System.out.printf("访问indexmap2");
        map.put("index", index);
        return "index";
    }
}
