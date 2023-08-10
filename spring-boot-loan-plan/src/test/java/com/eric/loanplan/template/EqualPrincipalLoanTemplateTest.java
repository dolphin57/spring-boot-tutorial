package com.eric.loanplan.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EqualPrincipalLoanTemplateTest {
    private EqualPrincipalLoanTemplate principalLoanTemplate;

    @BeforeEach
    void setUp() {
        principalLoanTemplate = new EqualPrincipalLoanTemplate();
    }

    @Test
    void getPerPrincipal() {
    }

    @Test
    void getPerInterest() {
    }

    @Test
    void getAverageCapital() {
        BigDecimal loanAmount = BigDecimal.valueOf(1000000); // 本金
        BigDecimal annualInterestRate = BigDecimal.valueOf(4.9); // 年利率
        int loanTerm = 120;

        // 明细计算
        Map<Integer, BigDecimal> monthlyPrincipal = principalLoanTemplate.getPerPrincipal(loanAmount, annualInterestRate, loanTerm);
        System.out.println("等额本金---每月本金:" + monthlyPrincipal);

        Map<Integer, BigDecimal> perMonthRemainingPrincipal = principalLoanTemplate.getPerRemainingPrincipal(loanAmount, loanTerm);
        System.out.println("等额本金---每月剩余本金:" + perMonthRemainingPrincipal);

        Map<Integer, BigDecimal> mapInterest = principalLoanTemplate.getPerInterest(loanAmount, annualInterestRate, loanTerm);
        System.out.println("等额本金---每月利息:" + mapInterest);

        Map<Integer, BigDecimal> monthlyPrincipalInterest = principalLoanTemplate.getPerPrincipalInterest(loanAmount, annualInterestRate, loanTerm);
        System.out.println("等额本金---每月本息：" + monthlyPrincipalInterest);

        // 汇总计算
        BigDecimal interestCount = principalLoanTemplate.getTotalInterest(loanAmount, annualInterestRate, loanTerm);
        System.out.println("等额本金---总利息：" + interestCount);

        BigDecimal totalPrincipalInterest = loanAmount.add(interestCount);
        System.out.println("等额本金---本息合计:" + totalPrincipalInterest);
    }
}