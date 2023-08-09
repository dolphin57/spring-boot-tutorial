package com.eric.loanplan.date;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MonthSplitter implements DateSplitter {
    @Override
    public List<LocalDate> split(LocalDate currentDate, LocalDate endDate, int repaymentDate) {
        List<LocalDate> result = new ArrayList<>();

        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            result.add(currentDate);
            currentDate = currentDate.plusMonths(1);
        }
        return result;
    }

    public static void main(String[] args) {
        MonthSplitter monthSplitter = new MonthSplitter();
        LocalDate startDate = LocalDate.of(2023, 8, 9);
        int repaymentDate = 21;
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        LocalDate currentDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), repaymentDate);
        List<LocalDate> dateList = monthSplitter.split(currentDate, endDate, repaymentDate);
        System.out.println(dateList);

        long betweenDay = ChronoUnit.DAYS.between(startDate, currentDate);
        System.out.println(betweenDay);
    }
}
