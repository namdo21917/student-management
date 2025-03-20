package com.study.java.studentmanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "semesters")
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String semester;

    @Column(nullable = false)
    private String group;

    @Column(nullable = false)
    private String year;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private boolean active;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean deleted;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}