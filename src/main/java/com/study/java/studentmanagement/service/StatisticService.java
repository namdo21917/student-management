package com.study.java.studentmanagement.service;

import com.study.java.studentmanagement.dto.statistic.StatisticResponse;
import com.study.java.studentmanagement.dto.user.UserRequest;
import com.study.java.studentmanagement.dto.user.UserResponse;
import com.study.java.studentmanagement.model.Major;
import com.study.java.studentmanagement.model.Semester;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final UserRepository userRepository;
    private final MajorRepository majorRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final TeacherRepository teacherRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public StatisticResponse getStatisticDashboard() {
        Long countStudent = userRepository.count();
        Long countCourse = courseRepository.count();
        Long countTeachers = teacherRepository.count();
        Optional<Semester> activeSemester = semesterRepository.findActiveSemesters().stream().findFirst();

        StatisticResponse statisticResponse = new StatisticResponse();
        statisticResponse.setTotalStudents(countStudent);
        statisticResponse.setTotalCourse(countCourse);
        statisticResponse.setTotalTeacher(countTeachers);
        statisticResponse.setActiveSemester(activeSemester.get().getSemester());

        return statisticResponse;
    }

    public UserResponse createUser(UserRequest request) {
        User user = new User();
        updateUserFromRequest(user, request);
        return convertToResponse(userRepository.save(user));
    }

    public UserResponse updateUserByAdmin(String id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        updateUserFromRequest(user, request);
        return convertToResponse(userRepository.save(user));
    }

    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setDeleted(true);
        userRepository.save(user);
    }

    public UserResponse restoreUser(String msv) {
        User user = userRepository.findByMsv(msv)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setDeleted(false);
        return convertToResponse(userRepository.save(user));
    }

    public UserResponse updateUserProfile(String id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        updateUserProfileFromRequest(user, request);
        return convertToResponse(userRepository.save(user));
    }

    public List<UserResponse> searchStudents(String keyword) {
        return userRepository.findByFullNameContainingIgnoreCaseOrMsvContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private void updateUserFromRequest(User user, UserRequest request) {
        user.setFullName(request.getFullName());
        user.setMsv(request.getMsv());
        user.setPassword(request.getPassword());
        user.setYear(request.getYear());
        user.setGvcn(request.getGvcn());
        user.setGender(request.getGender());
        user.setClassName(request.getClassName());
        user.setEmail(request.getEmail());
        if (request.getMajorId() != null) {
            Major major = majorRepository.findById(request.getMajorId())
                    .orElseThrow(() -> new RuntimeException("Major not found"));
            user.setMajor(major);
            user.setMajorName(major.getName());
        }
        user.setPhone(request.getPhone());
        user.setCountry(request.getCountry());
        user.setAddress(request.getAddress());
        user.setDob(request.getDob());
    }

    private void updateUserProfileFromRequest(User user, UserRequest request) {
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setDob(request.getDob());
        user.setGender(request.getGender());
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setMsv(user.getMsv());
        response.setFullName(user.getFullName());
        response.setGender(user.getGender());
        response.setMajorId(user.getMajor() != null ? user.getMajor().getId() : null);
        response.setYear(user.getYear());
        response.setClassName(user.getClassName());
        response.setAdmin(user.isAdmin());
        response.setGvcn(user.getGvcn());
        response.setGvcnName(user.getGvcnName());
        response.setDeleted(user.isDeleted());
        response.setGV(user.isGV());
        response.setEmail(user.getEmail());
        response.setDob(user.getDob());
        response.setPhone(user.getPhone());
        response.setCountry(user.getCountry());
        response.setAddress(user.getAddress());
        return response;
    }
}