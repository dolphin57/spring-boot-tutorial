package com.eric.service;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author Dolphin
 * @Date 2024/6/23 下午10:07
 * @Description TODO
 */
@Service
public class ExpressionService {
    @Autowired
    private AviatorEvaluator evaluator;

    public Object evaluate(String expression, Map<String, Object> variables) {
        Expression compiledExpression = evaluator.compile(expression);
        return compiledExpression.execute(variables);
    }
}
