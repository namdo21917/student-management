package com.study.java.studentmanagement.controller;

import com.study.java.studentmanagement.dto.grade.GradeRequest;
import com.study.java.studentmanagement.dto.grade.GradeResponse;
import com.study.java.studentmanagement.service.GradeService;
import com.study.java.studentmanagement.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grade")
@RequiredArgsConstructor
public class GradeController {
    private final GradeService gradeService;

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<GradeResponse>>> getAllGrades() {
        List<GradeResponse> grades = gradeService.getAllGrades();
        return ResponseEntity.ok(new ApiResponse<>(grades));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GradeResponse>> getGradeById(@PathVariable String id) {
        GradeResponse grade = gradeService.getGradeById(id);
        return ResponseEntity.ok(new ApiResponse<>(grade));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<GradeResponse>> createGrade(@RequestBody GradeRequest request) {
        GradeResponse grade = gradeService.createGrade(request);
        return ResponseEntity.ok(new ApiResponse<>(grade));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<GradeResponse>> updateGrade(
            @PathVariable String id,
            @RequestBody GradeRequest request) {
        GradeResponse grade = gradeService.updateGrade(id, request);
        return ResponseEntity.ok(new ApiResponse<>(grade));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGrade(@PathVariable String id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.ok(new ApiResponse<>(null));
    }
}