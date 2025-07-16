package com.fitech.app.users.application.controllers;

import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.application.dto.FitnessGoalTypeDto;
import com.fitech.app.users.domain.services.FitnessGoalTypeService;
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
@RequestMapping("/v1/app/fitness-goal-type")
@Tag(name = "Fitness Goal Types", description = "Management of fitness goal categories and types")
@SecurityRequirement(name = "bearerAuth")
public class FitnessGoalTypeController {

    @Autowired
    private FitnessGoalTypeService fitnessGoalTypeService;

    @PostMapping
    public ResponseEntity<FitnessGoalTypeDto> create(@RequestBody FitnessGoalTypeDto dto) {
        return ResponseEntity.ok(fitnessGoalTypeService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FitnessGoalTypeDto> update(@PathVariable Integer id, @RequestBody FitnessGoalTypeDto dto) {
        FitnessGoalTypeDto updated = fitnessGoalTypeService.update(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FitnessGoalTypeDto> getById(@PathVariable Integer id) {
        FitnessGoalTypeDto dto = fitnessGoalTypeService.getById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<ResultPage<FitnessGoalTypeDto>> getAll(Pageable paging) {
        return ResponseEntity.ok(fitnessGoalTypeService.getAll(paging));
    }

    @GetMapping("/all")
    public ResponseEntity<List<FitnessGoalTypeDto>> getAll() {
        return ResponseEntity.ok(fitnessGoalTypeService.getAll());
    }
} 