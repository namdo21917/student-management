package com.study.java.studentmanagement.repository;

import com.study.java.studentmanagement.model.Semester;
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
}