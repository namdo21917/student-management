package com.study.java.studentmanagement.controller;

import com.study.java.studentmanagement.dto.semester.SemesterRequest;
import com.study.java.studentmanagement.dto.semester.SemesterResponse;
import com.study.java.studentmanagement.service.SemesterService;
import com.study.java.studentmanagement.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semester")
@RequiredArgsConstructor
public class SemesterController {
    private final SemesterService semesterService;

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<SemesterResponse>>> getAllSemesters() {
        List<SemesterResponse> semesters = semesterService.getAllSemesters();
        return ResponseEntity.ok(new ApiResponse<List<SemesterResponse>>(semesters));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse<SemesterResponse>> getSemesterById(@PathVariable String id) {
        SemesterResponse semester = semesterService.getSemesterById(id);
        return ResponseEntity.ok(new ApiResponse<SemesterResponse>(semester));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<SemesterResponse>> createSemester(@RequestBody SemesterRequest request) {
        SemesterResponse semester = semesterService.createSemester(request);
        return ResponseEntity.ok(new ApiResponse<SemesterResponse>(semester));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<SemesterResponse>> updateSemester(
            @PathVariable String id,
            @RequestBody SemesterRequest request) {
        SemesterResponse semester = semesterService.updateSemester(id, request);
        return ResponseEntity.ok(new ApiResponse<SemesterResponse>(semester));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSemester(@PathVariable String id) {
        semesterService.deleteSemester(id);
        return ResponseEntity.ok(new ApiResponse<Void>(null));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<SemesterResponse>> searchSemesters(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(semesterService.searchSemesters(keyword, pageable));
    }
}