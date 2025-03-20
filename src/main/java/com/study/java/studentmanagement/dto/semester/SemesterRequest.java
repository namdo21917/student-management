package com.study.java.studentmanagement.dto.semester;

import lombok.Data;

@Data
public class SemesterRequest {
    private String semester;
    private String group;
    private String year;
    private boolean active;
}