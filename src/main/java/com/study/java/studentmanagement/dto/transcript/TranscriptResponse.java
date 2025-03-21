package com.study.java.studentmanagement.dto.transcript;

import lombok.Data;
import java.util.List;

@Data
public class TranscriptResponse {
    private String id;
    private String studentId;
    private String studentName;
    private String studentCode;
    private String semesterId;
    private String semesterName;
    private List<GradeResponse> grades;
    private boolean deleted;
    private String createdAt;
    private String updatedAt;
}