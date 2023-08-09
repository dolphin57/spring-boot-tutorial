package com.eric.loanplan;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 等额本息还款方式
 *  等额本息还款, 即借款人每月按相等的金额偿还贷款本息，其中每月贷款利息按月初剩余贷款本金计算并逐月结清。把按揭贷款的本金总额与利息总额相加，
 *  然后平均分摊到还款期限的每个月中。作为还款人，每个月还给银行固定金额，但每月还款额中的本金比重逐月递增、利息比重逐月递减。
 *
 *  年利率＝月利率×12（月）＝日利率×360（天）
 *
 */
public class AveragePrincipalPlusInterestUtils {
    public static final int monthNum = 12;

    /**
     * 等额本息商贷
     *
     * @param loanAmount         总金额
     * @param annualInterestRate 年利率
     * @param loanTerm           贷款年限
     * @return
     */
    public static Map<String, Object> getAveragePrincipalInterest(double loanAmount, double annualInterestRate, int loanTerm) {
        int totalMonth = loanTerm * monthNum;
        Map<String, Object> result = new HashMap<String, Object>();

        BigDecimal perMonthPrincipalInterest = getPerMonthPrincipalInterest(loanAmount, annualInterestRate, totalMonth);
        Map<Integer, BigDecimal> monthlyInterest = getPerMonthInterest(loanAmount, annualInterestRate, totalMonth);
        Map<Integer, BigDecimal> mapPrincipal = getPerMonthPrincipal(loanAmount, annualInterestRate, totalMonth);
        BigDecimal totalInterest = getInterestCount(loanAmount, annualInterestRate, totalMonth);
        BigDecimal totalPrincipalInterest = getPrincipalInterestCount(loanAmount, annualInterestRate, totalMonth);

        result.put("allMonth", totalMonth);//总月数
        result.put("principalInterest", perMonthPrincipalInterest);//成本+利息
//        result.put("benjin", mapPrincipal.get(1));//首月本金
//        result.put("firstMapInterest", mapInterest.get(1));//首月利息
        result.put("totalInterest", totalInterest);//总利息
        result.put("allcount", totalPrincipalInterest);//本息合计

        System.out.println("等额本息---每月本息：" + perMonthPrincipalInterest);
        System.out.println("等额本息---首月本金:" + mapPrincipal.get(1));
        System.out.println("等额本息---每月本金：" + mapPrincipal);
        System.out.println("等额本息---首月利息：" + monthlyInterest.get(1));
        System.out.println("等额本息---每月利息：" + monthlyInterest);
        System.out.println("等额本息---总利息：" + totalInterest);
        System.out.println("等额本息---本息合计：" + totalPrincipalInterest);
        return result;
    }

    public static Map<String, Object> getAveragePrincipalInterestByMonth(double loanAmount, double annualInterestRate, int totalMonth) {
        Map<String, Object> result = new HashMap<String, Object>();

        BigDecimal perMonthPrincipalInterest = getPerMonthPrincipalInterest(loanAmount, annualInterestRate, totalMonth);
        Map<Integer, BigDecimal> monthlyInterest = getPerMonthInterest(loanAmount, annualInterestRate, totalMonth);
        Map<Integer, BigDecimal> mapPrincipal = getPerMonthPrincipal(loanAmount, annualInterestRate, totalMonth);
        BigDecimal totalInterest = getInterestCount(loanAmount, annualInterestRate, totalMonth);
        BigDecimal totalPrincipalInterest = getPrincipalInterestCount(loanAmount, annualInterestRate, totalMonth);

        result.put("allMonth", totalMonth);//总月数
        result.put("principalInterest", perMonthPrincipalInterest);//成本+利息
//        result.put("benjin", mapPrincipal.get(1));//首月本金
//        result.put("firstMapInterest", mapInterest.get(1));//首月利息
        result.put("totalInterest", totalInterest);//总利息
        result.put("allcount", totalPrincipalInterest);//本息合计

        System.out.println("等额本息---每月本息：" + perMonthPrincipalInterest);
        System.out.println("等额本息---首月本金:" + mapPrincipal.get(1));
        System.out.println("等额本息---每月本金：" + mapPrincipal);
        System.out.println("等额本息---首月利息：" + monthlyInterest.get(1));
        System.out.println("等额本息---每月利息：" + monthlyInterest);
        System.out.println("等额本息---总利息：" + totalInterest);
        System.out.println("等额本息---本息合计：" + totalPrincipalInterest);
        return result;
    }

