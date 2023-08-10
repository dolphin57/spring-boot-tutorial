package com.eric.loanplan.template;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InterestFirstLoanTemplateTest {
    private static BigDecimal loanAmount; //贷款本息
    private static BigDecimal annualInterestRate; //年利息
    private static Integer loanTerm; //贷款期数

    private static InterestFirstLoanTemplate interestFirstLoanTemplate;

    @BeforeAll
    public static void setUp() throws Exception {
        loanAmount = BigDecimal.valueOf(1000000L); //贷款本息
        annualInterestRate = BigDecimal.valueOf(0.049); //年利息
        loanTerm = 12; //贷款期数
        interestFirstLoanTemplate = new InterestFirstLoanTemplate();
    }

    @Test
    void getPerInterest() {
    }

    @Test
    void getPerPrincipalInterest() {
        Map<Integer, BigDecimal> monthlyRepayment = interestFirstLoanTemplate.getPerPrincipalInterest(loanAmount, annualInterestRate, loanTerm);
        for (Map.Entry<Integer, BigDecimal> entry : monthlyRepayment.entrySet()) {
            System.out.println("期数: " + entry.getKey() + " 还款额：" + entry.getValue());
        }
    }
}