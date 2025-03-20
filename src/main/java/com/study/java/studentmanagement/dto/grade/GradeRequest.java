package com.study.java.studentmanagement.dto.grade;

import lombok.Data;

@Data
public class GradeRequest {
    private String courseId;
    private String transcriptId;
    private double midScore;
    private double finalScore;
}