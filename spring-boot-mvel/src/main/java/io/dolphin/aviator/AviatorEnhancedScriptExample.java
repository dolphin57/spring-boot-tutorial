package io.dolphin.aviator;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dolphin
 * @date 2024年04月15日 14:49
 * @description
 */
public class AviatorEnhancedScriptExample {
    public static void main(String[] args) {
        // 定义脚本内容
        String script = "int base = 8;\n"
                + "int multiplier = 6;\n"
                + "\n"
                + "// 计算结果\n"
                + "int result = base * multiplier;\n"
                + "\n"
                + "// 根据结果进行条件判断\n"
                + "String message = (result > 90) ? \"Result is greater than 90\" : \"Result is less than or equal to 90\";\n"
                + "\n"
                + "// 返回最终结果和消息\n"
                + "[result, message]";
        // 编译脚本
        Expression compiledExpr = AviatorEvaluator.compile(script);

        // 执行脚本并获取结果
        Object executionResult = compiledExpr.execute();

        // 处理结果（假设返回的是Object数组）
        if (executionResult instanceof ArrayList) {
            List results = (ArrayList) executionResult;
            int calculatedResult = (int) results.get(0);
            String message = (String) results.get(1);

            System.out.println("Calculated Result: " + calculatedResult);
            System.out.println("Message: " + message);
        }
    }
}