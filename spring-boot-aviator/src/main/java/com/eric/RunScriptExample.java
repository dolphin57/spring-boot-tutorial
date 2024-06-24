package com.eric;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;

public class RunScriptExample {
    public static void main(final String[] args) throws Exception {
        // Compile the script into a Expression instance.
        Expression exp = AviatorEvaluator.getInstance().compileScript("examples/20-lambda.av");
        // Run the exprssion.
        exp.execute();

    }
}
