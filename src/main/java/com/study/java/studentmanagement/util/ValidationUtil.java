package com.study.java.studentmanagement.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[0-9]{10}$");

    private static final Pattern MSV_PATTERN = Pattern.compile(
            "^[A-Z0-9]{6,8}$");

    private static final Pattern MGV_PATTERN = Pattern.compile(
            "^[A-Z0-9]{4,6}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidMSV(String msv) {
        return msv != null && MSV_PATTERN.matcher(msv).matches();
    }

    public static boolean isValidMGV(String mgv) {
        return mgv != null && MGV_PATTERN.matcher(mgv).matches();
    }

    public static boolean isNullOrEmpty(String str) {
        return !StringUtils.hasText(str);
    }

    public static boolean isValidName(String name) {
        return StringUtils.hasText(name) && name.length() >= 2 && name.length() <= 50;
    }

    public static boolean isValidCredit(Integer credit) {
        return credit != null && credit > 0 && credit <= 10;
    }

    public static boolean isValidGrade(Double grade) {
        return grade != null && grade >= 0 && grade <= 10;
    }

    public static boolean isValidMajorCode(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        // Chỉ cho phép chữ cái in hoa và số
        return code.matches("^[A-Z0-9]+$");
    }
}