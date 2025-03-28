package com.study.java.studentmanagement.dto.user;

import com.study.java.studentmanagement.dto.major.MajorResponse;
import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String msv;
    private String fullName;
    private String gender;
    private String majorId;
    private String year;
    private String className;
    private boolean admin;
    private String gvcn;
    private String gvcnName;
    private boolean deleted;
    private boolean isGV;
    private String email;
    private String dob;
    private String phone;
    private String country;
    private String address;

    MajorResponse major;
}