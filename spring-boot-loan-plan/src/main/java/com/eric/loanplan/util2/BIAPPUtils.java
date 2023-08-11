package com.eric.loanplan.util2;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 先息后本
 */
public class BIAPPUtils {
    /**
     * 按月还款利息
     *
     * @param loanAmount          贷款金额
     * @param monthlyInterestRate 贷款月利率
     * @return
     */
    public static BigDecimal monthlyInterest(BigDecimal loanAmount, BigDecimal monthlyInterestRate) {
        return loanAmount.multiply(monthlyInterestRate).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 还款总利息
     *
     * @param loanAmount          贷款金额
     * @param monthlyInterestRate 贷款月利率
     * @param loanTerm
     * @return
     */
    public static BigDecimal totalInterest(BigDecimal loanAmount, BigDecimal monthlyInterestRate, int loanTerm) {
        return loanAmount.multiply(monthlyInterestRate).multiply(new BigDecimal(loanTerm)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 月还款额 每月还款是利息，最后一个月是到期一次性付清
     *
     * @param loanAmount
     * @param annualInterestRate
     * @param loanTerm
     * @return
     */
    public static Map<Integer, BigDecimal> monthlyRepayment(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTerm) {
        Map<Integer, BigDecimal> repaymentMap = new HashMap<>();
        BigDecimal monthlyInterestRate = annualInterestRate.divide(BigDecimal.valueOf(12), 8, BigDecimal.ROUND_HALF_UP);

        BigDecimal monthlyInterest = monthlyInterest(loanAmount, monthlyInterestRate);

        for (int i = 1; i <= loanTerm; i++) {
            if (i < loanTerm) {
                repaymentMap.put(i, monthlyInterest);
            } else {
                repaymentMap.put(i, loanAmount.add(monthlyInterest));
            }
        }
        return repaymentMap;
    }

    public static void main(String[] args) {
        BigDecimal loanAmount = BigDecimal.valueOf(1000000L); //贷款本息
        BigDecimal annualInterestRate = BigDecimal.valueOf(0.049); //年利息
        Integer loanTerm = 12; //贷款期数

        Map<Integer, BigDecimal> monthlyRepayment = monthlyRepayment(loanAmount, annualInterestRate, loanTerm);
        for (Map.Entry<Integer, BigDecimal> entry : monthlyRepayment.entrySet()) {
            System.out.println("期数: " + entry.getKey() + " 还款额：" + entry.getValue());
        }

        // 首期不足月，按天数日利率处理，足月
        // 最后一期加利息
    }
}
