package com.fitech.app.users.application.controllers;

import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.application.dto.MetricTypeDto;
import com.fitech.app.users.domain.services.MetricTypeService;
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
@RequestMapping("/v1/app/metric-type")
@Tag(name = "Metric Types", description = "Management of fitness and health metric types")
@SecurityRequirement(name = "bearerAuth")
public class MetricTypeController {

    @Autowired
    private MetricTypeService metricTypeService;

    @PostMapping
    public ResponseEntity<MetricTypeDto> create(@RequestBody MetricTypeDto dto) {
        return ResponseEntity.ok(metricTypeService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetricTypeDto> update(@PathVariable Integer id, @RequestBody MetricTypeDto dto) {
        MetricTypeDto updated = metricTypeService.update(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetricTypeDto> getById(@PathVariable Integer id) {
        MetricTypeDto dto = metricTypeService.getById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<ResultPage<MetricTypeDto>> getAll(Pageable paging) {
        return ResponseEntity.ok(metricTypeService.getAll(paging));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MetricTypeDto>> getAll() {
        return ResponseEntity.ok(metricTypeService.getAll());
    }
} 