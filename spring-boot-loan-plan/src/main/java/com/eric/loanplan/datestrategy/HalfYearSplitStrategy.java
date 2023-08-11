package com.eric.loanplan.datestrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HalfYearSplitStrategy implements DateSplitStrategy {
    @Override
    public List<LocalDate> split(LocalDate startDate, LocalDate endDate, int repaymentDate) {
        List<LocalDate> result = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            result.add(currentDate);
            currentDate = currentDate.plusMonths(6);
        }
        return result;
    }

    @Override
    public Map<Integer, List<LocalDate>> splitStartEnd(LocalDate startDate, LocalDate endDate, int repaymentDate) {
        return null;
    }
}
