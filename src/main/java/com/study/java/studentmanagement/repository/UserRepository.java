package com.study.java.studentmanagement.repository;

import com.study.java.studentmanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByMsv(String msv);

    Optional<User> findByEmail(String email);

    List<User> findByMajorId(String majorId);

    List<User> findByGvcn(String gvcn);

    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.isAdmin = false AND u.isGV = false")
    Page<User> findAllActiveStudents(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.deleted = false AND " +
            "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(u.msv) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', ?1, '%')))")
    List<User> searchUsers(String keyword);

    List<User> findByClassName(String className);

    List<User> findByYear(String year);

    boolean existsByMsv(String msv);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.major.id = ?1 AND u.deleted = false")
    Long countStudentsByMajor(String majorId);

    @Query("SELECT u FROM User u WHERE u.major.id = ?1 AND u.deleted = false")
    Page<User> findStudentsByMajor(String majorId, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.username = ?#{@securityService.getCurrentUsername()}")
    User findCurrentUser();

    @Query("SELECT u FROM User u WHERE u.isAdmin = false AND u.isGV = false AND u.deleted = false")
    List<User> findAllStudents();

    List<User> findByFullNameContainingIgnoreCaseOrMsvContainingIgnoreCase(String fullName, String msv);
}