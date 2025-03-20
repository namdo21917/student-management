package com.study.java.studentmanagement.repository;

import com.study.java.studentmanagement.model.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, String> {
    Optional<Semester> findByCode(String code);

    List<Semester> findByNameContainingIgnoreCase(String name);

    @Query("SELECT s FROM Semester s WHERE s.startDate <= ?1 AND s.endDate >= ?1 AND s.deleted = false")
    Optional<Semester> findCurrentSemester(LocalDateTime now);

    List<Semester> findByStartDateAfterAndDeletedFalseOrderByStartDateAsc(LocalDateTime date);

    List<Semester> findByEndDateBeforeAndDeletedFalseOrderByEndDateDesc(LocalDateTime date);

    @Query("SELECT s FROM Semester s WHERE s.deleted = false ORDER BY s.startDate DESC")
    List<Semester> findAllActiveSemesters();

    boolean existsByCode(String code);

    Optional<Semester> findBySemesterAndGroupAndYear(String semester, String group, String year);

    List<Semester> findByActiveTrue();

    @Query("SELECT s FROM Semester s WHERE s.active = true AND " +
            "(LOWER(s.semester) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(s.group) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(s.year) LIKE LOWER(CONCAT('%', ?1, '%')))")
    Page<Semester> searchSemesters(String keyword, Pageable pageable);

    @Query("SELECT COUNT(s) FROM Semester s WHERE s.active = true")
    Long countActiveSemesters();
}