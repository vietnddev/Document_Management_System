package com.flowiee.dms.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static String getCurrentYear() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy");
        LocalDateTime now = LocalDateTime.now();
        return dateTimeFormatter.format(now);
    }

    public static String getCurrentMonth() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM");
        LocalDateTime now = LocalDateTime.now();
        return dateTimeFormatter.format(now);
    }

    public static String getCurrentDay() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd");
        LocalDateTime now = LocalDateTime.now();
        return dateTimeFormatter.format(now);
    }
}