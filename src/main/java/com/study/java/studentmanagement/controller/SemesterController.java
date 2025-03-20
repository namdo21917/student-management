package com.study.java.studentmanagement.controller;

import com.study.java.studentmanagement.dto.semester.SemesterRequest;
import com.study.java.studentmanagement.dto.semester.SemesterResponse;
import com.study.java.studentmanagement.service.SemesterService;
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
    public ResponseEntity<List<SemesterResponse>> getAllSemesters() {
        return ResponseEntity.ok(semesterService.getAllSemesters());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<SemesterResponse> getSemesterById(@PathVariable String id) {
        return ResponseEntity.ok(semesterService.getSemesterById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<SemesterResponse> createSemester(@RequestBody SemesterRequest request) {
        return ResponseEntity.ok(semesterService.createSemester(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SemesterResponse> updateSemester(
            @PathVariable String id,
            @RequestBody SemesterRequest request) {
        return ResponseEntity.ok(semesterService.updateSemester(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSemester(@PathVariable String id) {
        semesterService.deleteSemester(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<SemesterResponse>> searchSemesters(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(semesterService.searchSemesters(keyword, pageable));
    }
}