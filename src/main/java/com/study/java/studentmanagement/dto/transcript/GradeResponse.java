package com.study.java.studentmanagement.dto.transcript;

import lombok.Data;

@Data
public class GradeResponse {
    private String id;
    private String courseId;
    private String courseName;
    private double midScore;
    private double finalScore;
    private double averageScore;
    private String status;
    private String transcriptId;
}