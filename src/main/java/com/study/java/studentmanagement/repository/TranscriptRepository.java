package com.study.java.studentmanagement.repository;

import com.study.java.studentmanagement.model.Transcript;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TranscriptRepository extends JpaRepository<Transcript, String> {
        @Query("SELECT t FROM Transcript t WHERE t.student.id = ?1")
        List<Transcript> findByStudentId(String studentId);

        Optional<Transcript> findById(String id);

        @Query("SELECT t FROM Transcript t WHERE t.student.id = ?1 AND t.semesterId = ?2")
        List<Transcript> findByStudentIdAndSemesterId(String studentId, String semesterId);

        List<Transcript> findBySemesterId(String semesterId);

        @Query("SELECT t FROM Transcript t WHERE t.student.id = ?1 AND t.deleted = false ORDER BY t.semesterId DESC")
        List<Transcript> findAllActiveTranscriptsByStudent(String studentId);

        @Query("SELECT t FROM Transcript t WHERE t.semesterId = ?1 AND t.deleted = false ORDER BY t.studentName")
        List<Transcript> findAllActiveTranscriptsBySemester(String semesterId);
        

        @Query("SELECT t FROM Transcript t WHERE t.deleted = false AND " +
                        "(LOWER(t.studentName) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
                        "LOWER(t.studentCode) LIKE LOWER(CONCAT('%', ?1, '%')))")
        List<Transcript> searchTranscripts(String keyword);

        @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Transcript t WHERE t.student.id = ?1 AND t.semesterId = ?2")
        boolean existsByStudentIdAndSemesterId(String studentId, String semesterId);

        Page<Transcript> findByStudentNameContainingIgnoreCaseOrStudentCodeContainingIgnoreCaseOrSemesterNameContainingIgnoreCase(
                        String studentName, String studentCode, String semesterName, Pageable pageable);

        List<Transcript> findByStudentCode(String studentCode);

        Optional<Transcript> findByStudentCodeAndSemesterId(String studentCode, String semesterId);

        boolean existsByStudentCodeAndSemesterId(String studentCode, String semesterId);
}