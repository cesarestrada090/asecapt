package com.asecapt.app.users.application.controllers;

import com.asecapt.app.users.domain.entities.User;
import com.asecapt.app.users.domain.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            // Validate input
            if (request.getUsername() == null || request.getUsername().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("INVALID_INPUT", "Username and password are required"));
            }

            // Find user by username
            Optional<User> userOptional = userService.findByUsername(request.getUsername().trim());
            
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("INVALID_CREDENTIALS", "Invalid username or password"));
            }

            User user = userOptional.get();

            // Check if user is active
            if (!user.isActive()) {
                return ResponseEntity.badRequest().body(createErrorResponse("USER_INACTIVE", "User account is inactive"));
            }

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body(createErrorResponse("INVALID_CREDENTIALS", "Invalid username or password"));
            }

            // Check if user is admin (type = 1)
            if (user.getType() != 1) {
                return ResponseEntity.badRequest().body(createErrorResponse("INSUFFICIENT_PRIVILEGES", "Access denied. Admin privileges required"));
            }

            // Login successful - return user info (without password)
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", createUserResponse(user));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("INTERNAL_ERROR", "An error occurred during login"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateSession() {
        // For now, we'll implement a simple validation
        // In a real-world scenario, you'd validate JWT tokens or session cookies
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Session valid");
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> createErrorResponse(String errorCode, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("errorCode", errorCode);
        response.put("message", message);
        return response;
    }

    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("type", user.getType());
        userInfo.put("active", user.isActive());
        userInfo.put("isEmailVerified", user.getIsEmailVerified());
        
        // Include person info if available
        if (user.getPerson() != null) {
            Map<String, Object> personInfo = new HashMap<>();
            personInfo.put("firstName", user.getPerson().getFirstName());
            personInfo.put("lastName", user.getPerson().getLastName());
            personInfo.put("email", user.getPerson().getEmail());
            personInfo.put("documentNumber", user.getPerson().getDocumentNumber());
            userInfo.put("person", personInfo);
        }
        
        return userInfo;
    }

    // Request DTO
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}