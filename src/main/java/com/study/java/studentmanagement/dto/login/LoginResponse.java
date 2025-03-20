package com.study.java.studentmanagement.dto.login;

import com.study.java.studentmanagement.dto.user.UserResponse;
import lombok.Data;

@Data
public class LoginResponse {
    private TokenResponse tokens;
    private LoginData data;

    @Data
    public static class LoginData {
        private UserResponse user;
    }
}