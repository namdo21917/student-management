package com.study.java.studentmanagement.dto.course;

import lombok.Data;

@Data
public class CourseRequest {
    private String name;
    private String code;
    private int credit;
    private String majorId;
}