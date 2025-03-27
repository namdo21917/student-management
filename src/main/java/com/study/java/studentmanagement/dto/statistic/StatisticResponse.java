package com.study.java.studentmanagement.dto.statistic;

import lombok.Data;

@Data
public class StatisticResponse {
    private Long totalStudents;
    private Long totalTeacher;
    private Long totalCourse;
    private String activeSemester;
}