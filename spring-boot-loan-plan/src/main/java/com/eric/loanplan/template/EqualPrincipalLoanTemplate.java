package com.eric.loanplan.template;

import com.eric.loanplan.datestrategy.DateSplitStrategy;
import com.eric.loanplan.intereststrategy.InterestCalculationStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 等额本金还款方式
 */
public class EqualPrincipalLoanTemplate extends LoanTemplate {

    private DateSplitStrategy dateSplitStrategy;

    public EqualPrincipalLoanTemplate(DateSplitStrategy dateSplitStrategy) {
        this.dateSplitStrategy = dateSplitStrategy;
    }

    @Override
    public Map<Integer, BigDecimal> getPerPrincipal(BigDecimal loanAmount,BigDecimal annualInterestRate, int loanTerm) {
        Map<Integer, BigDecimal> principalMap = new HashMap<>();

        for (int i = 1; i <= loanTerm; i++) {
            BigDecimal monthIncome = loanAmount.divide(new BigDecimal(loanTerm), 2, BigDecimal.ROUND_HALF_UP);
            principalMap.put(i, monthIncome);
            if (i == loanTerm) {
                BigDecimal lastAmount = loanAmount.subtract(monthIncome.multiply(BigDecimal.valueOf(loanTerm - 1)));
                principalMap.put(i, lastAmount);
            }
        }

        return principalMap;
    }

    @Override
    public Map<Integer, BigDecimal> getPerRemainingPrincipal(BigDecimal loanAmount, int loanTerm) {
        Map<Integer, BigDecimal> remainingPrincipalMap = new HashMap<>();
        for (int i = 1; i <= loanTerm; i++) {
            BigDecimal repaiedPrincipal = loanAmount.multiply(new BigDecimal(i - 1))
                    .divide(BigDecimal.valueOf(loanTerm), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal remainingPrincipal = loanAmount.subtract(repaiedPrincipal);
            remainingPrincipalMap.put(i, remainingPrincipal);
        }
        return remainingPrincipalMap;
    }

    @Override
    public Map<Integer, BigDecimal> getPerInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm) {
        Map<Integer, BigDecimal> interestMap = new HashMap<>();

        for (int i = 1; i <= loanTerm; i++) {
            BigDecimal repaiedPrincipal = loanAmount.multiply(new BigDecimal(i - 1))
                    .divide(BigDecimal.valueOf(loanTerm), 6, BigDecimal.ROUND_HALF_UP);
            BigDecimal remainingPrincipal = loanAmount.subtract(repaiedPrincipal);
            BigDecimal monthlyInterest = remainingPrincipal.multiply(annualInterestRate)
                    .divide(new BigDecimal(1200), 2,BigDecimal.ROUND_HALF_UP);
            interestMap.put(i, monthlyInterest);
        }
        return interestMap;
    }

    @Override
    public Map<Integer, BigDecimal> getPerPrincipalInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm) {
        Map<Integer, BigDecimal> principalInterestMap = new HashMap<>();

        // todo 存在小数位数问题
        Map<Integer, BigDecimal> monthlyPrincipal = getPerPrincipal(loanAmount,annualInterestRate, loanTerm);
        Map<Integer, BigDecimal> monthInterest = getPerInterest(loanAmount, annualInterestRate, loanTerm);
        for (Map.Entry<Integer, BigDecimal> entry : monthlyPrincipal.entrySet()) {
            principalInterestMap.put(entry.getKey(), entry.getValue().add(monthInterest.get(entry.getKey())));
        }
        return principalInterestMap;
    }

    @Override
    public BigDecimal getTotalInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm) {
        BigDecimal totalInterest = new BigDecimal(0);
        Map<Integer, BigDecimal> mapInterest = getPerInterest(loanAmount, annualInterestRate, loanTerm);
        for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
            totalInterest = totalInterest.add(entry.getValue());
        }
        return totalInterest.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public List<LocalDate> splitDate(LocalDate startDate, LocalDate endDate, int repaymentDate) {
        return dateSplitStrategy.split(startDate, endDate, repaymentDate);
    }
}
