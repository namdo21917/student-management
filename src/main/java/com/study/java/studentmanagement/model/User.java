package com.study.java.studentmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "msv", unique = true, nullable = false)
    private String msv;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String gender;

    @Column(name = "major_name")
    private String majorName;

    private String year;

    @Column(name = "class_name")
    private String className;

    @Column(unique = true)
    private String email;

    private String phone;
    private String country;
    private String address;
    private String dob;

    @Column(name = "gvcn")
    private String gvcn;

    @Column(name = "gvcn_name")
    private String gvcnName;

    @Column(name = "is_admin", columnDefinition = "boolean default false")
    private boolean isAdmin;

    @Column(name = "is_gv", columnDefinition = "boolean default false")
    private boolean isGV;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean deleted;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Grade> grades;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return fullName;
    }
}