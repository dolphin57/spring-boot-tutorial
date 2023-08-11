package com.eric.loanplan.template;

import com.eric.loanplan.datestrategy.DateSplitStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EqualInstallmentLoanTemplate extends LoanTemplate {
    public static final int monthNum = 12;

    private DateSplitStrategy dateSplitStrategy;

    public EqualInstallmentLoanTemplate(DateSplitStrategy dateSplitStrategy) {
        this.dateSplitStrategy = dateSplitStrategy;
    }

    @Override
    public Map<Integer, BigDecimal> getPerPrincipal(BigDecimal loanAmount,BigDecimal annualInterestRate, int loanTerm) {
        double monthInterestRate = annualInterestRate.doubleValue() / monthNum;
        Map<Integer, BigDecimal> perPrincipalMap = new HashMap<>();

        BigDecimal perMonthPrincipalInterest = loanAmount.multiply(new BigDecimal(monthInterestRate * Math.pow(1 + monthInterestRate, loanTerm)))
                .divide(new BigDecimal(Math.pow(1 + monthInterestRate, loanTerm) - 1), 2, BigDecimal.ROUND_HALF_UP);
        Map<Integer, BigDecimal> mapInterest = getPerInterest(loanAmount, annualInterestRate, loanTerm);
        for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
            perPrincipalMap.put(entry.getKey(), perMonthPrincipalInterest.subtract(entry.getValue()));
        }
        return perPrincipalMap;
    }

    @Override
    public Map<Integer, BigDecimal> getPerRemainingPrincipal(BigDecimal loanAmount, int loanTerm) {
        return null;
    }

    @Override
    public Map<Integer, BigDecimal> getPerInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm) {
        Map<Integer, BigDecimal> perInterestMap = new HashMap<>();
        double monthInterestRate = annualInterestRate.doubleValue() / monthNum;
        BigDecimal monthInterest;

        for (int i = 1; i <= loanTerm; i++) {
            BigDecimal multiply = loanAmount.multiply(new BigDecimal(monthInterestRate));
            BigDecimal sub = new BigDecimal(Math.pow(1 + monthInterestRate, loanTerm))
                    .subtract(new BigDecimal(Math.pow(1 + monthInterestRate, i - 1)));
            monthInterest = multiply.multiply(sub).divide(new BigDecimal(Math.pow(1 + monthInterestRate, loanTerm) - 1), 2, BigDecimal.ROUND_HALF_UP);
            monthInterest = monthInterest.setScale(2, BigDecimal.ROUND_HALF_UP);
            perInterestMap.put(i, monthInterest);
        }
        return perInterestMap;
    }

    @Override
    public Map<Integer, BigDecimal> getPerPrincipalInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm) {
        double monthInterestRate = annualInterestRate.doubleValue() / monthNum;
        Map<Integer, BigDecimal> perPrincipalInterestMap = new HashMap<>();

        for (int i = 1; i <= loanTerm; i++) {
            BigDecimal perPrincipalInterest = loanAmount.multiply(new BigDecimal(monthInterestRate * Math.pow(1 + monthInterestRate, loanTerm)))
                    .divide(new BigDecimal(Math.pow(1 + monthInterestRate, loanTerm) - 1), 2, BigDecimal.ROUND_UP);
            perPrincipalInterestMap.put(i, perPrincipalInterest);
        }

        return perPrincipalInterestMap;
    }

    @Override
    public BigDecimal getTotalInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm) {
        BigDecimal totalInterest = new BigDecimal(0);

        Map<Integer, BigDecimal> mapInterest = getPerInterest(loanAmount, annualInterestRate, loanTerm);
        for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
            totalInterest = totalInterest.add(entry.getValue());
        }
        return totalInterest;
    }

    @Override
    public List<LocalDate> splitDate(LocalDate startDate, LocalDate endDate, int repaymentDate) {
        return dateSplitStrategy.split(startDate, endDate, repaymentDate);
    }
}
