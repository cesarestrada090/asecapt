package com.fitech.app.users.application.controllers;

import com.fitech.app.commons.application.controllers.BaseController;
import com.fitech.app.users.application.dto.MetricTypeUOMDto;
import com.fitech.app.users.application.dto.MetricTypeUOMDetailDto;
import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.domain.services.MetricTypeUOMService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/app/metric-type-uom")
@Tag(name = "Metric Type UOM", description = "Management of metric types and their units of measure")
@SecurityRequirement(name = "bearerAuth")
public class MetricTypeUOMController extends BaseController {

    private final MetricTypeUOMService metricTypeUomService;

    @Autowired
    public MetricTypeUOMController(MetricTypeUOMService metricTypeUomService) {
        this.metricTypeUomService = metricTypeUomService;
    }

    @PostMapping
    public ResponseEntity<MetricTypeUOMDto> create(@Valid @RequestBody MetricTypeUOMDto metricTypeUomDto) {
        MetricTypeUOMDto created = metricTypeUomService.create(metricTypeUomDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetricTypeUOMDto> update(@PathVariable Integer id, @Valid @RequestBody MetricTypeUOMDto metricTypeUomDto) {
        MetricTypeUOMDto updated = metricTypeUomService.update(id, metricTypeUomDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        metricTypeUomService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetricTypeUOMDto> findById(@PathVariable Integer id) {
        MetricTypeUOMDto metricTypeUom = metricTypeUomService.findById(id);
        return ResponseEntity.ok(metricTypeUom);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable paging = PageRequest.of(page - 1, size);
        ResultPage<MetricTypeUOMDetailDto> resultPageWrapper = metricTypeUomService.findAll(paging);
        Map<String, Object> response = prepareResponse(resultPageWrapper);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/by-metric-type/{metricTypeId}")
    public ResponseEntity<Map<String, Object>> findByMetricType(
            @PathVariable Integer metricTypeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable paging = PageRequest.of(page - 1, size);
        ResultPage<MetricTypeUOMDetailDto> resultPageWrapper = metricTypeUomService.findByMetricType(metricTypeId, paging);
        Map<String, Object> response = prepareResponse(resultPageWrapper);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    protected String getResource() {
        return "metricTypeUoms";
    }
} 