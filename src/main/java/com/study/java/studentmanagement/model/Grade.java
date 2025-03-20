package com.study.java.studentmanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "course_id", nullable = false)
    private String courseId;

    @Column(name = "transcript_id", nullable = false)
    private String transcriptId;

    @Column(name = "mid_score", nullable = false)
    private double midScore;

    @Column(name = "final_score", nullable = false)
    private double finalScore;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean deleted;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}