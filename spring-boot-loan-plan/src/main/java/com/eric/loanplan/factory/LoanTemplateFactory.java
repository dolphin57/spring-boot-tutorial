package com.eric.loanplan.factory;

import com.eric.loanplan.template.EqualPrincipalLoanTemplate;
import com.eric.loanplan.template.InterestFirstLoanTemplate;
import com.eric.loanplan.template.LoanTemplate;

public class LoanTemplateFactory {
    public LoanTemplate createLoanTemplate(RepaymentType type) {
//        switch (type) {
//            case EQUAL_INSTALLMENT:
//                return new EqualPrincipalLoanTemplate();
//            case EQUAL_PRINCIPAL:
//                return new EqualPrincipalLoanTemplate();
//            case INTEREST_FIRST:
//                return new InterestFirstLoanTemplate();
//            default:
//                throw new IllegalArgumentException("Invalid repayment type");
        return null;
    }

    public static double calculateX(double loanAmount, double annualInterestRate, int numberOfPeriods) {
        double monthlyInterestRate = annualInterestRate / 12;
        double remainingLoanAmount = loanAmount;
        double X = 0;

        for (int i = 1; i <= numberOfPeriods; i++) {
            double currentPeriodPrincipal = remainingLoanAmount - (X + monthlyInterestRate * (loanAmount - X));
            X += currentPeriodPrincipal;
            remainingLoanAmount -= currentPeriodPrincipal;
        }

        return X;
    }

    public static void main(String[] args) {
        double loanAmount = 466183.62; // 贷款金额
        double interestRate = 0.047/12; // 利率
        int days = 2; // 天数
        int N = 6; // N的值

        double X = calculateX(loanAmount, interestRate, days, N);
        System.out.println("X 的值为：" + X);
    }

    public static double calculateX(double loanAmount, double interestRate, int days, int N) {
        double X = loanAmount;
        for (int i = 1; i <= N; i++) {
            double principal = X - (loanAmount - calculateTotalPrincipal(X, interestRate, i, N)) * interestRate * (days / 30.0);
            X = principal;
        }
        return X;
    }

    public static double calculateTotalPrincipal(double X, double interestRate, int currentPeriod, int N) {
        double totalPrincipal = 0;
        for (int i = 1; i < currentPeriod; i++) {
            totalPrincipal += X - totalPrincipal * interestRate;
        }
        return totalPrincipal;
    }
}
