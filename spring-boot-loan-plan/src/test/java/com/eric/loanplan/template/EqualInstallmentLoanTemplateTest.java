package com.eric.loanplan.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EqualInstallmentLoanTemplateTest {
    private EqualInstallmentLoanTemplate equalInstallmentLoanTemplate;

    private BigDecimal loanAmount; //贷款本金
    private BigDecimal annualInterestRate; //年利息
    private Integer loanTerm; //贷款期数

    @BeforeEach
    void setUp() {
        equalInstallmentLoanTemplate = new EqualInstallmentLoanTemplate();
        loanAmount = BigDecimal.valueOf(1000000L); //贷款本金
        annualInterestRate = BigDecimal.valueOf(0.049); //年利息
        loanTerm = 120; //贷款期数
    }

    @Test
    void getPerPrincipal() {
    }

    @Test
    void getPerInterest() {
    }

    @Test
    void getPerPrincipalInterest() {
    }

    @Test
    void getCommercialLoan() {
        Map<Integer, BigDecimal> perPrincipalInterestMap = equalInstallmentLoanTemplate.getPerPrincipalInterest(loanAmount, annualInterestRate, loanTerm);
        Map<Integer, BigDecimal> perInterestMap = equalInstallmentLoanTemplate.getPerInterest(loanAmount, annualInterestRate, loanTerm);
        Map<Integer, BigDecimal> perPrincipalMap = equalInstallmentLoanTemplate.getPerPrincipal(loanAmount, annualInterestRate, loanTerm);
        BigDecimal totalInterest = equalInstallmentLoanTemplate.getTotalInterest(loanAmount, annualInterestRate, loanTerm);
//        BigDecimal totalPrincipalInterest = equalInstallmentLoanTemplate.getPrincipalInterestCount(loanAmount, annualInterestRate, totalMonth);

        System.out.println("等额本息---每月本息：" + perPrincipalInterestMap);
        System.out.println("等额本息---每月利息：" + perInterestMap);
        System.out.println("等额本息---每月本金：" + perPrincipalMap);
        System.out.println("等额本息---总利息：" + totalInterest);
//        System.out.println("等额本息---本息合计：" + totalPrincipalInterest);
    }

    @Test
    void getCombinedLoan() {
        BigDecimal reserveFundAmount = BigDecimal.valueOf(1080000);
        BigDecimal reserveFundRate = BigDecimal.valueOf(0.0325);
        BigDecimal commercialLoanAmount = BigDecimal.valueOf(1000000);
        BigDecimal commercialLoanRate = BigDecimal.valueOf(0.046);

        Map<Integer, BigDecimal> perReserveFundPrincipalInterestMap = equalInstallmentLoanTemplate.getPerPrincipalInterest(reserveFundAmount, reserveFundRate, loanTerm);
        System.out.println("等额本息公积金---每月本息：" + perReserveFundPrincipalInterestMap);
        Map<Integer, BigDecimal> perReserveFundInterestMap = equalInstallmentLoanTemplate.getPerInterest(reserveFundAmount, reserveFundRate, loanTerm);
        System.out.println("等额本息公积金---每月利息：" + perReserveFundInterestMap);
        Map<Integer, BigDecimal> perReserveFundPrincipalMap = equalInstallmentLoanTemplate.getPerPrincipal(reserveFundAmount, reserveFundRate, loanTerm);
        System.out.println("等额本息公积金---每月利息：" + perReserveFundPrincipalMap);
        BigDecimal totalReserveFundInterest = equalInstallmentLoanTemplate.getTotalInterest(reserveFundAmount, reserveFundRate, loanTerm);
        System.out.println("等额本息公积金---总利息：" + totalReserveFundInterest);
//        BigDecimal totalreserveFundPrincipalInterest = getPrincipalInterestCount(reserveFundAmount, reserveFundRate, totalMonth);

        Map<Integer, BigDecimal> perCommercialLoanPrincipalInterestMap = equalInstallmentLoanTemplate.getPerPrincipalInterest(commercialLoanAmount, commercialLoanRate, loanTerm);
        System.out.println("等额本息公积金---每月本息：" + perCommercialLoanPrincipalInterestMap);
        Map<Integer, BigDecimal> perCommercialLoanInterestMap = equalInstallmentLoanTemplate.getPerInterest(commercialLoanAmount, commercialLoanRate, loanTerm);
        System.out.println("等额本息公积金---每月利息：" + perCommercialLoanInterestMap);
        Map<Integer, BigDecimal> perCommercialLoanPrincipalMap = equalInstallmentLoanTemplate.getPerPrincipal(commercialLoanAmount, commercialLoanRate, loanTerm);
        System.out.println("等额本息公积金---每月利息：" + perCommercialLoanPrincipalMap);
        BigDecimal totalCommercialLoanInterest = equalInstallmentLoanTemplate.getTotalInterest(commercialLoanAmount, commercialLoanRate, loanTerm);
        System.out.println("等额本息公积金---总利息：" + totalCommercialLoanInterest);
//        BigDecimal totalcommercialLoanPrincipalInterest = getPrincipalInterestCount(commercialLoanAmount, commercialLoanRate, totalMonth);
    }
}