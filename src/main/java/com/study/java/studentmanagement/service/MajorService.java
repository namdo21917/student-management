package com.study.java.studentmanagement.service;

import com.study.java.studentmanagement.dto.major.MajorRequest;
import com.study.java.studentmanagement.dto.major.MajorResponse;
import com.study.java.studentmanagement.model.Major;
import com.study.java.studentmanagement.repository.MajorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MajorService {
    private final MajorRepository majorRepository;

    public List<MajorResponse> getAllMajors() {
        return majorRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public MajorResponse getMajorById(String id) {
        return majorRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("Major not found"));
    }

    public MajorResponse createMajor(MajorRequest request) {
        if (majorRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Major code already exists");
        }
        Major major = new Major();
        updateMajorFromRequest(major, request);
        return convertToResponse(majorRepository.save(major));
    }

    public MajorResponse updateMajor(String id, MajorRequest request) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Major not found"));

        // Check if code is being changed and if new code already exists
        if (!major.getCode().equals(request.getCode()) &&
                majorRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Major code already exists");
        }

        updateMajorFromRequest(major, request);
        return convertToResponse(majorRepository.save(major));
    }

    public void deleteMajor(String id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Major not found"));
        major.setDeleted(true);
        majorRepository.save(major);
    }

    public Page<MajorResponse> searchMajors(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return majorRepository.findAll(pageable).map(this::convertToResponse);
        }

        return majorRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(this::convertToResponse);
    }

    private void updateMajorFromRequest(Major major, MajorRequest request) {
        major.setName(request.getName());
        major.setCode(request.getCode());
        major.setDescription(request.getDescription());
        major.setActive(request.isActive());
    }

    private MajorResponse convertToResponse(Major major) {
        MajorResponse response = new MajorResponse();
        response.setId(major.getId());
        response.setName(major.getName());
        response.setCode(major.getCode());
        response.setDescription(major.getDescription());
        response.setActive(major.isActive());
        response.setDeleted(major.isDeleted());
        return response;
    }
}