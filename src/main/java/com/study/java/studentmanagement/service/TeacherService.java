package com.study.java.studentmanagement.service;

import com.study.java.studentmanagement.dto.teacher.TeacherRequest;
import com.study.java.studentmanagement.dto.teacher.TeacherResponse;
import com.study.java.studentmanagement.model.Teacher;
import com.study.java.studentmanagement.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;

    public List<TeacherResponse> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public TeacherResponse getTeacherById(String id) {
        return teacherRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    public TeacherResponse createTeacher(TeacherRequest request) {
        // Check if teacher with same mgv already exists
        if (teacherRepository.existsByMgv(request.getMgv())) {
            throw new RuntimeException("Teacher with this MGV already exists");
        }

        Teacher teacher = new Teacher();
        updateTeacherFromRequest(teacher, request);
        return convertToResponse(teacherRepository.save(teacher));
    }

    public TeacherResponse updateTeacher(String id, TeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Check if another teacher with same mgv exists
        if (!teacher.getMgv().equals(request.getMgv()) &&
                teacherRepository.existsByMgv(request.getMgv())) {
            throw new RuntimeException("Teacher with this MGV already exists");
        }

        updateTeacherFromRequest(teacher, request);
        return convertToResponse(teacherRepository.save(teacher));
    }

    public void deleteTeacher(String id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        teacher.setDeleted(true);
        teacherRepository.save(teacher);
    }

    public Page<TeacherResponse> searchTeachers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return teacherRepository.findAll(pageable).map(this::convertToResponse);
        }

        return teacherRepository.findByFullNameContainingIgnoreCaseOrMgvContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword, keyword, keyword, pageable)
                .map(this::convertToResponse);
    }

    private void updateTeacherFromRequest(Teacher teacher, TeacherRequest request) {
        teacher.setMgv(request.getMgv());
        teacher.setFullName(request.getFullName());
        teacher.setEmail(request.getEmail());
        teacher.setAdmin(request.isAdmin());
        teacher.setGv(request.isGV());
    }

    private TeacherResponse convertToResponse(Teacher teacher) {
        TeacherResponse response = new TeacherResponse();
        response.setId(teacher.getId());
        response.setMgv(teacher.getMgv());
        response.setFullName(teacher.getFullName());
        response.setEmail(teacher.getEmail());
        response.setAdmin(teacher.isAdmin());
        response.setGV(teacher.isGv());
        response.setDeleted(teacher.isDeleted());
        return response;
    }
}