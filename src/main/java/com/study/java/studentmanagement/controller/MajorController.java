package com.study.java.studentmanagement.controller;

import com.study.java.studentmanagement.dto.major.MajorRequest;
import com.study.java.studentmanagement.dto.major.MajorResponse;
import com.study.java.studentmanagement.service.MajorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/major")
@RequiredArgsConstructor
public class MajorController {
    private final MajorService majorService;

    @GetMapping("/getAll")
    public ResponseEntity<List<MajorResponse>> getAllMajors() {
        return ResponseEntity.ok(majorService.getAllMajors());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<MajorResponse> getMajorById(@PathVariable String id) {
        return ResponseEntity.ok(majorService.getMajorById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<MajorResponse> createMajor(@RequestBody MajorRequest request) {
        return ResponseEntity.ok(majorService.createMajor(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MajorResponse> updateMajor(
            @PathVariable String id,
            @RequestBody MajorRequest request) {
        return ResponseEntity.ok(majorService.updateMajor(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMajor(@PathVariable String id) {
        majorService.deleteMajor(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MajorResponse>> searchMajors(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(majorService.searchMajors(keyword, pageable));
    }
}