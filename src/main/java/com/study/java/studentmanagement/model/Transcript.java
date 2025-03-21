package com.study.java.studentmanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "transcripts")
public class Transcript {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "student_code", nullable = false)
    private String studentCode;

    @Column(name = "semester_id", nullable = false)
    private String semesterId;

    @Column(name = "semester_name", nullable = false)
    private String semesterName;

    @OneToMany(mappedBy = "transcript", cascade = CascadeType.ALL)
    private List<Grade> grades;

    @Column(name = "deleted")
    private boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}