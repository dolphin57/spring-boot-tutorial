package com.eric.loanplan.template;

import com.eric.loanplan.intereststrategy.InterestCalculationStrategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class InterestFirstLoanTemplate extends LoanTemplate {
    public static final int monthNum = 12;

    private InterestCalculationStrategy interestCalculationStrategy;

    @Override
    public Map<Integer, BigDecimal> getPerPrincipal(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm) {
        return null;
    }

    @Override
    public Map<Integer, BigDecimal> getPerRemainingPrincipal(BigDecimal loanAmount, int loanTerm) {
        return null;
    }

    @Override
    public Map<Integer, BigDecimal> getPerInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm) {
        // todo 需要处理边界利息, 如首月不足期、
        return null;
    }

    @Override
    public Map<Integer, BigDecimal> getPerPrincipalInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm) {
        Map<Integer, BigDecimal> repaymentMap = new HashMap<>();
        BigDecimal monthlyInterestRate = annualInterestRate.divide(BigDecimal.valueOf(12), 8, BigDecimal.ROUND_HALF_UP);

        Map<Integer, BigDecimal> perInterestMap = getPerInterest(loanAmount, annualInterestRate, loanTerm);

        for (int i = 1; i <= loanTerm; i++) {
            if (i < loanTerm) {
                repaymentMap.put(i, perInterestMap.get(i));
            } else {
                repaymentMap.put(i, loanAmount.add(perInterestMap.get(i)));
            }
        }
        return repaymentMap;
    }

    @Override
    public BigDecimal getTotalInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm) {
        return loanAmount.multiply(annualInterestRate).multiply(new BigDecimal(loanTerm))
                .divide(BigDecimal.valueOf(monthNum), 2, BigDecimal.ROUND_HALF_UP);
    }
}
