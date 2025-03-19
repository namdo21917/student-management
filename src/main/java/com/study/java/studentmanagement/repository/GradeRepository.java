package com.study.java.studentmanagement.repository;

import com.study.java.studentmanagement.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}