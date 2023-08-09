package com.eric.loanplan.date;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class QuarterSplitter implements DateSplitter {
    @Override
    public List<LocalDate> split(LocalDate startDate, LocalDate endDate, int repaymentDate) {
        List<LocalDate> result = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            result.add(currentDate);
            currentDate = currentDate.plusMonths(3);
        }
        return result;
    }
}
