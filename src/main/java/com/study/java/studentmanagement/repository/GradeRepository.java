package com.study.java.studentmanagement.repository;

import com.study.java.studentmanagement.model.Grade;
import com.study.java.studentmanagement.model.Transcript;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, String> {
    List<Grade> findByStudentId(String studentId);

    List<Grade> findByStudentIdAndSemesterId(String studentId, String semesterId);

    List<Grade> findByCourseId(String courseId);

    List<Grade> findBySemesterId(String semesterId);

    @Query("SELECT g FROM Grade g WHERE g.courseId = ?1 AND g.semester.id = ?2 AND g.deleted = false")
    Page<Grade> findByCourseAndSemester(String courseId, String semesterId, Pageable pageable);

    @Query("SELECT g FROM Grade g JOIN Course c ON g.courseId = c.id WHERE c.teacherId = ?1 AND g.deleted = false")
    Page<Grade> findByCourseTeacherId(String teacherId, Pageable pageable);

    @Query("SELECT AVG(g.averageScore) FROM Grade g WHERE g.courseId = ?1 AND g.semester.id = ?2")
    double calculateAverageScoreByCourseAndSemester(String courseId, String semesterId);

    @Query("SELECT COUNT(g) FROM Grade g WHERE g.courseId = ?1 AND g.semester.id = ?2")
    long countByCourseAndSemester(String courseId, String semesterId);

    @Query("SELECT COUNT(g) FROM Grade g WHERE g.courseId = ?1 AND g.semester.id = ?2 AND g.averageScore >= 5.0")
    long countPassedStudentsByCourseAndSemester(String courseId, String semesterId);

    @Query("SELECT g FROM Grade g WHERE g.deleted = false")
    Page<Grade> findAllActive(Pageable pageable);

    @Query("SELECT g FROM Grade g WHERE g.student.fullName LIKE %?1% AND g.deleted = false")
    Page<Grade> findByStudentNameContainingIgnoreCase(String studentName, Pageable pageable);

    @Query("SELECT g FROM Grade g WHERE g.courseName LIKE %?1% AND g.deleted = false")
    Page<Grade> findByCourseNameContainingIgnoreCase(String courseName, Pageable pageable);

    @Query("SELECT g FROM Grade g WHERE g.semester.semester LIKE %?1% AND g.deleted = false")
    Page<Grade> findBySemesterNameContainingIgnoreCase(String semesterName, Pageable pageable);

    @Query("UPDATE Grade g SET g.deleted = true WHERE g.id = ?1")
    void softDelete(String id);

    @Query("SELECT g FROM Grade g WHERE g.student.fullName = ?1 AND g.courseName = ?2 AND g.semester.semester = ?3 AND g.deleted = false")
    Optional<Grade> findByStudentNameAndCourseNameAndSemesterName(String studentName, String courseName,
            String semesterName);

    Optional<Grade> findByTranscriptIdAndCourseId(String transcriptId, String courseId);

    List<Grade> findByTranscriptId(String transcriptId);

    boolean existsByTranscriptIdAndCourseId(String transcriptId, String courseId);

    boolean existsByCourseIdAndTranscriptAndDeletedFalse(String courseId, Transcript transcript);
}