package com.eric.loanplan.template;

import com.eric.loanplan.datestrategy.DateSplitStrategy;
import com.eric.loanplan.intereststrategy.InterestCalculationStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterestFirstLoanTemplate extends LoanTemplate {
    public static final int monthNum = 12;

    private InterestCalculationStrategy interestCalculationStrategy;

    private DateSplitStrategy dateSplitStrategy;

    public InterestFirstLoanTemplate(InterestCalculationStrategy interestCalculationStrategy, DateSplitStrategy dateSplitStrategy) {
        this.interestCalculationStrategy = interestCalculationStrategy;
        this.dateSplitStrategy = dateSplitStrategy;
    }

    @Override
    public List<LocalDate> splitDate(LocalDate startDate, LocalDate endDate, int repaymentDate) {
        return dateSplitStrategy.split(startDate, endDate, repaymentDate);
    }

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
//        List<LocalDate> dateList = dateSplitStrategy.split(startDate, endDate, repaymentDate);
//        int loanTerm = dateList.size();
        Map<Integer, BigDecimal> integerMap = interestCalculationStrategy.calculateInterest(loanAmount, annualInterestRate, loanTerm);
        return integerMap;
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
