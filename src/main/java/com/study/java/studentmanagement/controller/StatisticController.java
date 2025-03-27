package com.study.java.studentmanagement.controller;

import com.study.java.studentmanagement.dto.statistic.StatisticResponse;
import com.study.java.studentmanagement.dto.teacher.TeacherResponse;
import com.study.java.studentmanagement.service.StatisticService;
import com.study.java.studentmanagement.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistic")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<StatisticResponse>> getStatisticDashboard() {
        StatisticResponse statisticResponse = statisticService.getStatisticDashboard();
        return ResponseEntity.ok(new ApiResponse<>(statisticResponse));
    }
}
