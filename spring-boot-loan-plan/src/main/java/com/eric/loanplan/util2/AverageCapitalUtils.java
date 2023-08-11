package com.eric.loanplan.util2;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 等额本金还款方式
 */
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
    public static Map<String, Object> getAverageCapitalAll(BigDecimal invest1, BigDecimal yearRate1, BigDecimal invest2, BigDecimal yearRate2, int totalYear) {
        int totalMonth = totalYear * monthNum;
        Map<String, Object> map = new HashMap<>();

        // 公积金首月 成本和利息
        BigDecimal benxi1 = getPerMonthPrincipalInterest(invest1, yearRate1, totalMonth).get(1);
        BigDecimal lixicount1 = getInterestCount(invest1, yearRate1, totalMonth);

        //商贷首月成本+利息
        BigDecimal benxi2 = getPerMonthPrincipalInterest(invest2, yearRate2, totalMonth).get(1);
        //总利息
        BigDecimal lixicount2 = getInterestCount(invest2, yearRate2, totalMonth);

        DecimalFormat df = new DecimalFormat("#.00");
        BigDecimal allCount = invest1.add(lixicount1).add(lixicount2).add(invest2);

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
    public static Map<String, Object> getAverageCapital(BigDecimal loanAmount, BigDecimal annualInterestRate, int loanTern) {
        int totalMonth = loanTern * monthNum;
        Map<String, Object> result = new HashMap<>();

        // 明细计算
        Map<Integer, BigDecimal> monthlyPrincipal = getPerMonthPrincipal(loanAmount, totalMonth);
        System.out.println("等额本金---每月本金:" + monthlyPrincipal);

        Map<Integer, BigDecimal> perMonthRemainingPrincipal = getPerMonthRemainingPrincipal(loanAmount, totalMonth);
        System.out.println("等额本金---每月剩余本金:" + perMonthRemainingPrincipal);

        Map<Integer, BigDecimal> mapInterest = getPerMonthInterest(loanAmount, annualInterestRate, totalMonth);
        System.out.println("等额本金---每月利息:" + mapInterest);

        Map<Integer, BigDecimal> monthlyPrincipalInterest = getPerMonthPrincipalInterest(loanAmount, annualInterestRate, totalMonth);
        System.out.println("等额本金---每月本息：" + monthlyPrincipalInterest);

        // 汇总计算
        BigDecimal interestCount = getInterestCount(loanAmount, annualInterestRate, totalMonth);
        System.out.println("等额本金---总利息：" + interestCount);

        BigDecimal totalPrincipalInterest = loanAmount.add(interestCount);
        System.out.println("等额本金---本息合计:" + totalPrincipalInterest);

        System.out.println("---------------");

        result.put("allMonth", totalMonth);// 总月数
        result.put("benxi", monthlyPrincipalInterest.get(1));// 首月成本+利息
        result.put("lixicount", interestCount);// 总利息
        result.put("allcount", totalPrincipalInterest);// 合计

        return result;
    }

    /**
     * 等额本金计算获取还款方式为等额本金的每月偿还本金
     * 公式：每月应还本金=贷款本金÷还款月数
     * 最后一个月用减法
     *
     * @param loanAmount 总借款额（贷款本金）
     * @param totalMonth 还款总月数
     * @return
     */
    private static Map<Integer, BigDecimal> getPerMonthPrincipal(BigDecimal loanAmount, int totalMonth) {
        Map<Integer, BigDecimal> principalMap = new HashMap<>();

        for (int i = 1; i <= totalMonth; i++) {
            BigDecimal monthIncome = loanAmount.divide(new BigDecimal(totalMonth), 2, BigDecimal.ROUND_HALF_UP);
            principalMap.put(i, monthIncome);
            if (i == totalMonth) {
                BigDecimal lastAmount = loanAmount.subtract(monthIncome.multiply(BigDecimal.valueOf(totalMonth - 1)));
                principalMap.put(i, lastAmount);
            }
        }

        return principalMap;
    }

    private static Map<Integer, BigDecimal> getPerMonthRemainingPrincipal(BigDecimal loanAmount, int totalMonth) {
        Map<Integer, BigDecimal> remainingPrincipalMap = new HashMap<>();
        for (int i = 1; i <= totalMonth; i++) {
            BigDecimal repaiedPrincipal = loanAmount.multiply(new BigDecimal(i - 1))
                    .divide(BigDecimal.valueOf(totalMonth), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal remainingPrincipal = loanAmount.subtract(repaiedPrincipal);
            remainingPrincipalMap.put(i, remainingPrincipal);
        }
        return remainingPrincipalMap;
    }

    /**
     * 等额本金计算获取的每月偿还利息
     * 公式：每月应还利息=剩余本金×月利率=(贷款本金-已归还本金累计额)×月利率
     *
     * 推导：
     *   [loanAmount - loanAmount * (i-1)/totalMonth]*月利率 = loanAmount * (121-i)/120 * rate
     *
     * @param loanAmount
     * @param annualInterestRate
     * @param totalMonth
     * @return 每月偿还利息
     */
    private static Map<Integer, BigDecimal> getPerMonthInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int totalMonth) {
        BigDecimal monthInterestRate = annualInterestRate.divide(BigDecimal.valueOf(monthNum), 6, BigDecimal.ROUND_HALF_UP); // 获取月利率
        Map<Integer, BigDecimal> interestMap = new HashMap<>();

        for (int i = 1; i <= totalMonth; i++) {
            BigDecimal repaiedPrincipal = loanAmount.multiply(new BigDecimal(i - 1))
                    .divide(BigDecimal.valueOf(totalMonth), 6, BigDecimal.ROUND_HALF_UP);
            BigDecimal remainingPrincipal = loanAmount.subtract(repaiedPrincipal);
            BigDecimal monthlyInterest = remainingPrincipal.multiply(annualInterestRate)
                    .divide(new BigDecimal(1200), 2,BigDecimal.ROUND_HALF_UP);
            interestMap.put(i, monthlyInterest);
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
    public static Map<Integer, BigDecimal> getPerMonthPrincipalInterest(BigDecimal loanAmount, BigDecimal annualInterestRate, int totalMonth) {
        Map<Integer, BigDecimal> principalInterestMap = new HashMap<>();

        // todo 存在小数位数问题
        Map<Integer, BigDecimal> monthlyPrincipal = getPerMonthPrincipal(loanAmount, totalMonth);
        Map<Integer, BigDecimal> monthInterest = getPerMonthInterest(loanAmount, annualInterestRate, totalMonth);
        for (Map.Entry<Integer, BigDecimal> entry : monthlyPrincipal.entrySet()) {
            principalInterestMap.put(entry.getKey(), entry.getValue().add(monthInterest.get(entry.getKey())));
        }
        return principalInterestMap;
    }

    /**
     * 等额本金计算的总利息
     *
     * @param loanAmount
     * @param annualInterestRate
     * @param totalMonth
     * @return
     */
    private static BigDecimal getInterestCount(BigDecimal loanAmount, BigDecimal annualInterestRate, int totalMonth) {
        BigDecimal totalInterest = new BigDecimal(0);
        Map<Integer, BigDecimal> mapInterest = getPerMonthInterest(loanAmount, annualInterestRate, totalMonth);
        for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
            totalInterest = totalInterest.add(entry.getValue());
        }
        return totalInterest.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public static void main(String[] args) {
//        Map<String, Object> capitalAll = getAverageCapitalAll(500000, 0.0325, 500000, 0.049, 10);
//        System.out.println(capitalAll);

        BigDecimal invest = BigDecimal.valueOf(1000000); // 本金
        BigDecimal yearRate = BigDecimal.valueOf(4.9); // 年利率
        int totalMonth = 10;
        Map<String, Object> result = getAverageCapital(invest, yearRate, totalMonth);
        System.out.println(result);

        // todo 利率按6位小数
        // todo 最后一个月的还款额用减法
        // todo 时间切分，开始日期、固定还款日
        // todo 增加剩余本金
        // todo 首期最小天数是参数，首月的还款额根据天数
    }
}
