package com.study.java.studentmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID of the user to retrieve") @PathVariable String id) {
        return ResponseEntity.ok(new UserResponse());
    }

    @Operation(summary = "Create new user", description = "Creates a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Parameter(description = "User details") @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(new UserResponse());
    }

    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID of the user to update") @PathVariable String id,
            @Parameter(description = "Updated user details") @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(new UserResponse());
    }

    @Operation(summary = "Delete user", description = "Deletes a user from the system")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete") @PathVariable String id) {
        return ResponseEntity.noContent().build();
    }

    // DTO classes for example
    public static class UserResponse {
        @Schema(description = "User's unique identifier")
        private String id;

        @Schema(description = "User's full name")
        private String fullName;

        @Schema(description = "User's email address")
        private String email;

        @Schema(description = "User's role in the system")
        private String role;
    }

    public static class CreateUserRequest {
        @Schema(description = "User's full name", required = true)
        private String fullName;

        @Schema(description = "User's email address", required = true)
        private String email;

        @Schema(description = "User's password", required = true, minLength = 8)
        private String password;
    }

    public static class UpdateUserRequest {
        @Schema(description = "User's full name")
        private String fullName;

        @Schema(description = "User's email address")
        private String email;
    }
}