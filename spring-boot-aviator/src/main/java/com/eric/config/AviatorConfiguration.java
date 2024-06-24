package com.eric.config;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

/**
 * @Author Dolphin
 * @Date 2024/6/23 下午9:58
 * @Description TODO
 */
public class AviatorConfiguration {
    @Bean
    public AviatorEvaluatorInstance aviatorEvaluator() {
        return AviatorEvaluator.newInstance();
    }

    @PostConstruct
    public void init() {
        // 初始化Aviator，如果有全局变量或函数需要注册，可以在这里进行
        AviatorEvaluator.addFunction(new AddFunction());

        // 注册全局变量示例
//        AviatorEvaluator.setGlobalVariable("globalVar", "someValue");
    }
}
