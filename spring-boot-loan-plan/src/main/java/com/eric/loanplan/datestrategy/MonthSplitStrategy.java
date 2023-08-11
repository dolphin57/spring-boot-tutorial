package com.eric.loanplan.datestrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthSplitStrategy implements DateSplitStrategy {
    @Override
    public List<LocalDate> split(LocalDate startDate, LocalDate endDate, int repaymentDate) {
        List<LocalDate> result = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), repaymentDate);

        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            result.add(currentDate);
            currentDate = currentDate.plusMonths(1);
        }
        return result;
    }

    public Map<Integer, List<LocalDate>> splitStartEnd(LocalDate startDate, LocalDate endDate, int repaymentDate) {
        Map<Integer, List<LocalDate>> startEndMap = new HashMap<>();
        int term = 1;
        LocalDate currentDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), repaymentDate);

        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            List<LocalDate> startEnd = new ArrayList<>();
            startEnd.add(startDate);
            startEnd.add(currentDate);
            startEndMap.put(term, startEnd);

            startDate = currentDate;
            currentDate = currentDate.plusMonths(1);
            term++;
        }
        return startEndMap;
    }
}
