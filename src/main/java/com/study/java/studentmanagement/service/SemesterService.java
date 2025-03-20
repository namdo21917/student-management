package com.study.java.studentmanagement.service;

import com.study.java.studentmanagement.dto.semester.SemesterRequest;
import com.study.java.studentmanagement.dto.semester.SemesterResponse;
import com.study.java.studentmanagement.model.Semester;
import com.study.java.studentmanagement.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SemesterService {
    private final SemesterRepository semesterRepository;

    public List<SemesterResponse> getAllSemesters() {
        return semesterRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public SemesterResponse getSemesterById(String id) {
        return semesterRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("Semester not found"));
    }

    public SemesterResponse createSemester(SemesterRequest request) {
        // Check if semester with same semester, group and year already exists
        if (semesterRepository.existsBySemesterAndGroupAndYear(
                request.getSemester(), request.getGroup(), request.getYear())) {
            throw new RuntimeException("Semester already exists");
        }

        Semester semester = new Semester();
        updateSemesterFromRequest(semester, request);
        return convertToResponse(semesterRepository.save(semester));
    }

    public SemesterResponse updateSemester(String id, SemesterRequest request) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semester not found"));

        // Check if another semester with same semester, group and year exists
        if (!semester.getSemester().equals(request.getSemester()) ||
                !semester.getGroup().equals(request.getGroup()) ||
                !semester.getYear().equals(request.getYear())) {

            if (semesterRepository.existsBySemesterAndGroupAndYear(
                    request.getSemester(), request.getGroup(), request.getYear())) {
                throw new RuntimeException("Semester already exists");
            }
        }

        updateSemesterFromRequest(semester, request);
        return convertToResponse(semesterRepository.save(semester));
    }

    public void deleteSemester(String id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semester not found"));
        semester.setDeleted(true);
        semesterRepository.save(semester);
    }

    public Page<SemesterResponse> searchSemesters(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return semesterRepository.findAll(pageable).map(this::convertToResponse);
        }

        return semesterRepository
                .findBySemesterContainingIgnoreCaseOrGroupContainingIgnoreCaseOrYearContainingIgnoreCase(
                        keyword, keyword, keyword, pageable)
                .map(this::convertToResponse);
    }

    private void updateSemesterFromRequest(Semester semester, SemesterRequest request) {
        semester.setSemester(request.getSemester());
        semester.setGroup(request.getGroup());
        semester.setYear(request.getYear());
        semester.setActive(request.isActive());
    }

    private SemesterResponse convertToResponse(Semester semester) {
        SemesterResponse response = new SemesterResponse();
        response.setId(semester.getId());
        response.setSemester(semester.getSemester());
        response.setGroup(semester.getGroup());
        response.setYear(semester.getYear());
        response.setActive(semester.isActive());
        response.setDeleted(semester.isDeleted());
        return response;
    }
}