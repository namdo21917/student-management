package com.study.java.studentmanagement.service;

import com.study.java.studentmanagement.dto.login.LoginRequest;
import com.study.java.studentmanagement.dto.login.LoginResponse;
import com.study.java.studentmanagement.dto.teacher.TeacherResponse;
import com.study.java.studentmanagement.dto.user.UserResponse;
import com.study.java.studentmanagement.model.Teacher;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.repository.TeacherRepository;
import com.study.java.studentmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;

    public LoginResponse login(LoginRequest request) {
        // Implementation remains the same
        return null; // Placeholder
    }

    public UserResponse getUserDetails(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setMsv(user.getMsv());
        response.setFullName(user.getFullName());
        response.setGender(user.getGender());
        response.setMajorId(user.getMajorId());
        response.setYear(user.getYear());
        response.setClassName(user.getClassName());
        response.setAdmin(user.isAdmin());
        response.setGvcn(user.getGvcn());
        response.setGvcnName(user.getGvcnName());
        response.setDeleted(user.isDeleted());
        response.setGv(user.isGV());
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
        response.setGv(teacher.isGV());

        return response;
    }
}