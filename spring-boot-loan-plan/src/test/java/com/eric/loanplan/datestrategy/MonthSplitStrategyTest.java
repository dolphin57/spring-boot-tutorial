package com.eric.loanplan.datestrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MonthSplitStrategyTest {
    private MonthSplitStrategy monthSplitter;
    private LocalDate startDate;
    private int repaymentDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        monthSplitter = new MonthSplitStrategy();
        startDate = LocalDate.of(2022, 4, 13);
        repaymentDate = 21;
        endDate = LocalDate.of(2023, 4, 13);
    }

    @Test
    void split() {
        List<LocalDate> dateList = monthSplitter.split(startDate, endDate, repaymentDate);
        for (int i = 1; i <= dateList.size(); i++) {
            System.out.println("期数: " + i + "日期: " + dateList.get(i - 1));
        }
//        long betweenDay = ChronoUnit.DAYS.between(startDate, currentDate);
//        System.out.println(betweenDay);
    }

    @Test
    void splitStartEnd() {
        Map<Integer, List<LocalDate>> map = monthSplitter.splitStartEnd(startDate, endDate, repaymentDate);
        System.out.println(map);
    }
}