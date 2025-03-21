package com.study.java.studentmanagement.service;

import com.study.java.studentmanagement.dto.course.CourseResponse;
import com.study.java.studentmanagement.dto.grade.GradeRequest;
import com.study.java.studentmanagement.dto.grade.GradeResponse;
import com.study.java.studentmanagement.model.Course;
import com.study.java.studentmanagement.model.Grade;
import com.study.java.studentmanagement.model.Transcript;
import com.study.java.studentmanagement.repository.CourseRepository;
import com.study.java.studentmanagement.repository.GradeRepository;
import com.study.java.studentmanagement.repository.TranscriptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GradeService {
    private final GradeRepository gradeRepository;
    private final CourseService courseService;
    private final TranscriptRepository transcriptRepository;
    private final CourseRepository courseRepository;

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
        // Validate transcript exists
        Transcript transcript = transcriptRepository.findById(request.getTranscriptId())
                .orElseThrow(() -> new RuntimeException("Transcript not found"));

        // Validate course exists
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if grade already exists for this course in the transcript
        if (gradeRepository.existsByCourseIdAndTranscriptAndDeletedFalse(course.getId(), transcript)) {
            throw new RuntimeException("Grade already exists for this course in the transcript");
        }

        Grade grade = new Grade();
        updateGradeFromRequest(grade, request, transcript, course);
        calculateGrade(grade);
        return convertToResponse(gradeRepository.save(grade));
    }

    public GradeResponse updateGrade(String id, GradeRequest request) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));

        // Validate transcript exists
        Transcript transcript = transcriptRepository.findById(request.getTranscriptId())
                .orElseThrow(() -> new RuntimeException("Transcript not found"));

        // Validate course exists
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if updating to a different course that already has a grade
        if (!grade.getCourseId().equals(course.getId()) &&
                gradeRepository.existsByCourseIdAndTranscriptAndDeletedFalse(course.getId(), transcript)) {
            throw new RuntimeException("Grade already exists for this course in the transcript");
        }

        updateGradeFromRequest(grade, request, transcript, course);
        calculateGrade(grade);
        return convertToResponse(gradeRepository.save(grade));
    }

    public void deleteGrade(String id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        grade.setDeleted(true);
        gradeRepository.save(grade);
    }

    private void updateGradeFromRequest(Grade grade, GradeRequest request, Transcript transcript, Course course) {
        grade.setCourseId(course.getId());
        grade.setCourseName(course.getName());
        grade.setTranscript(transcript);
        grade.setMidScore(request.getMidScore());
        grade.setFinalScore(request.getFinalScore());
    }

    private void calculateGrade(Grade grade) {
        double averageScore = (grade.getMidScore() * 0.3) + (grade.getFinalScore() * 0.7);
        grade.setAverageScore(averageScore);
        grade.setStatus(averageScore >= 4.0 ? "Passed" : "Failed");
    }

    private GradeResponse convertToResponse(Grade grade) {
        GradeResponse response = new GradeResponse();
        response.setId(grade.getId());
        response.setCourseId(grade.getCourseId());
        response.setCourseName(grade.getCourseName());
        response.setTranscriptId(grade.getTranscript().getId());
        response.setMidScore(grade.getMidScore());
        response.setFinalScore(grade.getFinalScore());
        response.setAverageScore(grade.getAverageScore());
        response.setStatus(grade.getStatus());
        response.setDeleted(grade.isDeleted());

        // Set course information
        CourseResponse courseResponse = courseService.getCourseById(grade.getCourseId());
        response.setCourse(courseResponse);

        return response;
    }
}