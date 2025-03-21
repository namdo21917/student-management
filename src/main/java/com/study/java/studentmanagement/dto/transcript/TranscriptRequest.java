package com.study.java.studentmanagement.dto.transcript;

import lombok.Data;

@Data
public class TranscriptRequest {
    private String studentId;
    private String semesterId;
}