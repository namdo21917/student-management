package com.study.java.studentmanagement.session;

import com.study.java.studentmanagement.dto.user.UserResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSession {
    private static String accessToken;
    private static UserResponse user;

    public static String getAccessToken() {
        return accessToken;
    }

    public static UserResponse getUser() {
        return user;
    }

    public static void setAccessToken(String token) {
        accessToken = token;
    }

    public static void setUser(UserResponse user) {
        UserSession.user = user;
    }

    public static void clear() {
        accessToken = null;
        user = null;
    }

    public static boolean isLoggedIn() {
        return accessToken != null && user != null;
    }
}