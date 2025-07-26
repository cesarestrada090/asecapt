package com.asecapt.app.users.application.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/logs")
public class LogSnippetController {

    @GetMapping("/tail")
    public ResponseEntity<String> getLogTail() {
        Path logPath = Paths.get("/home/ec2-user/core_backend/target/app.log");
        int linesToShow = 600;
        try {
            if (!Files.exists(logPath)) {
                return ResponseEntity.status(404).body("file not found: " + logPath.toString());
            }
            List<String> allLines = Files.readAllLines(logPath);
            List<String> lastLines = allLines.stream()
                    .skip(Math.max(0, allLines.size() - linesToShow))
                    .collect(Collectors.toList());
            String result = String.join("\n", lastLines);
            return ResponseEntity.ok()
                    .header("Content-Type", "text/plain")
                    .body(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error reading log: " + e.getMessage());
        }
    }
} 