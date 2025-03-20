package com.study.java.studentmanagement.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private TokenResponse tokens;
    private UserDataResponse data;

    @Data
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
    }

    @Data
    public static class UserDataResponse {
        private UserResponse user;
    }

    @Data
    public static class UserResponse {
        private String id;
        private String msv;
        private String fullName;
        private String gender;
        private MajorResponse majorId;
        private String year;
        private String className;
        private boolean isAdmin;
        private String gvcn;
        private String gvcnName;
        private boolean deleted;
        private boolean isGV;
        private String email;
        private String dob;
        private String phone;
        private String country;
        private String address;
    }

    @Data
    public static class MajorResponse {
        private String id;
        private String name;
    }
}