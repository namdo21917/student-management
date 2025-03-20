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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(name = "midterm_score", nullable = false)
    private double midtermScore;

    @Column(name = "final_score", nullable = false)
    private double finalScore;

    @Column(name = "average_score", nullable = false)
    private double averageScore;

    @Column(nullable = false)
    private boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public Semester getSemester() {
        return semester;
    }

    public double getMidtermScore() {
        return midtermScore;
    }

    public double getFinalScore() {
        return finalScore;
    }

    // Custom method to calculate total grade
    public double calculateTotalGrade() {
        return (midtermScore * 0.4) + (finalScore * 0.6);
    }
}