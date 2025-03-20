package com.study.java.studentmanagement.repository;

import com.study.java.studentmanagement.model.Grade;
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

    @Query("SELECT g FROM Grade g WHERE g.studentId = ?1 AND g.deleted = false")
    List<Grade> findActiveGradesByStudentId(String studentId);

    @Query("SELECT AVG(g.totalGrade) FROM Grade g WHERE g.studentId = ?1 AND g.semesterId = ?2 AND g.deleted = false")
    Double calculateAverageGradeForSemester(String studentId, String semesterId);

    @Query("SELECT COUNT(g) FROM Grade g WHERE g.studentId = ?1 AND g.totalGrade < 4.0 AND g.deleted = false")
    Long countFailedCourses(String studentId);

    Page<Grade> findByCourseAndSemester(String courseId, String semesterId, Pageable pageable);

    Page<Grade> findByTeacherId(String teacherId, Pageable pageable);

    @Query("SELECT AVG(g.averageScore) FROM Grade g WHERE g.course.id = ?1 AND g.semester.id = ?2")
    double calculateAverageScoreByCourseAndSemester(String courseId, String semesterId);

    @Query("SELECT COUNT(g) FROM Grade g WHERE g.course.id = ?1 AND g.semester.id = ?2")
    long countByCourseAndSemester(String courseId, String semesterId);

    @Query("SELECT COUNT(g) FROM Grade g WHERE g.course.id = ?1 AND g.semester.id = ?2 AND g.averageScore >= 5.0")
    long countPassedStudentsByCourseAndSemester(String courseId, String semesterId);

    @Query("SELECT g FROM Grade g WHERE g.deleted = false")
    Page<Grade> findAllActive(Pageable pageable);

    @Query("SELECT g FROM Grade g WHERE g.student.name LIKE %?1% AND g.deleted = false")
    Page<Grade> findByStudentNameContainingIgnoreCase(String studentName, Pageable pageable);

    @Query("SELECT g FROM Grade g WHERE g.course.name LIKE %?1% AND g.deleted = false")
    Page<Grade> findByCourseNameContainingIgnoreCase(String courseName, Pageable pageable);

    @Query("SELECT g FROM Grade g WHERE g.semester.name LIKE %?1% AND g.deleted = false")
    Page<Grade> findBySemesterNameContainingIgnoreCase(String semesterName, Pageable pageable);

    @Query("UPDATE Grade g SET g.deleted = true WHERE g.id = ?1")
    void softDelete(String id);

    @Query("SELECT g FROM Grade g WHERE g.student.name = ?1 AND g.course.name = ?2 AND g.semester.name = ?3 AND g.deleted = false")
    Optional<Grade> findByStudentNameAndCourseNameAndSemesterName(String studentName, String courseName,
            String semesterName);

    Optional<Grade> findByTranscriptIdAndCourseId(String transcriptId, String courseId);

    List<Grade> findByTranscriptId(String transcriptId);

    boolean existsByTranscriptIdAndCourseId(String transcriptId, String courseId);
}