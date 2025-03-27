package com.study.java.studentmanagement.service;

import com.study.java.studentmanagement.dto.transcript.GradeResponse;
import com.study.java.studentmanagement.dto.transcript.TranscriptRequest;
import com.study.java.studentmanagement.dto.transcript.TranscriptResponse;
import com.study.java.studentmanagement.model.Grade;
import com.study.java.studentmanagement.model.Semester;
import com.study.java.studentmanagement.model.Transcript;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.repository.SemesterRepository;
import com.study.java.studentmanagement.repository.TranscriptRepository;
import com.study.java.studentmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TranscriptService {
    private final TranscriptRepository transcriptRepository;
    private final UserRepository userRepository;
    private final SemesterRepository semesterRepository;
    @Autowired
    public List<TranscriptResponse> getAllTranscripts() {
        return transcriptRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public TranscriptResponse getTranscriptById(String id) {
        return transcriptRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("Transcript not found"));
    }

    public List<TranscriptResponse> getTranscriptByStudentId(String studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return transcriptRepository.findByStudentCode(student.getMsv()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public TranscriptResponse getTranscriptBySemesterStudent(String studentId, String semesterId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return transcriptRepository.findByStudentCodeAndSemesterId(student.getMsv(), semesterId)
                .stream()
                .findFirst()
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("Transcript not found"));
    }

    public TranscriptResponse createTranscript(TranscriptRequest request) {
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new RuntimeException("Semester not found"));

        // Check if transcript already exists
        if (transcriptRepository.existsByStudentCodeAndSemesterId(student.getMsv(), semester.getId())) {
            throw new RuntimeException("Transcript already exists for this student and semester");
        }

        Transcript transcript = new Transcript();
        transcript.setStudent(student);
        transcript.setStudentName(student.getFullName());
        transcript.setStudentCode(student.getMsv());
        transcript.setSemesterId(semester.getId());
        transcript.setSemesterName(buildSemesterName(semester));
        return convertToResponse(transcriptRepository.save(transcript));
    }

    public TranscriptResponse restoreTranscript(String transcriptId) {
        Transcript transcript = transcriptRepository.findById(transcriptId)
                .orElseThrow(() -> new RuntimeException("Transcript not found"));
        transcript.setDeleted(false);
        return convertToResponse(transcriptRepository.save(transcript));
    }

    public TranscriptResponse updateTranscript(String id, TranscriptRequest request) {
        Transcript transcript = transcriptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transcript not found"));

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new RuntimeException("Semester not found"));

        // Check if another transcript with same student and semester exists
        if (!transcript.getStudentCode().equals(student.getMsv()) ||
                !transcript.getSemesterId().equals(semester.getId())) {
            if (transcriptRepository.existsByStudentCodeAndSemesterId(student.getMsv(), semester.getId())) {
                throw new RuntimeException("Transcript already exists for this student and semester");
            }
        }

        transcript.setStudent(student);
        transcript.setStudentName(student.getFullName());
        transcript.setStudentCode(student.getMsv());
        transcript.setSemesterId(semester.getId());
        transcript.setSemesterName(buildSemesterName(semester));
        return convertToResponse(transcriptRepository.save(transcript));
    }

    public void deleteTranscript(String id) {
        Transcript transcript = transcriptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transcript not found"));
        transcript.setDeleted(true);
        transcriptRepository.save(transcript);
    }

    public Page<TranscriptResponse> searchTranscripts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return transcriptRepository.findAll(pageable).map(this::convertToResponse);
        }

        return transcriptRepository
                .findByStudentNameContainingIgnoreCaseOrStudentCodeContainingIgnoreCaseOrSemesterNameContainingIgnoreCase(
                        keyword, keyword, keyword, pageable)
                .map(this::convertToResponse);
    }

    private TranscriptResponse convertToResponse(Transcript transcript) {
        TranscriptResponse response = new TranscriptResponse();
        response.setId(transcript.getId());
        response.setStudentId(transcript.getStudent().getId());
        response.setStudentName(transcript.getStudentName());
        response.setStudentCode(transcript.getStudentCode());
        response.setSemesterId(transcript.getSemesterId());
        response.setSemesterName(transcript.getSemesterName());
        response.setGrades(transcript.getGrades().stream()
                .filter(grade -> !grade.isDeleted())
                .map(this::convertGradeToResponse)
                .collect(Collectors.toList()));
        response.setDeleted(transcript.isDeleted());
        response.setCreatedAt(transcript.getCreatedAt().toString());
        response.setUpdatedAt(transcript.getUpdatedAt().toString());
        return response;
    }

    private GradeResponse convertGradeToResponse(Grade grade) {
        GradeResponse response = new GradeResponse();
        response.setId(grade.getId());
        response.setCourseId(grade.getCourseId());
        response.setCourseName(grade.getCourseName());
        response.setMidScore(grade.getMidScore());
        response.setFinalScore(grade.getFinalScore());
        response.setAverageScore(grade.getAverageScore());
        response.setStatus(grade.getStatus());
        response.setTranscriptId(grade.getTranscript().getId());
        return response;
    }

    private String buildSemesterName(Semester semester) {
        return String.format("%s - %s - Năm học: %s",
                semester.getSemester(),
                semester.getGroup(),
                semester.getYear());
    }
}