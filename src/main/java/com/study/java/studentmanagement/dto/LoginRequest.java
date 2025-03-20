package com.study.java.studentmanagement.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String msv;
    private String password;
}