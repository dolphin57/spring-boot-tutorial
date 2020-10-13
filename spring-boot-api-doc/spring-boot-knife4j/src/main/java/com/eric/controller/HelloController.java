package com.eric.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: Eric Liang
 * @Since: 2020-10-13 17:36
 */
@Api(tags = "你好")
@RestController
@RequestMapping("/hello")
public class HelloController {

    @ApiOperation(value = "向客人问好")
    @GetMapping("/sayHi")
    public ResponseEntity<String> sayHi(@RequestParam("name")String name){
        return ResponseEntity.ok("Hi:"+name);
    }
}
