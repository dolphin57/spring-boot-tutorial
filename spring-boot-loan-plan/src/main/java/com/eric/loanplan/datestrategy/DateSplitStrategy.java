package com.eric.loanplan.datestrategy;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DateSplitStrategy {
    /**
     * 切分单还款日
     * @param startDate
     * @param endDate
     * @param repaymentDate
     * @return
     */
    public List<LocalDate> split(LocalDate startDate, LocalDate endDate, int repaymentDate);

    /**
     * 切分开始和结束时间
     * @param startDate
     * @param endDate
     * @param repaymentDate
     * @return
     */
    public Map<Integer, List<LocalDate>> splitStartEnd(LocalDate startDate, LocalDate endDate, int repaymentDate);
}
