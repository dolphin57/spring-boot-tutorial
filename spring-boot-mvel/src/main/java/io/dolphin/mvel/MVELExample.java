package io.dolphin.mvel;

import org.mvel2.MVEL;
import org.mvel2.integration.impl.MapVariableResolverFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dolphin
 * @date 2024年04月11日 11:10
 * @description
 */
public class MVELExample {
    public static void main(String[] args) {
        // 1. 基础表达式
        Object result = MVEL.eval("2 + 3 * 4");
        System.out.println("Result: " + result); // 输出: Result: 14

        // 2. 使用变量
        Map<String, Object> vars = new HashMap<>();
        vars.put("a", 5);
        vars.put("b", 7);

        // 使用变量的表达式计算：a * b
        String expression = "a * b";
        Object varRes = MVEL.eval(expression, vars);
        System.out.println("varRes: " + varRes); // 输出: Result: 35

        // 3. 使用函数调用
        Arithmetic arithmetic = new Arithmetic();
        Map<String, Object> context = new HashMap<>();
        context.put("arithmetic", arithmetic);

        // 调用对象方法的表达式：arithmetic.square(5)
        String functionExpression = "arithmetic.square(5)";
        Object functionRes = MVEL.eval(functionExpression, context);
        System.out.println("functionRes: " + functionRes); // 输出: Result: 25

        // 4. 条件判断与三目运算符表达式
        Map<String, Object> condVars = new HashMap<>();
        condVars.put("a", 10);
        condVars.put("b", 5);
        String conditionExpression = "a > b ? a : b";
        Object condRes = MVEL.eval(conditionExpression, condVars);
        System.out.println("condRes: " + condRes);
    }

    public static class Arithmetic {
        public static int square(int value) {
            return value * value;
        }
    }
}
