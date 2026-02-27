package com.atlaspay.api.controller;

import com.atlaspay.api.dto.ApiResponse;
import com.atlaspay.api.utils.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    /**
     * Get system statistics (Admin only)
     * GET /api/admin/statistics
     *
     * @return System statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalUsers", 0);
        statistics.put("totalTransactions", 0);
        statistics.put("totalTransactionValue", 0);

        return ResponseUtil.success("System statistics retrieved", statistics);
    }

    /**
     * Get dashboard information (Admin only)
     * GET /api/admin/dashboard
     *
     * @return Dashboard data
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("activeUsers", 0);
        dashboard.put("pendingTransactions", 0);
        dashboard.put("completedTransactions", 0);
        dashboard.put("failedTransactions", 0);

        return ResponseUtil.success("Dashboard retrieved", dashboard);
    }
}
