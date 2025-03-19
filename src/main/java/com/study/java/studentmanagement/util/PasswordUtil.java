package com.study.java.studentmanagement.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordUtil {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final Pattern PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    public static String encode(String password) {
        return passwordEncoder.encode(password);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public static boolean isStrong(String password) {
        if (password == null) {
            return false;
        }
        return PATTERN.matcher(password).matches();
    }

    public static String getStrengthMessage(String password) {
        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        if (!password.matches(".*[0-9].*")) {
            return "Password must contain at least one digit";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter";
        }
        if (!password.matches(".*[@#$%^&+=].*")) {
            return "Password must contain at least one special character (@#$%^&+=)";
        }
        if (password.contains(" ")) {
            return "Password must not contain spaces";
        }
        return "Strong password";
    }
}