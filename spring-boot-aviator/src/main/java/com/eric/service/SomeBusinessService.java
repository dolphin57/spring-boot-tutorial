package com.eric.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Dolphin
 * @Date 2024/6/23 下午10:09
 * @Description TODO
 */
@Service
public class SomeBusinessService {
    @Autowired
    private ExpressionService expressionService;

    public void someBusinessMethod() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("x", 10);
        variables.put("y", 20);

        String expression = "x + y"; // 简单的加法表达式
        Object result = expressionService.evaluate(expression, variables);
        System.out.println("Result: " + result);
    }
}
