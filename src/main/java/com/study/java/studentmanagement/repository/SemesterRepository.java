package com.study.java.studentmanagement.repository;

import com.study.java.studentmanagement.model.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, String> {
    Optional<Semester> findBySemesterAndGroupAndYear(String semester, String group, String year);

    boolean existsBySemesterAndGroupAndYear(String semester, String group, String year);

    Page<Semester> findBySemesterContainingIgnoreCaseOrGroupContainingIgnoreCaseOrYearContainingIgnoreCase(
            String semester, String group, String year, Pageable pageable);
}