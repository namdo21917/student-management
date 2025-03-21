package com.study.java.studentmanagement.dto.major;

import lombok.Data;

@Data
public class MajorResponse {
    private String id;
    private String name;
    private String code;
    private String description;
    private boolean active;
    private boolean deleted;
}