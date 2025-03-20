package com.study.java.studentmanagement.controller;

import com.study.java.studentmanagement.dto.user.UserRequest;
import com.study.java.studentmanagement.dto.user.UserResponse;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.service.UserService;
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
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/create-user")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/updateByAdmin/{id}")
    public ResponseEntity<UserResponse> updateUserByAdmin(
            @PathVariable String id,
            @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUserByAdmin(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/restore/{msv}")
    public ResponseEntity<UserResponse> restoreUser(@PathVariable String msv) {
        return ResponseEntity.ok(userService.restoreUser(msv));
    }

    @PutMapping("/updateProfile/{id}")
    public ResponseEntity<UserResponse> updateUserProfile(
            @PathVariable String id,
            @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUserProfile(id, request));
    }

    @PostMapping("/searchStudents")
    public ResponseEntity<List<UserResponse>> searchStudents(@RequestParam String keyword) {
        return ResponseEntity.ok(userService.searchStudents(keyword));
    }
}