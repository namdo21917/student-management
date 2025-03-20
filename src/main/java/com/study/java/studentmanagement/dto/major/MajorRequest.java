package com.study.java.studentmanagement.dto.major;

import lombok.Data;

@Data
public class MajorRequest {
    private String name;
    private String code;
    private String description;
    private boolean active;
}