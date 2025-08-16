package com.asecapt.app.complaints.domain.services;

import com.asecapt.app.complaints.application.dtos.CreateComplaintRequest;
import com.asecapt.app.complaints.domain.entities.Complaint;
import com.asecapt.app.complaints.infrastructure.repositories.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    public Complaint createComplaint(CreateComplaintRequest request) {
        Complaint complaint = new Complaint();
        complaint.setType(request.getType());
        complaint.setName(request.getName());
        complaint.setEmail(request.getEmail());
        complaint.setPhone(request.getPhone());
        complaint.setDocument(request.getDocument());
        complaint.setDescription(request.getDescription());
        complaint.setStatus("pendiente");
        
        return complaintRepository.save(complaint);
    }

    public Optional<Complaint> findByComplaintNumber(String complaintNumber) {
        return complaintRepository.findByComplaintNumber(complaintNumber);
    }
}