    /**
     * 等额本息组合贷
     *
     * @param reserveFundAmount    公积金金额
     * @param reserveFundRate      公积金年利率
     * @param commercialLoanAmount 商贷金额
     * @param commercialLoanRate   商贷年利率
     * @param totalYear            贷款年限
     * @return
     */
    public static Map<String, Object> getAveragePrincipalInterestAll(double reserveFundAmount, double reserveFundRate,
                                                                     double commercialLoanAmount, double commercialLoanRate, int totalYear) {
        int totalMonth = totalYear * monthNum;
        Map<String, Object> map = new HashMap<>();

        BigDecimal reserveFundPrincipalInterest = getPerMonthPrincipalInterest(reserveFundAmount, reserveFundRate, totalMonth);
        BigDecimal totalreserveFundInterest = getInterestCount(reserveFundAmount, reserveFundRate, totalMonth);
        BigDecimal totalreserveFundPrincipalInterest = getPrincipalInterestCount(reserveFundAmount, reserveFundRate, totalMonth);

        BigDecimal commercialLoanPrincipalInterest = getPerMonthPrincipalInterest(commercialLoanAmount, commercialLoanRate, totalMonth);
        BigDecimal totalcommercialLoanInterest = getInterestCount(commercialLoanAmount, commercialLoanRate, totalMonth);
        BigDecimal totalcommercialLoanPrincipalInterest = getPrincipalInterestCount(commercialLoanAmount, commercialLoanRate, totalMonth);

        DecimalFormat df = new DecimalFormat("#.00");
        map.put("allMonth", totalMonth);
        map.put("PrincipalInterest", df.format(reserveFundPrincipalInterest.setScale(2, BigDecimal.ROUND_HALF_UP)
                .add(commercialLoanPrincipalInterest.setScale(2, BigDecimal.ROUND_HALF_UP))));
        map.put("totalInterest", df.format(totalreserveFundInterest.add(totalcommercialLoanInterest)));
        map.put("totalPrincipalInterest", df.format(totalreserveFundPrincipalInterest.add(totalcommercialLoanPrincipalInterest)));
        return map;
    }

    /**
     * 等额本息计算每月偿还本金和利息
     * 公式：每月偿还本息=(贷款本金×月利率×(1＋月利率))^还款月数)/(1＋月利率)^还款月数-1)

     * @param loanAmount         总借款额/贷款本金
     * @param annualInterestRate 年利率
     * @param totalMonth         还款总月数
     * @return 每月偿还本金和利息, 不四舍五入，直接截取小数点最后两位
     */
    private static BigDecimal getPerMonthPrincipalInterest(double loanAmount, double annualInterestRate, int totalMonth) {
        double monthInterestRate = annualInterestRate / monthNum;
        BigDecimal monthIncome = new BigDecimal(loanAmount)
                .multiply(new BigDecimal(monthInterestRate * Math.pow(1 + monthInterestRate, totalMonth)))
                .divide(new BigDecimal(Math.pow(1 + monthInterestRate, totalMonth) - 1), 2, BigDecimal.ROUND_UP);
        return monthIncome;
    }

    /**
     * 等额本息计算每月偿还利息
     * 公式：每月偿还利息=贷款本金×月利率×((1+月利率)^还款月数-(1+月利率)^(还款月序号-1)) / ((1+月利率)^还款月数-1)
     *
     * @param loanAmount         总借款额/贷款本金
     * @param annualInterestRate 年利率
     * @param totalMonth         还款总月数
     * @return 每月偿还利息
     */
    private static Map<Integer, BigDecimal> getPerMonthInterest(double loanAmount, double annualInterestRate, int totalMonth) {
        Map<Integer, BigDecimal> monthInterestMap = new HashMap<>();
        double monthInterestRate = annualInterestRate / monthNum;
        BigDecimal monthInterest;

        for (int i = 1; i < totalMonth + 1; i++) {
            BigDecimal multiply = new BigDecimal(loanAmount).multiply(new BigDecimal(monthInterestRate));
            BigDecimal sub = new BigDecimal(Math.pow(1 + monthInterestRate, totalMonth))
                    .subtract(new BigDecimal(Math.pow(1 + monthInterestRate, i - 1)));
            monthInterest = multiply.multiply(sub).divide(new BigDecimal(Math.pow(1 + monthInterestRate, totalMonth) - 1), 2, BigDecimal.ROUND_HALF_UP);
            monthInterest = monthInterest.setScale(2, BigDecimal.ROUND_HALF_UP);
            monthInterestMap.put(i, monthInterest);
        }
        return monthInterestMap;
    }

