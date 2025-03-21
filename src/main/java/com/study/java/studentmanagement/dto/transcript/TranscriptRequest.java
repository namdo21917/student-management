package com.study.java.studentmanagement.dto.transcript;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptRequest {
    private String studentId;
    private String studentName;
    private String studentCode;
    private String semesterId;
    private String semesterName;
}