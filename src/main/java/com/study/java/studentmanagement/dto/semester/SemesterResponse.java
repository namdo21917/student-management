package com.study.java.studentmanagement.dto.semester;

import lombok.Data;

@Data
public class SemesterResponse {
    private String id;
    private String semester;
    private String group;
    private String year;
    private boolean active;
    private boolean deleted;
}