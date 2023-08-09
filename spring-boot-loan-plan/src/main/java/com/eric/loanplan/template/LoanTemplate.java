package com.eric.loanplan.template;

import java.math.BigDecimal;

public abstract class LoanTemplate {
    public abstract void getPerPrincipal(BigDecimal loanAmount, int loanTerm);
    public abstract void getPerRemainingPrincipal(BigDecimal loanAmount, int loanTerm);
    public abstract void getPerInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm);
    public abstract void getPerPrincipalInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm);
    public abstract void getTotalInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm);

}
