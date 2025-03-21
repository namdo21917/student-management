package com.study.java.studentmanagement.controller;

import com.study.java.studentmanagement.dto.transcript.TranscriptRequest;
import com.study.java.studentmanagement.dto.transcript.TranscriptResponse;
import com.study.java.studentmanagement.service.TranscriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transcript")
@RequiredArgsConstructor
public class TranscriptController {
    private final TranscriptService transcriptService;

    @GetMapping("/getAll")
    public ResponseEntity<List<TranscriptResponse>> getAllTranscripts() {
        return ResponseEntity.ok(transcriptService.getAllTranscripts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TranscriptResponse> getTranscriptById(@PathVariable String id) {
        return ResponseEntity.ok(transcriptService.getTranscriptById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<TranscriptResponse>> getTranscriptByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(transcriptService.getTranscriptByStudentId(studentId));
    }

    @GetMapping("/student/{studentId}/semester/{semesterId}")
    public ResponseEntity<TranscriptResponse> getTranscriptBySemesterStudent(
            @PathVariable String studentId,
            @PathVariable String semesterId) {
        return ResponseEntity.ok(transcriptService.getTranscriptBySemesterStudent(studentId, semesterId));
    }

    @PostMapping("/create")
    public ResponseEntity<TranscriptResponse> createTranscript(@RequestBody TranscriptRequest request) {
        return ResponseEntity.ok(transcriptService.createTranscript(request));
    }

    @PutMapping("/restore")
    public ResponseEntity<TranscriptResponse> restoreTranscript(@RequestBody String transcriptId) {
        return ResponseEntity.ok(transcriptService.restoreTranscript(transcriptId));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TranscriptResponse> updateTranscript(
            @PathVariable String id,
            @RequestBody TranscriptRequest request) {
        return ResponseEntity.ok(transcriptService.updateTranscript(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTranscript(@PathVariable String id) {
        transcriptService.deleteTranscript(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/search")
    public ResponseEntity<Page<TranscriptResponse>> searchTranscripts(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(transcriptService.searchTranscripts(keyword, pageable));
    }
}