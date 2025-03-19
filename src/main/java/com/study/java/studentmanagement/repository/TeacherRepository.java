package com.study.java.studentmanagement.repository;

import com.study.java.studentmanagement.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, String> {
    Optional<Teacher> findByMgv(String mgv);

    Optional<Teacher> findByEmail(String email);

    List<Teacher> findByFullNameContainingIgnoreCase(String fullName);

    @Query("SELECT t FROM Teacher t WHERE t.isGV = true AND t.deleted = false")
    List<Teacher> findAllActiveTeachers();

    boolean existsByMgv(String mgv);

    boolean existsByEmail(String email);

    @Query("SELECT t FROM Teacher t WHERE t.deleted = false AND " +
            "(LOWER(t.fullName) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(t.mgv) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(t.email) LIKE LOWER(CONCAT('%', ?1, '%')))")
    List<Teacher> searchTeachers(String keyword);
}