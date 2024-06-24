package io.dolphin.date;

import com.google.common.collect.Range;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author dolphin
 * @date 2024年03月11日 10:27
 * @description
 */
public class GuavaDateDifferenceCalculator {
    public static void main(String[] args) {
        // 从前端获取的日期字符串
        String dateSampleLocal = "20240229"; // 替换为从前端获取的startDate

        // 将字符串转换为LocalDate对象
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate startDate = LocalDate.parse(dateSampleLocal, formatter);

        // 将startDate增加1年
        LocalDate nextYearStartDate = startDate.plusMonths(12);
        System.out.println("nextYearStartDate: " + nextYearStartDate);

        // 创建一个Calendar实例，并设置它的时间为当前时间
        Calendar calendar = Calendar.getInstance();
        // 设置指定日期：2024年2月29日
        calendar.set(Calendar.YEAR, 2024); // 设置年份为2024年
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY); // 注意月份是从0开始计数，所以2月是1
        calendar.set(Calendar.DAY_OF_MONTH, 29); // 设置日期为29日

        // 获取当前的年份
        int currentYear = calendar.get(Calendar.YEAR);

        // 将年份加1
        calendar.set(Calendar.YEAR, currentYear + 1);

        // 输出增加一年后的日期
        Date newDate = calendar.getTime();
        System.out.println("Next year's date: " + newDate);
    }
}
