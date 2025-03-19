package com.study.java.studentmanagement.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : "";
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : "";
    }

    public static LocalDateTime parseDate(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isValidDateFormat(String dateStr) {
        try {
            LocalDateTime.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidDateTimeFormat(String dateTimeStr) {
        try {
            LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}