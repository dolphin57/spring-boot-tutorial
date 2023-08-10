package com.eric.loanplan.intereststrategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class QuarterInterestStrategy implements InterestCalculationStrategy {
    @Override
    public Map<Integer, BigDecimal> calculateInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm) {
        Map<Integer, BigDecimal> perInterestMap = new HashMap<>();

        for (int i = 1; i <= loanTerm; i++) {
            BigDecimal perInterest = loanAmount.multiply(annualInterestRate).divide(BigDecimal.valueOf(4), 2, BigDecimal.ROUND_HALF_UP);
            perInterestMap.put(i, perInterest);
        }
        return perInterestMap;
    }
}
