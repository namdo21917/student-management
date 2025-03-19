package com.study.java.studentmanagement.repository;

import com.study.java.studentmanagement.model.Transcript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranscriptRepository extends JpaRepository<Transcript, String> {
    List<Transcript> findByStudentId(String studentId);

    List<Transcript> findByStudentIdAndSemesterId(String studentId, String semesterId);

    List<Transcript> findBySemesterId(String semesterId);

    @Query("SELECT t FROM Transcript t WHERE t.studentId = ?1 AND t.deleted = false ORDER BY t.semesterId DESC")
    List<Transcript> findAllActiveTranscriptsByStudent(String studentId);

    @Query("SELECT t FROM Transcript t WHERE t.semesterId = ?1 AND t.deleted = false ORDER BY t.studentName")
    List<Transcript> findAllActiveTranscriptsBySemester(String semesterId);

    @Query("SELECT AVG(t.averageScore) FROM Transcript t WHERE t.studentId = ?1 AND t.deleted = false")
    Double calculateGPA(String studentId);

    @Query("SELECT COUNT(t) FROM Transcript t WHERE t.studentId = ?1 AND t.status = 'Failed' AND t.deleted = false")
    Long countFailedCourses(String studentId);

    @Query("SELECT t FROM Transcript t WHERE t.deleted = false AND " +
            "(LOWER(t.studentName) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(t.studentCode) LIKE LOWER(CONCAT('%', ?1, '%')))")
    List<Transcript> searchTranscripts(String keyword);
}