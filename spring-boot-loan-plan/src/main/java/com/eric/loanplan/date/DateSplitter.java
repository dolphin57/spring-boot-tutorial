package com.eric.loanplan.date;

import java.time.LocalDate;
import java.util.List;

public interface DateSplitter {
    public List<LocalDate> split(LocalDate startDate, LocalDate endDate, int repaymentDate);
}
