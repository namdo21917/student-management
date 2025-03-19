package com.study.java.studentmanagement.repository;

import com.study.java.studentmanagement.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    // Tìm kiếm cơ bản
    Optional<Course> findByCode(String code);

    List<Course> findByNameContainingIgnoreCase(String name);

    List<Course> findByMajorId(String majorId);

    // Kiểm tra tồn tại
    boolean existsByCode(String code);

    // Các phương thức tìm kiếm với soft delete
    @Query("SELECT c FROM Course c WHERE c.deleted = false")
    List<Course> findAllActive();

    @Query("SELECT c FROM Course c WHERE c.deleted = false")
    Page<Course> findAllActive(Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.majorId = ?1 AND c.deleted = false")
    List<Course> findActiveCoursesByMajor(String majorId);

    // Tìm kiếm nâng cao
    @Query("SELECT c FROM Course c WHERE c.deleted = false AND " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(c.code) LIKE LOWER(CONCAT('%', ?1, '%')))")
    List<Course> searchCourses(String keyword);

    // Thống kê
    @Query("SELECT COUNT(c) FROM Course c WHERE c.majorId = ?1 AND c.deleted = false")
    Long countCoursesByMajor(String majorId);

    @Query("SELECT SUM(c.credit) FROM Course c WHERE c.majorId = ?1 AND c.deleted = false")
    Integer sumCreditsByMajor(String majorId);

    // Sắp xếp theo tín chỉ
    @Query("SELECT c FROM Course c WHERE c.deleted = false ORDER BY c.credit DESC")
    List<Course> findAllActiveOrderByCreditsDesc();
}