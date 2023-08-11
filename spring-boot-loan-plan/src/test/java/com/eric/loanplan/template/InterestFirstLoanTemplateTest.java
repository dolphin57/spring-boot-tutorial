package com.eric.loanplan.template;

import com.eric.loanplan.datestrategy.DateSplitStrategy;
import com.eric.loanplan.datestrategy.MonthSplitStrategy;
import com.eric.loanplan.intereststrategy.InterestCalculationStrategy;
import com.eric.loanplan.intereststrategy.MonthInterestStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

class InterestFirstLoanTemplateTest {
    private BigDecimal loanAmount; //贷款本金
    private BigDecimal annualInterestRate; //年利率
    private Integer repaymentDate; // 放款日
    private LocalDate startDate;// 合同开始时间
    private LocalDate endDate;// 合同结束时间

    private InterestFirstLoanTemplate interestFirstLoanTemplate;
    private DateSplitStrategy splitStrategy;

    @BeforeEach
    public void setUp() throws Exception {
        loanAmount = BigDecimal.valueOf(1000000L);
        annualInterestRate = BigDecimal.valueOf(0.049);
        repaymentDate = 21;
        startDate = LocalDate.of(2023, 8, 9);
        endDate = LocalDate.of(2023, 12, 31);

        InterestCalculationStrategy interestStrategy = new MonthInterestStrategy();
        splitStrategy = new MonthSplitStrategy();
        interestFirstLoanTemplate = new InterestFirstLoanTemplate(interestStrategy, splitStrategy);
    }

    @Test
    void getPerInterest() {
    }

    @Test
    void getPerPrincipalInterest() {
        List<LocalDate> dateList = interestFirstLoanTemplate.splitDate(startDate, endDate, repaymentDate);
        Map<Integer, BigDecimal> monthlyRepayment = interestFirstLoanTemplate.getPerPrincipalInterest(loanAmount, annualInterestRate, dateList.size());

        // 日期切分后,首期不足月处理
        BigDecimal dayInterest = null;
        LocalDate currentDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), repaymentDate);
        if (startDate.isBefore(currentDate)) {
            double dayInterestRate = annualInterestRate.doubleValue() / 360;
            long betweenDay = ChronoUnit.DAYS.between(startDate, currentDate); // 算不算合同开始当天
            dayInterest = loanAmount.multiply(BigDecimal.valueOf(dayInterestRate))
                    .multiply(BigDecimal.valueOf(betweenDay)).setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        for (int i = 1; i <= dateList.size(); i++) {
            if (i == 1) {
//                BigDecimal DownPaymentAmount = monthlyRepayment.get(i).add(dayInterest).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.println("期数: " + i + " 还款额: " + dayInterest + " 日期: "+ dateList.get(i-1));
            } else {
                System.out.println("期数: " + i + " 还款额: " + monthlyRepayment.get(i) + " 日期: "+ dateList.get(i-1));
            }
        }
    }

    @Test
    void getStartEndPrincipal() {
        Map<Integer, List<LocalDate>> startEndMap = splitStrategy.splitStartEnd(startDate, endDate, repaymentDate);
        Map<Integer, BigDecimal> monthlyRepayment = interestFirstLoanTemplate.getPerPrincipalInterest(loanAmount, annualInterestRate, startEndMap.size());

        // 日期切分后,首期不足月处理
        BigDecimal dayInterest = null;
        LocalDate currentDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), repaymentDate);
        if (startDate.isBefore(currentDate)) {
            double dayInterestRate = annualInterestRate.doubleValue() / 360;
            long betweenDay = ChronoUnit.DAYS.between(startDate, currentDate); // 算不算合同开始当天
            dayInterest = loanAmount.multiply(BigDecimal.valueOf(dayInterestRate))
                    .multiply(BigDecimal.valueOf(betweenDay)).setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        for (Map.Entry<Integer, List<LocalDate>> entry : startEndMap.entrySet()) {
            if (1 == entry.getKey()) {
                System.out.println("期数: " + entry.getKey() + " 还款额: " + dayInterest +
                        " 开始日期: "+ entry.getValue().get(0) + " 结束日期: "+ entry.getValue().get(1));
            } else {
                System.out.println("期数: " + entry.getKey() + " 还款额: " + monthlyRepayment.get(entry.getKey()) +
                        " 开始日期: "+ entry.getValue().get(0) + " 结束日期: "+ entry.getValue().get(1));
            }
        }
    }
}