package com.asecapt.app.users.application.controllers;

import com.asecapt.app.users.domain.entities.User;
import com.asecapt.app.users.domain.services.StudentService;
import com.asecapt.app.users.domain.services.StudentService.CreateStudentRequest;
import com.asecapt.app.users.domain.services.StudentService.UpdateStudentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = {"http://localhost:4200", "https://asecapt.com"})
public class StudentController {

    @Autowired
    private StudentService studentService;

    // Get all students
    @GetMapping
    public ResponseEntity<List<User>> getAllStudents() {
        try {
            List<User> students = studentService.getAllStudents();
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Search students
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchStudents(@RequestParam(required = false) String query) {
        try {
            List<User> students = studentService.searchStudents(query);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get student by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getStudentById(@PathVariable Integer id) {
        try {
            return studentService.getStudentById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Create new student
    @PostMapping
    public ResponseEntity<Object> createStudent(@RequestBody CreateStudentRequest request) {
        try {
            User student = studentService.createStudent(request);
            return ResponseEntity.ok(student);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Update student
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateStudent(@PathVariable Integer id, @RequestBody UpdateStudentRequest request) {
        System.out.println("=== CONTROLLER: UPDATE STUDENT CALLED ===");
        System.out.println("Controller received ID: " + id);
        System.out.println("Request email: " + (request != null ? request.getEmail() : "null"));
        System.out.println("Request document: " + (request != null ? request.getDocumentNumber() : "null"));
        try {
            User student = studentService.updateStudent(id, request);
            return ResponseEntity.ok(student);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Toggle student status (active/inactive)
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<Object> toggleStudentStatus(@PathVariable Integer id) {
        try {
            User student = studentService.toggleStudentStatus(id);
            return ResponseEntity.ok(student);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Get student statistics
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStudentStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalStudents", studentService.getStudentsCount());
            stats.put("activeStudents", studentService.getActiveStudentsCount());
            stats.put("inactiveStudents", studentService.getStudentsCount() - studentService.getActiveStudentsCount());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 