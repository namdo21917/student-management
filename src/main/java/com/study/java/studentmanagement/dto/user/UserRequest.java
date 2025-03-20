package com.study.java.studentmanagement.dto.user;

import lombok.Data;

@Data
public class UserRequest {
    private String fullName;
    private String msv;
    private String password;
    private String year;
    private String gvcn;
    private String gender;
    private String className;
    private String email;
    private String majorId;
    private String phone;
    private String country;
    private String address;
    private String dob;
}