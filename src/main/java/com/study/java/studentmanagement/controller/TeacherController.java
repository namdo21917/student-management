package com.study.java.studentmanagement.controller;

import com.study.java.studentmanagement.dto.teacher.TeacherRequest;
import com.study.java.studentmanagement.dto.teacher.TeacherResponse;
import com.study.java.studentmanagement.service.TeacherService;
import com.study.java.studentmanagement.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<TeacherResponse>>> getAllTeachers() {
        List<TeacherResponse> teachers = teacherService.getAllTeachers();
        return ResponseEntity.ok(new ApiResponse<List<TeacherResponse>>(teachers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherResponse>> getTeacherById(@PathVariable String id) {
        TeacherResponse teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(new ApiResponse<TeacherResponse>(teacher));
    }

    @PostMapping("/create-teacher")
    public ResponseEntity<ApiResponse<TeacherResponse>> createTeacher(@RequestBody TeacherRequest request) {
        TeacherResponse teacher = teacherService.createTeacher(request);
        return ResponseEntity.ok(new ApiResponse<TeacherResponse>(teacher));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<TeacherResponse>> updateTeacher(
            @PathVariable String id,
            @RequestBody TeacherRequest request) {
        TeacherResponse teacher = teacherService.updateTeacher(id, request);
        return ResponseEntity.ok(new ApiResponse<TeacherResponse>(teacher));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTeacher(@PathVariable String id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.ok(new ApiResponse<Void>(null));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TeacherResponse>> searchTeachers(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(teacherService.searchTeachers(keyword, pageable));
    }
}