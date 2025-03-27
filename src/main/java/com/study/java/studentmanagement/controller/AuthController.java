package com.study.java.studentmanagement.controller;

import com.study.java.studentmanagement.dto.auth.AuthResponse;
import com.study.java.studentmanagement.dto.login.LoginStudentRequest;
import com.study.java.studentmanagement.dto.login.LoginResponse;
import com.study.java.studentmanagement.dto.login.LoginTeacherRequest;
import com.study.java.studentmanagement.dto.teacher.TeacherResponse;
import com.study.java.studentmanagement.dto.user.UserResponse;
import com.study.java.studentmanagement.service.AuthService;
import com.study.java.studentmanagement.session.TeacherSession;
import com.study.java.studentmanagement.session.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/student/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginStudentRequest request) {
        try {
            LoginResponse loginResponse = authService.studentlogin(request);
            AuthResponse authResponse = new AuthResponse();
            authResponse.setTokens(loginResponse.getTokens());

            if (loginResponse != null && loginResponse.getTokens() != null) {
                // Set tokens in sessions
                UserSession.setAccessToken(loginResponse.getTokens().getAccessToken());
                TeacherSession.setAccessToken(loginResponse.getTokens().getAccessToken());

                // Get user details
                String userId = loginResponse.getDataStudent().getId();

                UserResponse user = authService.getUserDetails(userId);
                UserSession.setUser(user);
                authResponse.setUser(user);

            }

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            log.error("Login error", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/teacher/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginTeacherRequest request) {
        try {
            LoginResponse loginResponse = authService.teacherlogin(request);
            AuthResponse authResponse = new AuthResponse();
            authResponse.setTokens(loginResponse.getTokens());

            if (loginResponse != null && loginResponse.getTokens() != null) {
                // Set tokens in sessions
                UserSession.setAccessToken(loginResponse.getTokens().getAccessToken());
                TeacherSession.setAccessToken(loginResponse.getTokens().getAccessToken());

                // Get user details
                String userId = loginResponse.getDataTeacher().getId();

                TeacherResponse teacher = authService.getTeacherDetails(userId);
                TeacherSession.setTeacher(teacher);
                authResponse.setTeacherData(teacher);

            }

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            log.error("Login error", e);
            return ResponseEntity.badRequest().build();
        }
    }
}