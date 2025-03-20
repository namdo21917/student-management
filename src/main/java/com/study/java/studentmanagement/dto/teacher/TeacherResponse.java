package com.study.java.studentmanagement.dto.teacher;

import lombok.Data;

@Data
public class TeacherResponse {
    private String id;
    private String mgv;
    private String fullName;
    private String email;
    private boolean gv;
}