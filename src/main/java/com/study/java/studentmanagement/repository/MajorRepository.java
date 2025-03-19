package com.study.java.studentmanagement.repository;

import com.study.java.studentmanagement.model.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MajorRepository extends JpaRepository<Major, String> {
    Optional<Major> findByCode(String code);

    List<Major> findByNameContainingIgnoreCase(String name);

    boolean existsByCode(String code);

}