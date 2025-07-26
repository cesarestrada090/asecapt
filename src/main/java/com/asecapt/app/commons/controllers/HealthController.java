package com.asecapt.app.commons.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "ASECAPT API");
        response.put("version", "1.0");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/swagger")
    public ResponseEntity<Map<String, Object>> swaggerHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("swagger-ui", "Available at /swagger-ui/index.html");
        response.put("api-docs", "Available at /v3/api-docs");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
} 