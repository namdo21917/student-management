package com.study.java.studentmanagement.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transcripts")
public class Transcript {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "student_code")
    private String studentCode;

    @Column(name = "semester_id")
    private String semesterId;

    @Column(name = "semester_name")
    private String semesterName;

    private String course;

    @Column(name = "mid_score")
    private Double midScore;

    @Column(name = "final_score")
    private Double finalScore;

    @Column(name = "average_score")
    private Double averageScore;

    private String status;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean deleted;

    @OneToMany(mappedBy = "studentId", fetch = FetchType.LAZY)
    private List<Grade> grades;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Transcript(String studentId, String semesterId) {
        this.studentId = studentId;
        this.semesterId = semesterId;
    }

    @PrePersist
    @PreUpdate
    public void calculateAverageScore() {
        if (midScore != null && finalScore != null) {
            this.averageScore = (midScore * 0.3) + (finalScore * 0.7);
            this.status = (this.averageScore >= 4.0) ? "Passed" : "Failed";
        }
    }
}