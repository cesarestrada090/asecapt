package com.fitech.app.users.application.controllers;

import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.application.dto.FitnessGoalStatusDto;
import com.fitech.app.users.domain.services.FitnessGoalStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/app/fitness-goal-status")
@Tag(name = "Fitness Goal Status", description = "Management of fitness goal status tracking")
@SecurityRequirement(name = "bearerAuth")
public class FitnessGoalStatusController {

    @Autowired
    private FitnessGoalStatusService fitnessGoalStatusService;

    @PostMapping
    public ResponseEntity<FitnessGoalStatusDto> create(@RequestBody FitnessGoalStatusDto dto) {
        return ResponseEntity.ok(fitnessGoalStatusService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FitnessGoalStatusDto> update(@PathVariable Integer id, @RequestBody FitnessGoalStatusDto dto) {
        FitnessGoalStatusDto updated = fitnessGoalStatusService.update(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FitnessGoalStatusDto> getById(@PathVariable Integer id) {
        FitnessGoalStatusDto dto = fitnessGoalStatusService.getById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<ResultPage<FitnessGoalStatusDto>> getAll(Pageable paging) {
        return ResponseEntity.ok(fitnessGoalStatusService.getAll(paging));
    }

    @GetMapping("/all")
    public ResponseEntity<List<FitnessGoalStatusDto>> getAll() {
        return ResponseEntity.ok(fitnessGoalStatusService.getAll());
    }
} 