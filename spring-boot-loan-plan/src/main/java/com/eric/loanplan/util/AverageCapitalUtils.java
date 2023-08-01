package com.eric.loanplan.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class AverageCapitalUtils {
    public static final int monthNum = 12;

    /**
     * 等额本金组合贷
     *
     * @param invest1   公积金金额
     * @param yearRate1 公积金年利率
     * @param invest2   商贷金额
     * @param yearRate2 商贷年利率
     * @param totalYear 贷款年限
     * @return
     */
    public static Map<String, Object> getAverageCapitalAll(double invest1, double yearRate1, double invest2, double yearRate2, int totalYear) {
        int totalMonth = totalYear * monthNum;
        Map<String, Object> map = new HashMap<>();

        // 公积金首月 成本和利息
        BigDecimal benxi1 = BigDecimal.valueOf(getPerMonthPrincipalInterest(invest1, yearRate1, totalMonth).get(1));
        BigDecimal lixicount1 = getInterestCount(invest1, yearRate1, totalMonth);

        //商贷首月成本+利息
        BigDecimal benxi2 = BigDecimal.valueOf(getPerMonthPrincipalInterest(invest2, yearRate2, totalMonth).get(1));
        //总利息
        BigDecimal lixicount2 = getInterestCount(invest2, yearRate2, totalMonth);

        DecimalFormat df = new DecimalFormat("#.00");
        BigDecimal allCount = BigDecimal.valueOf(invest1).add(lixicount1).add(lixicount2).add(BigDecimal.valueOf(invest2));

        map.put("allMonth", totalMonth);//总月数
        map.put("benxi", df.format(benxi1.add(benxi2)));//成本+利息
        map.put("lixicount", df.format(lixicount1.add(lixicount2)));//总利息
        map.put("allcount", df.format(allCount));//本息合计
        return map;
    }

    /**
     * 等额本金商贷或纯公积金
     *
     * @param loanAmount         贷款金额
     * @param annualInterestRate 贷款年利率
     * @param loanTern
     * @return
     */
    public static Map<String, Object> getAverageCapital(double loanAmount, double annualInterestRate, int loanTern) {
        int totalMonth = loanTern * monthNum;
        Map<String, Object> result = new HashMap<>();

        BigDecimal monthlyPrincipal = getPerMonthPrincipal(loanAmount, totalMonth);
        Map<Integer, Double> monthlyPrincipalInterest = getPerMonthPrincipalInterest(loanAmount, annualInterestRate, totalMonth);
        Map<Integer, Double> mapInterest = getPerMonthInterest(loanAmount, annualInterestRate, totalMonth);
        BigDecimal interestCount = getInterestCount(loanAmount, annualInterestRate, totalMonth);
        BigDecimal totalPrincipalInterest = BigDecimal.valueOf(loanAmount).add(interestCount);

        result.put("allMonth", totalMonth);//总月数
        result.put("benxi", monthlyPrincipalInterest.get(1));//首月成本+利息
//        result.put("benjin", benjin);//每月还款本金
//        result.put("firstMapInterest", mapInterest.get(1));//首月利息
        result.put("lixicount", interestCount);//总利息
        result.put("allcount", totalPrincipalInterest);//合计

        System.out.println("等额本金---每月本息：" + monthlyPrincipalInterest);
        System.out.println("等额本金---首月本息:" + monthlyPrincipalInterest.get(1));
        System.out.println("等额本金---每月本金:" + monthlyPrincipal);
        System.out.println("等额本金---每月利息:" + mapInterest);
        System.out.println("等额本金---首月利息:" + mapInterest.get(1));
        System.out.println("等额本金---总利息：" + interestCount);
        System.out.println("等额本金---本息合计:" + totalPrincipalInterest);
        System.out.println("---------------");

        return result;
    }

    /**
     * 等额本金计算获取还款方式为等额本金的每月偿还本金
     * 公式：每月应还本金=贷款本金÷还款月数
     *
     * @param loanAmount 总借款额（贷款本金）
     * @param totalMonth 还款总月数
     * @return
     */
    private static BigDecimal getPerMonthPrincipal(double loanAmount, int totalMonth) {
        BigDecimal monthIncome = new BigDecimal(loanAmount).divide(new BigDecimal(totalMonth), 2, BigDecimal.ROUND_HALF_UP);
        return monthIncome;
    }

    /**
     * 等额本金计算获取的每月偿还利息
     * 公式：每月应还利息=剩余本金×月利率=(贷款本金-已归还本金累计额)×月利率
     *
     * 等额本金公式推导
     *      设贷款总额为a,月利率为b,分期数为m,每月还款额(本金+利息)设为X,则：
     *      第1个月的利息为：(a-a/m*0)*b, 本金为a/m
     *      第2个月的利息为：(a-a/m*1)*b, 本金为a/m
     *      继续推导下去,可以得出....
     *      第个n月的利息为：[a-a/m*(n-1)]*b, 本金为a/m
     *
     * @param loanAmount
     * @param annualInterestRate
     * @param totalMonth
     * @return 每月偿还利息
     */
    private static Map<Integer, Double> getPerMonthInterest(double loanAmount, double annualInterestRate, int totalMonth) {
        double monthInterestRate = annualInterestRate / monthNum; // 获取月利率
        Map<Integer, Double> interestMap = new HashMap<>();

        for (int i = 1; i <= totalMonth; i++) {
            BigDecimal cumulativeReturnAmount = new BigDecimal(loanAmount).divide(new BigDecimal(totalMonth), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(i - 1));
            BigDecimal monthlyInterest = new BigDecimal(loanAmount).subtract(cumulativeReturnAmount).multiply(new BigDecimal(monthInterestRate))
                    .setScale(4, BigDecimal.ROUND_HALF_UP);
            interestMap.put(i, monthlyInterest.doubleValue());
        }

//        double monthlyPrincipal = getPerMonthPrincipal(loanAmount, totalMonth).doubleValue();
//        Map<Integer, Double> map = getPerMonthPrincipalInterest(loanAmount, annualInterestRate, totalMonth);
//        for (Map.Entry<Integer, Double> entry : map.entrySet()) {
//            BigDecimal principalBigDecimal = new BigDecimal(monthlyPrincipal);
//            BigDecimal principalInterestBigDecimal = new BigDecimal(entry.getValue());
//            BigDecimal interestBigDecimal = principalInterestBigDecimal.subtract(principalBigDecimal);
//            interestBigDecimal = interestBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
//            interestMap.put(entry.getKey(), interestBigDecimal.doubleValue());
//        }
        return interestMap;
    }

    /**
     * 等额本金计算获取每月偿还本金和利息
     * 公式：每月偿还本息=(贷款本金/还款月数)+(贷款本金-已归还本金累计额)×月利率
     *
     * @param loanAmount         总借款额（贷款本金）
     * @param annualInterestRate 年利率
     * @param totalMonth         还款总月数
     * @return 每月偿还本金和利息, 不四舍五入，直接截取小数点最后两位
     */
    public static Map<Integer, Double> getPerMonthPrincipalInterest(double loanAmount, double annualInterestRate, int totalMonth) {
        Map<Integer, Double> map = new HashMap<>();

        BigDecimal monthlyPrincipal = getPerMonthPrincipal(loanAmount, totalMonth);
        for (int i = 1; i <= totalMonth; i++) {
            BigDecimal cumulativeReturnAmount = new BigDecimal(loanAmount).divide(new BigDecimal(totalMonth), 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(i - 1));
            BigDecimal monthlyInterest = new BigDecimal(loanAmount).subtract(cumulativeReturnAmount).multiply(new BigDecimal(annualInterestRate))
                    .divide(BigDecimal.valueOf(monthNum),2,BigDecimal.ROUND_HALF_UP);
            BigDecimal monthPrincipalInterest = monthlyPrincipal.add(monthlyInterest).setScale(2, BigDecimal.ROUND_HALF_UP);
            map.put(i, monthPrincipalInterest.doubleValue());
        }
        return map;
    }

    /**
     * 等额本金计算的总利息
     *
     * @param loanAmount
     * @param annualInterestRate
     * @param totalMonth
     * @return
     */
    private static BigDecimal getInterestCount(double loanAmount, double annualInterestRate, int totalMonth) {
        BigDecimal totalInterest = new BigDecimal(0);
        Map<Integer, Double> mapInterest = getPerMonthInterest(loanAmount, annualInterestRate, totalMonth);
        for (Map.Entry<Integer, Double> entry : mapInterest.entrySet()) {
            totalInterest = totalInterest.add(new BigDecimal(entry.getValue()));
        }
        return totalInterest.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public static void main(String[] args) {
//        Map<String, Object> capitalAll = getAverageCapitalAll(500000, 0.0325, 500000, 0.049, 10);
//        System.out.println(capitalAll);

        double invest = 1000000; // 本金
        int totalMonth = 10;
        double yearRate = 0.049; // 年利率
        Map<String, Object> result = getAverageCapital(invest, yearRate, totalMonth);
        System.out.println(result);
    }
}
