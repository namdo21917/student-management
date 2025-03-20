package com.study.java.studentmanagement.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Getter
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