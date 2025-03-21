package com.study.java.studentmanagement.controller;

import com.study.java.studentmanagement.dto.user.UserRequest;
import com.study.java.studentmanagement.dto.user.UserResponse;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.service.UserService;
import com.study.java.studentmanagement.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<List<UserResponse>>(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<UserResponse>(user));
    }

    @PostMapping("/create-user")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody UserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.ok(new ApiResponse<UserResponse>(user));
    }

    @PutMapping("/updateByAdmin/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserByAdmin(
            @PathVariable String id,
            @RequestBody UserRequest request) {
        UserResponse user = userService.updateUserByAdmin(id, request);
        return ResponseEntity.ok(new ApiResponse<UserResponse>(user));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<Void>(null));
    }

    @PutMapping("/restore/{msv}")
    public ResponseEntity<ApiResponse<UserResponse>> restoreUser(@PathVariable String msv) {
        UserResponse user = userService.restoreUser(msv);
        return ResponseEntity.ok(new ApiResponse<UserResponse>(user));
    }

    @PutMapping("/updateProfile/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserProfile(
            @PathVariable String id,
            @RequestBody UserRequest request) {
        UserResponse user = userService.updateUserProfile(id, request);
        return ResponseEntity.ok(new ApiResponse<UserResponse>(user));
    }

    @GetMapping("/searchStudents")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchStudents(@RequestParam String keyword) {
        List<UserResponse> users = userService.searchStudents(keyword);
        return ResponseEntity.ok(new ApiResponse<List<UserResponse>>(users));
    }
}