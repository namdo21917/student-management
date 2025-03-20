package com.study.java.studentmanagement.service;

import com.study.java.studentmanagement.dto.course.CourseResponse;
import com.study.java.studentmanagement.dto.grade.GradeRequest;
import com.study.java.studentmanagement.dto.grade.GradeResponse;
import com.study.java.studentmanagement.model.Grade;
import com.study.java.studentmanagement.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepository;
    private final CourseService courseService;

    public List<GradeResponse> getAllGrades() {
        return gradeRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public GradeResponse getGradeById(String id) {
        return gradeRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
    }

    public GradeResponse createGrade(GradeRequest request) {
        Grade grade = new Grade();
        updateGradeFromRequest(grade, request);
        return convertToResponse(gradeRepository.save(grade));
    }

    public GradeResponse updateGrade(String id, GradeRequest request) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        updateGradeFromRequest(grade, request);
        return convertToResponse(gradeRepository.save(grade));
    }

    public void deleteGrade(String id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        grade.setDeleted(true);
        gradeRepository.save(grade);
    }

    private void updateGradeFromRequest(Grade grade, GradeRequest request) {
        grade.setCourseId(request.getCourseId());
        grade.setTranscriptId(request.getTranscriptId());
        grade.setMidScore(request.getMidScore());
        grade.setFinalScore(request.getFinalScore());
    }

    private GradeResponse convertToResponse(Grade grade) {
        GradeResponse response = new GradeResponse();
        response.setId(grade.getId());
        response.setCourseId(grade.getCourseId());
        response.setTranscriptId(grade.getTranscriptId());
        response.setMidScore(grade.getMidScore());
        response.setFinalScore(grade.getFinalScore());
        response.setDeleted(grade.isDeleted());

        // Set course information
        CourseResponse courseResponse = courseService.getCourseById(grade.getCourseId());
        response.setCourse(courseResponse);

        return response;
    }
}