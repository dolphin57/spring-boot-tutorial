package com.eric;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.eric.mapper")
public class SpringBootMybatisPlus {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootMybatisPlus.class);
    }
}