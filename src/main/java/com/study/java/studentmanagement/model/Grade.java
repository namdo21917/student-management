package com.study.java.studentmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "semester_id")
    private String semesterId;

    @Column(name = "attendance_grade")
    private Double attendanceGrade;

    @Column(name = "midterm_grade")
    private Double midtermGrade;

    @Column(name = "final_grade")
    private Double finalGrade;

    @Column(name = "total_grade")
    private Double totalGrade;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean deleted;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Custom method to calculate total grade
    @PrePersist
    @PreUpdate
    public void calculateTotalGrade() {
        if (attendanceGrade != null && midtermGrade != null && finalGrade != null) {
            this.totalGrade = (attendanceGrade * 0.1) + (midtermGrade * 0.3) + (finalGrade * 0.6);
        }
    }
}