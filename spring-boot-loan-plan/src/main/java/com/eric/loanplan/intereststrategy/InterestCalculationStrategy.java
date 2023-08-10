package com.eric.loanplan.intereststrategy;

import java.math.BigDecimal;
import java.util.Map;

public interface InterestCalculationStrategy {
    Map<Integer, BigDecimal> calculateInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm);
}
