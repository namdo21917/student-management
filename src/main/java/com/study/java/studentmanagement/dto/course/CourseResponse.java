package com.study.java.studentmanagement.dto.course;

import lombok.Data;

@Data
public class CourseResponse {
    private String id;
    private String name;
    private String code;
    private int credit;
    private String majorId;
    private String majorName;
    private boolean deleted;
}