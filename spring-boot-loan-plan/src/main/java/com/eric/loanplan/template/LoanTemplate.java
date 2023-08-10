package com.eric.loanplan.template;

import java.math.BigDecimal;
import java.util.Map;

public abstract class LoanTemplate {
    public abstract Map<Integer, BigDecimal> getPerPrincipal(BigDecimal loanAmount,BigDecimal annualInterestRate, int loanTerm);
    public abstract Map<Integer, BigDecimal> getPerRemainingPrincipal(BigDecimal loanAmount, int loanTerm);
    public abstract Map<Integer, BigDecimal> getPerInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm);
    public abstract Map<Integer, BigDecimal> getPerPrincipalInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm);
    public abstract BigDecimal getTotalInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm);

}
