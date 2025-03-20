package com.study.java.studentmanagement.dto.auth;

import com.study.java.studentmanagement.dto.login.TokenResponse;
import com.study.java.studentmanagement.dto.teacher.TeacherResponse;
import com.study.java.studentmanagement.dto.user.UserResponse;
import lombok.Data;

@Data
public class AuthResponse {
    private TokenResponse tokens;
    private UserResponse user;
    private TeacherResponse teacherData;
    private boolean isTeacher;

    public void setTeacher(boolean isTeacher) {
        this.isTeacher = isTeacher;
    }

    public void setTeacher(TeacherResponse teacher) {
        this.teacherData = teacher;
    }
}