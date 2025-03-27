package com.study.java.studentmanagement.service;

import com.study.java.studentmanagement.dto.login.LoginStudentRequest;
import com.study.java.studentmanagement.dto.login.LoginResponse;
import com.study.java.studentmanagement.dto.login.LoginTeacherRequest;
import com.study.java.studentmanagement.dto.login.TokenResponse;
import com.study.java.studentmanagement.dto.teacher.TeacherResponse;
import com.study.java.studentmanagement.dto.user.UserResponse;
import com.study.java.studentmanagement.model.Teacher;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.repository.TeacherRepository;
import com.study.java.studentmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;

    public LoginResponse studentlogin(LoginStudentRequest request) {
        // Tìm user theo msv
        User user = userRepository.findByMsv(request.getMsv())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Kiểm tra password (trong thực tế nên hash password)
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Tạo token đơn giản (trong thực tế nên dùng JWT)
        String token = UUID.randomUUID().toString();

        // Tạo response
        LoginResponse response = new LoginResponse();

        // Set token
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(token);
        response.setTokens(tokenResponse);

        // Set user data
        LoginResponse loginStudentData = new LoginResponse();
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setMsv(user.getMsv());
        userResponse.setFullName(user.getFullName());
        userResponse.setGender(user.getGender());
        userResponse.setMajorId(user.getMajor().getId());
        userResponse.setYear(user.getYear());
        userResponse.setClassName(user.getClassName());
        userResponse.setAdmin(user.isAdmin());
        userResponse.setGvcn(user.getGvcn());
        userResponse.setGvcnName(user.getGvcnName());
        userResponse.setDeleted(user.isDeleted());
        userResponse.setEmail(user.getEmail());
        userResponse.setDob(user.getDob());
        userResponse.setPhone(user.getPhone());
        userResponse.setCountry(user.getCountry());
        userResponse.setAddress(user.getAddress());

        response.setDataStudent(userResponse);

        return response;
    }

    public LoginResponse teacherlogin(LoginTeacherRequest request) {
        // Tìm user theo msv
        Teacher teacher = teacherRepository.findByMgv(request.getMgv())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Kiểm tra password (trong thực tế nên hash password)
        if (!teacher.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Tạo token đơn giản (trong thực tế nên dùng JWT)
        String token = UUID.randomUUID().toString();

        // Tạo response
        LoginResponse response = new LoginResponse();

        // Set token
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(token);
        response.setTokens(tokenResponse);

        // Set user data

        TeacherResponse teacherResponse = new TeacherResponse();
        teacherResponse.setId(teacher.getId());
        teacherResponse.setFullName(teacher.getFullName());
        teacherResponse.setAdmin(teacher.isAdmin());
        teacherResponse.setDeleted(teacher.isDeleted());
        teacherResponse.setEmail(teacher.getEmail());
        teacherResponse.setGV(teacher.isGv());
        teacherResponse.setMgv(teacher.getMgv());

        response.setDataTeacher(teacherResponse);

        return response;
    }

    public UserResponse getUserDetails(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setMsv(user.getMsv());
        response.setFullName(user.getFullName());
        response.setGender(user.getGender());
        response.setMajorId(user.getMajor().getId());
        response.setYear(user.getYear());
        response.setClassName(user.getClassName());
        response.setAdmin(user.isAdmin());
        response.setGvcn(user.getGvcn());
        response.setGvcnName(user.getGvcnName());
        response.setDeleted(user.isDeleted());
        response.setEmail(user.getEmail());
        response.setDob(user.getDob());
        response.setPhone(user.getPhone());
        response.setCountry(user.getCountry());
        response.setAddress(user.getAddress());

        return response;
    }

    public TeacherResponse getTeacherDetails(String userId) {
        Teacher teacher = teacherRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        TeacherResponse response = new TeacherResponse();
        response.setId(teacher.getId());
        response.setMgv(teacher.getMgv());
        response.setFullName(teacher.getFullName());
        response.setEmail(teacher.getEmail());
        response.setGV(teacher.isGv());

        return response;
    }
}