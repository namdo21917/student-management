package com.study.java.studentmanagement.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
//    private boolean success;
//    private String message;
//    private T data;
//    private HttpStatus status;
//    @Builder.Default
//    private LocalDateTime timestamp = LocalDateTime.now();
//
//    public static <T> ApiResponse<T> success(T data) {
//        return ApiResponse.<T>builder()
//                .success(true)
//                .data(data)
//                .status(HttpStatus.OK)
//                .message("Success")
//                .build();
//    }
//
//    public static <T> ApiResponse<T> success(T data, String message) {
//        return ApiResponse.<T>builder()
//                .success(true)
//                .data(data)
//                .status(HttpStatus.OK)
//                .message(message)
//                .build();
//    }
//
//    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
//        return ApiResponse.<T>builder()
//                .success(false)
//                .message(message)
//                .status(status)
//                .build();
//    }
}