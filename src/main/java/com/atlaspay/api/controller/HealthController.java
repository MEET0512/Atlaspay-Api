package com.atlaspay.api.controller;

import com.atlaspay.api.dto.ApiResponse;
import com.atlaspay.api.utils.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HealthController {

    /**
     * Health check endpoint
     * GET /api/health
     *
     * @return Health status
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        Map<String, String> healthStatus = new HashMap<>();
        healthStatus.put("status", "UP");
        healthStatus.put("timestamp", LocalDateTime.now().toString());
        healthStatus.put("application", "AtlasPay Secure Gateway");
        healthStatus.put("version", "1.0.0");

        return ResponseUtil.success("Application is running", healthStatus);
    }

    /**
     * Readiness probe endpoint (for Kubernetes, Docker, etc.)
     * GET /api/health/ready
     *
     * @return Ready status
     */
    @GetMapping("/ready")
    public ResponseEntity<ApiResponse<String>> readinessProbe() {
        return ResponseUtil.success("Application is ready", "READY");
    }

    /**
     * Liveness probe endpoint (for Kubernetes, Docker, etc.)
     * GET /api/health/live
     *
     * @return Live status
     */
    @GetMapping("/live")
    public ResponseEntity<ApiResponse<String>> livenessProbe() {
        return ResponseUtil.success("Application is live", "ALIVE");
    }
}
