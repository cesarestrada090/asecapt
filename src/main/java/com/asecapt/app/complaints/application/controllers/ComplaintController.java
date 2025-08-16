package com.asecapt.app.complaints.application.controllers;

import com.asecapt.app.complaints.application.dtos.CreateComplaintRequest;
import com.asecapt.app.complaints.domain.entities.Complaint;
import com.asecapt.app.complaints.domain.services.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
