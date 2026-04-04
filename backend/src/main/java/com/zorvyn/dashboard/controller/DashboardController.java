package com.zorvyn.dashboard.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zorvyn.dashboard.dto.dashboard.CategoryTotalResponse;
import com.zorvyn.dashboard.dto.dashboard.MonthlyTrendResponse;
import com.zorvyn.dashboard.dto.dashboard.SummaryResponse;
import com.zorvyn.dashboard.dto.record.RecordResponse;
import com.zorvyn.dashboard.service.DashboardService;
import com.zorvyn.dashboard.utils.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints for dashboard analytics")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @Operation(summary = "Get overall totals and net balance")
    public ResponseEntity<ApiResponse<SummaryResponse>> getSummary(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getSummary(auth.getName())));
    }

    @GetMapping("/by-category")
    @Operation(summary = "Get totals grouped by category")
    public ResponseEntity<ApiResponse<List<CategoryTotalResponse>>> getTotalsByCategory(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getTotalsByCategory(auth.getName())));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent 10 transactions")
    public ResponseEntity<ApiResponse<Page<RecordResponse>>> getRecentActivity(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getRecentActivity(auth.getName())));
    }

    @GetMapping("/trends")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(summary = "Get monthly income/expense trends (ANALYST/ADMIN only)")
    public ResponseEntity<ApiResponse<List<MonthlyTrendResponse>>> getTrends(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getMonthlyTrends(auth.getName())));
    }
}
