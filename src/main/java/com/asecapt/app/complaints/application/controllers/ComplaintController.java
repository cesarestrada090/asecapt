package com.asecapt.app.complaints.application.controllers;

import com.asecapt.app.complaints.application.dtos.CreateComplaintRequest;
import com.asecapt.app.complaints.domain.entities.Complaint;
import com.asecapt.app.complaints.domain.services.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<Complaint> createComplaint(@Valid @RequestBody CreateComplaintRequest request) {
        Complaint complaint = complaintService.createComplaint(request);
        return ResponseEntity.ok(complaint);
    }

    @GetMapping("/{complaintNumber}")
    public ResponseEntity<Complaint> getComplaintByNumber(@PathVariable String complaintNumber) {
        Optional<Complaint> complaint = complaintService.findByComplaintNumber(complaintNumber);
        
        if (complaint.isPresent()) {
            return ResponseEntity.ok(complaint.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        List<Complaint> complaints = complaintService.getAllComplaints();
        return ResponseEntity.ok(complaints);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Complaint> updateComplaintStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        Optional<Complaint> complaint = complaintService.updateStatus(id, status);
        
        if (complaint.isPresent()) {
            return ResponseEntity.ok(complaint.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/response")
    public ResponseEntity<Complaint> addResponse(
            @PathVariable Long id, 
            @RequestBody Map<String, String> responseData) {
        String response = responseData.get("response");
        Optional<Complaint> complaint = complaintService.addResponse(id, response);
        
        if (complaint.isPresent()) {
            return ResponseEntity.ok(complaint.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