    /**
     * 等额本息计算每月偿还本金
     *
     * @param loanAmount         总借款额（贷款本金）
     * @param annualInterestRate 年利率
     * @param totalMonth         还款总月数
     * @return 每月偿还本金
     */
    private static Map<Integer, BigDecimal> getPerMonthPrincipal(double loanAmount, double annualInterestRate, int totalMonth) {
        double monthInterestRate = annualInterestRate / monthNum;
        Map<Integer, BigDecimal> perMonthPrincipal = new HashMap<>();

        BigDecimal perMonthPrincipalInterest = new BigDecimal(loanAmount)
                .multiply(new BigDecimal(monthInterestRate * Math.pow(1 + monthInterestRate, totalMonth)))
                .divide(new BigDecimal(Math.pow(1 + monthInterestRate, totalMonth) - 1), 2, BigDecimal.ROUND_HALF_UP);
        Map<Integer, BigDecimal> mapInterest = getPerMonthInterest(loanAmount, annualInterestRate, totalMonth);
        for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
            perMonthPrincipal.put(entry.getKey(), perMonthPrincipalInterest.subtract(entry.getValue()));
        }
        return perMonthPrincipal;
    }

    /**
     * 等额本息应还本息总计
     *
     * @param loanAmount
     * @param annualInterestRate
     * @param totalMonth
     * @return
     */
    private static BigDecimal getPrincipalInterestCount(double loanAmount, double annualInterestRate, int totalMonth) {
        double monthInterestRate = annualInterestRate / monthNum;
        BigDecimal perMonthPrincipalInterest = new BigDecimal(loanAmount)
                .multiply(new BigDecimal(monthInterestRate * Math.pow(1 + monthInterestRate, totalMonth)))
                .divide(new BigDecimal(Math.pow(1 + monthInterestRate, totalMonth) - 1), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal principalInterestAll = perMonthPrincipalInterest.multiply(new BigDecimal(totalMonth));
        principalInterestAll = principalInterestAll.setScale(2, BigDecimal.ROUND_HALF_UP);
        return principalInterestAll;
    }

    /**
     * 等额本息计算的总利息
     *
     * @param loanAmount
     * @param annualInterestRate
     * @param totalMonth
     * @return
     */
    private static BigDecimal getInterestCount(double loanAmount, double annualInterestRate, int totalMonth) {
        BigDecimal interestAll = new BigDecimal(0);
        Map<Integer, BigDecimal> mapInterest = getPerMonthInterest(loanAmount, annualInterestRate, totalMonth);
        for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
            interestAll = interestAll.add(entry.getValue());
        }
        return interestAll;
    }

    /**
     * 等额本息还款，也称定期付息，即借款人每月按相等的金额偿还贷款本息
     * 其中每月贷款利息按月初剩余贷款本金计算并逐月结清。把按揭贷款的本金总额与利息总额相加，然后平均分摊到还款期限的每个月中
     * 作为还款人，每个月还给银行固定金额，但每月还款额中的本金比重逐月递增、利息比重逐月递减。
     *
     * @param args
     */
    public static void main(String[] args) {
        double loanAmount = 83790;
        int totalMonth = 5;
        int loanTerm = 10;
        double annualInterestRate = 0.057;

//        Map<Integer, BigDecimal> mapInterest = getPerMonthInterest(loanAmount, annualInterestRate, totalMonth);
//        for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
//            System.out.println("key:"+ entry.getKey() + ", value:" + entry.getValue());
//        }

//        BigDecimal perMonthPrincipalInterest = getPerMonthPrincipalInterest(loanAmount, annualInterestRate, totalMonth);
//        System.out.println(perMonthPrincipalInterest);
        Map<String, Object> result = getAveragePrincipalInterestByMonth(loanAmount, annualInterestRate, totalMonth);
    }
}
