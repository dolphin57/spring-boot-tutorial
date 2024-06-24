package com.eric.loanplan.template;

import java.time.LocalDate;

/**
 * @author dolphin
 * @date 2023年12月25日 11:01
 * @description
 */
public class LoanCalculator {
    public static void main(String[] args) {
        double loanAmount = 466183.62;
        int disbursementDate = 21;
        double interestRate = 0.047;
        int term = 6;
        LocalDate startDate = LocalDate.of(2023, 10, 19);

        int totalPayments = term;
        double monthlyInterestRate = interestRate / 12;
        double presentValue = loanAmount * Math.pow(1 + monthlyInterestRate, -totalPayments);
        double presentValueWithInterest = loanAmount * Math.pow(1 + monthlyInterestRate, -totalPayments + 1) /
                Math.pow(1 + monthlyInterestRate, -totalPayments) - loanAmount * monthlyInterestRate /
                Math.pow(1 + monthlyInterestRate, -totalPayments);
        double payment = (presentValueWithInterest - presentValue) / (monthlyInterestRate / 12);

        double principal1 = payment - loanAmount * interestRate / 360 * (disbursementDate - startDate.getDayOfMonth() + 1);
        double principal2 = payment - (loanAmount - principal1) * interestRate / totalPayments;
        double principal3 = payment - (loanAmount - principal1 - principal2) * interestRate / totalPayments;
        double principal4 = payment - (loanAmount - principal1 - principal2 - principal3) * interestRate / totalPayments;

        double totalPrincipal = principal1 + principal2 + principal3 + principal4;
        double totalInterest = payment * totalPayments - totalPrincipal;

        System.out.println("每期还款额：" + payment);
        System.out.println("第一期本金：" + principal1);
        System.out.println("第二期本金：" + principal2);
        System.out.println("第三期本金：" + principal3);
        System.out.println("第四期本金：" + principal4);
        System.out.println("总本金：" + totalPrincipal);
        System.out.println("总利息：" + totalInterest);
    }
}
