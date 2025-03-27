package com.study.java.studentmanagement.dto.teacher;

import lombok.Data;

@Data
public class TeacherRequest {
    private String mgv;
    private String fullName;
    private String email;
    private Boolean isAdmin;
    private Boolean isGV;
    private String password;
}