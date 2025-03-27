package com.study.java.studentmanagement.dto.login;

import com.study.java.studentmanagement.dto.teacher.TeacherResponse;
import com.study.java.studentmanagement.dto.user.UserResponse;
import lombok.Data;

@Data
public class LoginResponse {
    private TokenResponse tokens;
    private UserResponse dataStudent;
    private TeacherResponse dataTeacher;
}