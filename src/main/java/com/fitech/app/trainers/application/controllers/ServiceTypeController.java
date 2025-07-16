package com.fitech.app.trainers.application.controllers;

import com.fitech.app.trainers.application.dto.ServiceTypeDTO;
import com.fitech.app.trainers.domain.services.ServiceTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/app/service-types")
@Tag(name = "Service Types", description = "Management of available service types and categories")
@SecurityRequirement(name = "bearerAuth")
public class ServiceTypeController {
    
    private final ServiceTypeService serviceTypeService;
    
    public ServiceTypeController(ServiceTypeService serviceTypeService) {
        this.serviceTypeService = serviceTypeService;
    }
    
    /**
     * Obtiene todos los tipos de servicios activos
     */
    @GetMapping
    public ResponseEntity<List<ServiceTypeDTO>> getAllActiveServiceTypes() {
        List<ServiceTypeDTO> serviceTypes = serviceTypeService.getAllActiveServiceTypes();
        return ResponseEntity.ok(serviceTypes);
    }
    
    /**
     * Obtiene un tipo de servicio por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceTypeDTO> getServiceTypeById(@PathVariable Integer id) {
        return serviceTypeService.getServiceTypeById(id)
                .map(serviceType -> ResponseEntity.ok(serviceType))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Busca un tipo de servicio por nombre
     */
    @GetMapping("/by-name")
    public ResponseEntity<ServiceTypeDTO> getServiceTypeByName(@RequestParam String name) {
        return serviceTypeService.getServiceTypeByName(name)
                .map(serviceType -> ResponseEntity.ok(serviceType))
                .orElse(ResponseEntity.notFound().build());
    }
} 