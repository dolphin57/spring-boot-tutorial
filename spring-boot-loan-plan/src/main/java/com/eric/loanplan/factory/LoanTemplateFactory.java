//package com.eric.loanplan.factory;
//
//import com.eric.loanplan.template.EqualPrincipalLoanTemplate;
//import com.eric.loanplan.template.InterestFirstLoanTemplate;
//import com.eric.loanplan.template.LoanTemplate;
//
//public class LoanTemplateFactory {
//    public LoanTemplate createLoanTemplate(RepaymentType type) {
//        switch (type) {
//            case EQUAL_INSTALLMENT:
//                return new EqualPrincipalLoanTemplate();
//            case EQUAL_PRINCIPAL:
//                return new EqualPrincipalLoanTemplate();
//            case INTEREST_FIRST:
//                return new InterestFirstLoanTemplate();
//            default:
//                throw new IllegalArgumentException("Invalid repayment type");
//        }
//    }
//}
