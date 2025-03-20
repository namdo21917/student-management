package com.study.java.studentmanagement.controller;

import com.study.java.studentmanagement.dto.grade.GradeRequest;
import com.study.java.studentmanagement.dto.grade.GradeResponse;
import com.study.java.studentmanagement.service.GradeService;
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
    public ResponseEntity<List<GradeResponse>> getAllGrades() {
        return ResponseEntity.ok(gradeService.getAllGrades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeResponse> getGradeById(@PathVariable String id) {
        return ResponseEntity.ok(gradeService.getGradeById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<GradeResponse> createGrade(@RequestBody GradeRequest request) {
        return ResponseEntity.ok(gradeService.createGrade(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GradeResponse> updateGrade(
            @PathVariable String id,
            @RequestBody GradeRequest request) {
        return ResponseEntity.ok(gradeService.updateGrade(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable String id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.ok().build();
    }
}