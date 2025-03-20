package com.study.java.studentmanagement.service;

import com.study.java.studentmanagement.dto.course.CourseRequest;
import com.study.java.studentmanagement.dto.course.CourseResponse;
import com.study.java.studentmanagement.model.Course;
import com.study.java.studentmanagement.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public CourseResponse getCourseById(String id) {
        return courseRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public CourseResponse createCourse(CourseRequest request) {
        Course course = new Course();
        updateCourseFromRequest(course, request);
        return convertToResponse(courseRepository.save(course));
    }

    public CourseResponse updateCourse(String id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        updateCourseFromRequest(course, request);
        return convertToResponse(courseRepository.save(course));
    }

    public void deleteCourse(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setDeleted(true);
        courseRepository.save(course);
    }

    private void updateCourseFromRequest(Course course, CourseRequest request) {
        course.setName(request.getName());
        course.setCode(request.getCode());
        course.setCredit(request.getCredit());
        course.setMajorId(request.getMajorId());
    }

    private CourseResponse convertToResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setName(course.getName());
        response.setCode(course.getCode());
        response.setCredit(course.getCredit());
        response.setMajorId(course.getMajorId());
        response.setMajorName(course.getMajorName());
        response.setDeleted(course.isDeleted());
        return response;
    }
}