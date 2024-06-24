package io.dolphin.aviator;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.Options;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author dolphin
 * @date 2024年04月29日 17:58
 * @description
 */
public class AviatorExample {
    public static void main(String[] args) throws IOException {
        String expression = "a-(b-c) > 100";
        Expression compiledExp = AviatorEvaluator.compile(expression);
        Boolean result =
                (Boolean) compiledExp.execute(compiledExp.newEnv("a", 100.3, "b", 45, "c", -199.100));
        System.out.println(result);

        String deciExpr = "100.3 / 3.001";
        Expression compileDec = AviatorEvaluator.compile(deciExpr);
        double decResult = (double) compileDec.execute();
        System.out.println(decResult);
    }
}
