package com.eric.sensitive;

import com.eric.sensitive.plugin.SensitiveInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @Description:
 * @Author: Eric Liang
 * @Since: 2020-9-9 20:20
 */
@SpringBootApplication
@MapperScan("com.eric.sensitive.mapper")
public class SensitiveApplication {
    public static void main(String[] args) {
        SpringApplication.run(SensitiveApplication.class, args);
    }

    @Bean
    public SensitiveInterceptor sensitiveInterceptor(){
        return new SensitiveInterceptor();
    }
}
