package com.study.java.studentmanagement.repository;

import com.study.java.studentmanagement.model.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, String> {
        Optional<Semester> findBySemesterAndGroupAndYear(String semester, String group, String year);

        boolean existsBySemesterAndGroupAndYear(String semester, String group, String year);

        List<Semester> findByActiveTrue();

        Page<Semester> findBySemesterContainingIgnoreCaseOrGroupContainingIgnoreCaseOrYearContainingIgnoreCase(
                        String semester, String group, String year, Pageable pageable);

        List<Semester> findByDeletedFalseOrderByCreatedAtDesc();

        boolean existsByYearAndSemesterAndGroupAndDeletedFalse(String year, String semester, String group);

        @Query("SELECT s FROM Semester s WHERE s.deleted = false AND " +
                        "(LOWER(s.semester) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(s.group) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(s.year) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        Page<Semester> searchSemesters(String keyword, Pageable pageable);

        @Query("SELECT s FROM Semester s WHERE s.deleted = false AND s.active = true")
        List<Semester> findActiveSemesters();
}