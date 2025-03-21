package com.study.java.studentmanagement.dto.grade;

import com.study.java.studentmanagement.dto.course.CourseResponse;
import lombok.Data;

@Data
public class GradeResponse {
    private String id;
    private String courseId;
    private String courseName;
    private String transcriptId;
    private double midScore;
    private double finalScore;
    private double averageScore;
    private String status;
    private boolean deleted;
    private CourseResponse course;
}