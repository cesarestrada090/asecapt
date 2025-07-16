package com.fitech.app.trainers.application.controllers;

import com.fitech.app.trainers.application.dto.CreateContractDto;
import com.fitech.app.trainers.application.dto.ServiceContractDto;
import com.fitech.app.trainers.domain.services.ServiceContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/app/contracts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Service Contracts", description = "Service contract management between trainers and clients")
@SecurityRequirement(name = "bearerAuth")
public class ServiceContractController {

    private final ServiceContractService contractService;

    @PostMapping
    public ResponseEntity<?> createContract(@Valid @RequestBody CreateContractDto createDto) {
        try {
            log.info("Creating contract for service {} and client {}", createDto.getServiceId(), createDto.getClientId());
            ServiceContractDto contract = contractService.createContract(createDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(contract);
        } catch (RuntimeException e) {
            log.error("Error creating contract: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating contract", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContractById(@PathVariable Integer id) {
        try {
            return contractService.getContractById(id)
                .map(contract -> ResponseEntity.ok().body(contract))
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting contract by ID: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<?> getContractsByClient(@PathVariable Integer clientId) {
        try {
            List<ServiceContractDto> contracts = contractService.getContractsByClient(clientId);
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            log.error("Error getting contracts by client: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<?> getContractsByTrainer(@PathVariable Integer trainerId) {
        try {
            List<ServiceContractDto> contracts = contractService.getContractsByTrainer(trainerId);
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            log.error("Error getting contracts by trainer: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<?> getContractsByService(@PathVariable Integer serviceId) {
        try {
            List<ServiceContractDto> contracts = contractService.getContractsByService(serviceId);
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            log.error("Error getting contracts by service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/client/{clientId}/active")
    public ResponseEntity<?> getActiveContractsByClient(@PathVariable Integer clientId) {
        try {
            List<ServiceContractDto> contracts = contractService.getActiveContractsByClient(clientId);
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            log.error("Error getting active contracts by client: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/client/{clientId}/inactive")
    public ResponseEntity<?> getInactiveContractsByClient(@PathVariable Integer clientId) {
        try {
            List<ServiceContractDto> contracts = contractService.getInactiveContractsByClient(clientId);
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            log.error("Error getting inactive contracts by client: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/client/{clientId}/status/{status}")
    public ResponseEntity<?> getContractsByClientAndStatus(@PathVariable Integer clientId, @PathVariable String status) {
        try {
            List<ServiceContractDto> contracts = contractService.getContractsByClientAndStatus(clientId, status);
            return ResponseEntity.ok(contracts);
        } catch (RuntimeException e) {
            log.error("Error getting contracts by client and status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error getting contracts by client and status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/trainer/{trainerId}/active")
    public ResponseEntity<?> getActiveContractsByTrainer(@PathVariable Integer trainerId) {
        try {
            List<ServiceContractDto> contracts = contractService.getActiveContractsByTrainer(trainerId);
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            log.error("Error getting active contracts by trainer: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/check-availability")
    public ResponseEntity<?> checkServiceAvailability(@RequestParam Integer clientId, @RequestParam Integer serviceId) {
        try {
            boolean canContract = contractService.canClientContractService(clientId, serviceId);
            return ResponseEntity.ok(Map.of(
                "canContract", canContract,
                "message", canContract ? "Servicio disponible para contratar" : "Ya tienes un contrato activo para este servicio"
            ));
        } catch (Exception e) {
            log.error("Error checking service availability: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @PutMapping("/{contractId}/activate")
    public ResponseEntity<?> activateContract(@PathVariable Integer contractId) {
        try {
            ServiceContractDto contract = contractService.activateContract(contractId);
            return ResponseEntity.ok(contract);
        } catch (RuntimeException e) {
            log.error("Error activating contract: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error activating contract", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @PutMapping("/{contractId}/complete")
    public ResponseEntity<?> completeContract(@PathVariable Integer contractId) {
        try {
            ServiceContractDto contract = contractService.completeContract(contractId);
            return ResponseEntity.ok(contract);
        } catch (RuntimeException e) {
            log.error("Error completing contract: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error completing contract", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @PutMapping("/{contractId}/cancel")
    public ResponseEntity<?> cancelContract(@PathVariable Integer contractId) {
        try {
            ServiceContractDto contract = contractService.cancelContract(contractId);
            return ResponseEntity.ok(contract);
        } catch (RuntimeException e) {
            log.error("Error cancelling contract: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error cancelling contract", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/trainer/{trainerId}/stats")
    public ResponseEntity<?> getTrainerStats(@PathVariable Integer trainerId) {
        try {
            ServiceContractService.ContractStats stats = contractService.getStatsForTrainer(trainerId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting trainer stats: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }
} 