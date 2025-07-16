package com.fitech.app.trainers.application.controllers;

import com.fitech.app.trainers.application.dto.ClientResourceGroupDto;
import com.fitech.app.trainers.application.dto.ServiceResourceDto;
import com.fitech.app.trainers.application.dto.ClientResourceResponseDto;
import com.fitech.app.trainers.domain.entities.ServiceResource;
import com.fitech.app.trainers.domain.entities.TrainerService;
import com.fitech.app.trainers.domain.services.ServiceResourceService;
import com.fitech.app.trainers.infrastructure.repository.ServiceContractRepository;
import com.fitech.app.trainers.infrastructure.repository.TrainerServiceRepository;
import com.fitech.app.trainers.domain.entities.ServiceContract;
import com.fitech.app.users.domain.services.UserService;
import com.fitech.app.users.domain.entities.User;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/app/client-resources")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Client Resources", description = "Management of client resources like diets and routines")
@SecurityRequirement(name = "bearerAuth")
public class ClientResourceController {

    private final ServiceResourceService serviceResourceService;
    private final UserService userService;
    private final TrainerServiceRepository trainerServiceRepository;
    private final ServiceContractRepository serviceContractRepository;
    private final com.fitech.app.trainers.infrastructure.repositories.ServiceResourceRepository serviceResourceRepository;
    
    private Integer getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            User user = userService.getUserEntityByUsername(username);
            return user.getId();
        }
        return null;
    }
    
    @PostMapping
    public ResponseEntity<?> createResource(@RequestBody ServiceResourceDto resourceDto) {
        try {
            log.info("Creating resource: {} for service: {} and client: {}", 
                    resourceDto.getResourceName(), resourceDto.getServiceId(), resourceDto.getClientId());
            
            Integer currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }
            
            if (resourceDto.getServiceId() != null) {
                
                TrainerService service = trainerServiceRepository.findById(resourceDto.getServiceId())
                        .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + resourceDto.getServiceId()));
                
                
                if (!service.getTrainerId().equals(currentUserId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "No tienes permisos para crear recursos en este servicio"));
                }
                
                // If clientId was not provided, we can't proceed
                if (resourceDto.getClientId() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "Se require el ID del cliente para crear el recurso"));
                }
                
                log.info("Looking for active or completed contract between client {} and service {}", 
                        resourceDto.getClientId(), resourceDto.getServiceId());
                
                // Debug: Check all contracts for this service
                List<ServiceContract> allContractsForService = serviceContractRepository.findByServiceIdOrderByCreatedAtDesc(resourceDto.getServiceId());
                log.info("Found {} total contracts for service {}", allContractsForService.size(), resourceDto.getServiceId());
                for (ServiceContract c : allContractsForService) {
                    log.info("Contract ID: {}, Client: {}, Status: {}", c.getId(), c.getClientId(), c.getContractStatus());
                }
                
                // Find active or completed contract between this specific client and service
                ServiceContract contract = serviceContractRepository.findActiveContractByClientAndService(
                        resourceDto.getClientId(), resourceDto.getServiceId())
                        .orElseThrow(() -> new RuntimeException(
                            String.format("No se encontró un contrato activo o completado entre el cliente (ID: %d) y el servicio (ID: %d). " +
                                        "Verifica que exista un contrato en estado ACTIVE o COMPLETED para este cliente y servicio.", 
                                        resourceDto.getClientId(), resourceDto.getServiceId())));
                resourceDto.setTrainerId(service.getTrainerId());
                
                log.info("Auto-populated trainerId: {} and clientId: {} from serviceId: {} and contract", 
                        service.getTrainerId(), resourceDto.getClientId(), resourceDto.getServiceId());
            } else {
                
                if (resourceDto.getTrainerId() == null) {
                    resourceDto.setTrainerId(currentUserId);
                }
            }
            
            ServiceResourceDto createdResource = serviceResourceService.createResource(resourceDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdResource);
        } catch (Exception e) {
            log.error("Error creating resource: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al crear el recurso: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{resourceId}")
    public ResponseEntity<?> updateResource(
            @PathVariable Integer resourceId,
            @RequestBody ServiceResourceDto resourceDto) {
        try {
            log.info("Updating resource: {}", resourceId);
            ServiceResourceDto updatedResource = serviceResourceService.updateResource(resourceId, resourceDto);
            return ResponseEntity.ok(updatedResource);
        } catch (Exception e) {
            log.error("Error updating resource {}: {}", resourceId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al actualizar el recurso: " + e.getMessage()));
        }
    }

    /**
     * Obtener un recurso por ID
     */
    @GetMapping("/{resourceId}")
    public ResponseEntity<?> getResource(@PathVariable Integer resourceId) {
        try {
            return serviceResourceService.getResourceById(resourceId)
                    .map(resource -> ResponseEntity.ok().body(resource))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting resource {}: {}", resourceId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener el recurso: " + e.getMessage()));
        }
    }
    
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<?> getResourcesByService(@PathVariable Integer serviceId) {
        try {
            log.info("Getting resources for service: {}", serviceId);
            List<ServiceResourceDto> resources = serviceResourceService.getResourcesByServiceId(serviceId);
            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            log.error("Error getting resources for service {}: {}", serviceId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener los recursos: " + e.getMessage()));
        }
    }

   
    @GetMapping("/service/{serviceId}/type/{resourceType}")
    public ResponseEntity<?> getResourcesByServiceAndType(
            @PathVariable Integer serviceId,
            @PathVariable ServiceResource.ResourceType resourceType) {
        try {
            log.info("Getting resources of type {} for service: {}", resourceType, serviceId);
            List<ServiceResourceDto> resources = serviceResourceService.getResourcesByServiceIdAndType(serviceId, resourceType);
            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            log.error("Error getting resources of type {} for service {}: {}", resourceType, serviceId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener los recursos: " + e.getMessage()));
        }
    }

    /**
     * Obtener todos los recursos de un trainer
     */
    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<?> getResourcesByTrainer(@PathVariable Integer trainerId) {
        try {
            log.info("Getting all resources for trainer: {}", trainerId);
            List<ServiceResourceDto> resources = serviceResourceService.getResourcesByTrainerId(trainerId);
            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            log.error("Error getting resources for trainer {}: {}", trainerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener los recursos: " + e.getMessage()));
        }
    }

    /**
     * Desactivar un recurso
     */
    @PatchMapping("/{resourceId}/deactivate")
    public ResponseEntity<?> deactivateResource(@PathVariable Integer resourceId) {
        try {
            log.info("Deactivating resource: {}", resourceId);
            serviceResourceService.deactivateResource(resourceId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deactivating resource {}: {}", resourceId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al desactivar el recurso: " + e.getMessage()));
        }
    }

    /**
     * Reactivar un recurso
     */
    @PatchMapping("/{resourceId}/activate")
    public ResponseEntity<?> activateResource(@PathVariable Integer resourceId) {
        try {
            log.info("Activating resource: {}", resourceId);
            serviceResourceService.activateResource(resourceId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error activating resource {}: {}", resourceId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al activar el recurso: " + e.getMessage()));
        }
    }

    /**
     * Eliminar permanentemente un recurso
     */
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<?> deleteResource(@PathVariable Integer resourceId) {
        try {
            log.info("Deleting resource: {}", resourceId);
            serviceResourceService.deleteResource(resourceId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting resource {}: {}", resourceId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al eliminar el recurso: " + e.getMessage()));
        }
    }

    /**
     * Contar recursos activos de un servicio
     */
    @GetMapping("/service/{serviceId}/count")
    public ResponseEntity<?> countResourcesByService(@PathVariable Integer serviceId) {
        try {
            Long count = serviceResourceService.countActiveResourcesByServiceId(serviceId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error counting resources for service {}: {}", serviceId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al contar los recursos: " + e.getMessage()));
        }
    }

    // ========== DEBUG ENDPOINT ==========
    
    @GetMapping("/debug/contracts/{serviceId}/{clientId}")
    public ResponseEntity<?> debugContracts(@PathVariable Integer serviceId, @PathVariable Integer clientId) {
        try {
            log.info("DEBUG: Checking contracts for service {} and client {}", serviceId, clientId);
            
            // Check all contracts for this service
            List<ServiceContract> allContractsForService = serviceContractRepository.findByServiceIdOrderByCreatedAtDesc(serviceId);
            log.info("Found {} total contracts for service {}", allContractsForService.size(), serviceId);
            
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("serviceId", serviceId);
            debugInfo.put("clientId", clientId);
            
            List<Map<String, Object>> contractsInfo = new ArrayList<>();
            for (ServiceContract c : allContractsForService) {
                Map<String, Object> contractInfo = new HashMap<>();
                contractInfo.put("contractId", c.getId());
                contractInfo.put("clientId", c.getClientId());
                contractInfo.put("status", c.getContractStatus().toString());
                contractInfo.put("createdAt", c.getCreatedAt());
                contractInfo.put("isTargetClient", c.getClientId().equals(clientId));
                contractInfo.put("isActive", c.getContractStatus() == ServiceContract.ContractStatus.ACTIVE);
                contractInfo.put("isCompleted", c.getContractStatus() == ServiceContract.ContractStatus.COMPLETED);
                contractInfo.put("isValidForResources", 
                    c.getContractStatus() == ServiceContract.ContractStatus.ACTIVE || 
                    c.getContractStatus() == ServiceContract.ContractStatus.COMPLETED);
                contractsInfo.add(contractInfo);
                
                log.info("Contract ID: {}, Client: {}, Status: {}, Target Client: {}", 
                    c.getId(), c.getClientId(), c.getContractStatus(), c.getClientId().equals(clientId));
            }
            
            debugInfo.put("allContracts", contractsInfo);
            
            // Check specific active or completed contract
            Optional<ServiceContract> validContract = serviceContractRepository.findActiveContractByClientAndService(clientId, serviceId);
            debugInfo.put("hasValidContract", validContract.isPresent());
            if (validContract.isPresent()) {
                debugInfo.put("validContractId", validContract.get().getId());
                debugInfo.put("validContractStatus", validContract.get().getContractStatus().toString());
            }
            
            return ResponseEntity.ok(debugInfo);
        } catch (Exception e) {
            log.error("Error in debug endpoint: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error en debug: " + e.getMessage()));
        }
    }

    // ========== ENDPOINTS PARA CLIENTES ==========

    /**
     * Obtener todos los recursos del cliente autenticado
     */
    @GetMapping("/client")
    public ResponseEntity<?> getClientResources(
            @RequestParam(required = false) ServiceResource.ResourceType resourceType) {
        try {
            Integer clientId = getCurrentUserId();
            if (clientId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            List<ClientResourceResponseDto> resources;
            if (resourceType != null) {
                log.info("Getting resources of type {} for client: {}", resourceType, clientId);
                resources = serviceResourceService.getResourcesForClient(clientId, resourceType);
            } else {
                log.info("Getting all resources for client: {}", clientId);
                resources = serviceResourceService.getAllResourcesForClient(clientId);
            }

            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            log.error("Error getting client resources: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener los recursos: " + e.getMessage()));
        }
    }

    /**
     * Obtener dietas del cliente autenticado
     */
    @GetMapping("/client/diets")
    public ResponseEntity<?> getClientDiets() {
        try {
            Integer clientId = getCurrentUserId();
            if (clientId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            log.info("Getting diets for client: {}", clientId);
            List<ClientResourceResponseDto> diets = serviceResourceService.getResourcesForClient(
                    clientId, ServiceResource.ResourceType.DIETA);
            
            log.info("Returning {} diets for client {}", diets.size(), clientId);
            for (ClientResourceResponseDto diet : diets) {
                log.info("Diet: ID={}, Name={}, ClientId={}, TrainerName={}", 
                    diet.getId(), diet.getResourceName(), diet.getClientId(), diet.getTrainerName());
            }
            
            return ResponseEntity.ok(diets);
        } catch (Exception e) {
            log.error("Error getting client diets: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener las dietas: " + e.getMessage()));
        }
    }

    /**
     * Obtener rutinas del cliente autenticado
     */
    @GetMapping("/client/routines")
    public ResponseEntity<?> getClientRoutines() {
        try {
            Integer clientId = getCurrentUserId();
            if (clientId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            log.info("Getting routines for client: {}", clientId);
            List<ClientResourceResponseDto> routines = serviceResourceService.getResourcesForClient(
                    clientId, ServiceResource.ResourceType.RUTINA);
            return ResponseEntity.ok(routines);
        } catch (Exception e) {
            log.error("Error getting client routines: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener las rutinas: " + e.getMessage()));
        }
    }

    /**
     * Obtener los recursos asignados a los clientes del trainer autenticado
     */
    @GetMapping("/trainer/clients/resources")
    public ResponseEntity<?> getTrainerClientsResources(
            @RequestParam(required = false) ServiceResource.ResourceType resourceType) {
        try {
            Integer trainerId = getCurrentUserId();
            if (trainerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            log.info("Getting resources for trainer's clients: {}", trainerId);
            List<ClientResourceResponseDto> resources;
            if (resourceType != null) {
                resources = serviceResourceService.getResourcesForTrainerClients(trainerId, resourceType);
            } else {
                resources = serviceResourceService.getAllResourcesForTrainerClients(trainerId);
            }
            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            log.error("Error getting resources for trainer's clients: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener los recursos de los clientes: " + e.getMessage()));
        }
    }

    /**
     * Obtener los recursos asignados a los clientes de un trainer específico
     */
    @GetMapping("/trainer/{trainerId}/client-resources")
    public ResponseEntity<?> getTrainerClientResources(@PathVariable Integer trainerId) {
        try {
            // Verificar que el usuario autenticado sea el mismo trainer o tenga permisos
            Integer currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            // Por seguridad, solo permitir que el trainer vea sus propios recursos
            if (!currentUserId.equals(trainerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tienes permisos para ver estos recursos"));
            }

            log.info("Getting resources for trainer's clients: {}", trainerId);
            List<ClientResourceResponseDto> resources = serviceResourceService.getAllResourcesForTrainerClients(trainerId);
            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            log.error("Error getting resources for trainer's clients: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener los recursos de los clientes: " + e.getMessage()));
        }
    }
    
    @GetMapping("/grouped-by-client")
    public ResponseEntity<Page<ClientResourceGroupDto>> getResourcesGroupedByClient(
            @RequestParam(required = false, defaultValue = "DIETA") ServiceResource.ResourceType resourceType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "clientName") String sort) {
        try {
            Integer trainerId = getCurrentUserId();
            if (trainerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
            Page<ClientResourceGroupDto> resources = serviceResourceService.getResourcesGroupedByClient(
                trainerId, resourceType, pageable);

            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            log.error("Error getting grouped resources: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/grouped-by-client/all")
    public ResponseEntity<Page<ClientResourceGroupDto>> getResourcesGroupedByClientIncludingInactive(
            @RequestParam(required = false, defaultValue = "DIETA") ServiceResource.ResourceType resourceType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "clientName") String sort) {
        try {
            Integer trainerId = getCurrentUserId();
            if (trainerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            log.info("Getting ALL resources (including inactive) grouped by client for trainer: {} and type: {}", trainerId, resourceType);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
            Page<ClientResourceGroupDto> resources = serviceResourceService.getResourcesGroupedByClientIncludingInactive(
                trainerId, resourceType, pageable);

            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            log.error("Error getting grouped resources including inactive: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    @PatchMapping("/{resourceId}/status")
    public ResponseEntity<?> toggleResourceStatus(
            @PathVariable Integer resourceId,
            @RequestBody Map<String, Boolean> status) {
        try {
            log.info("Toggling resource status: {} to {}", resourceId, status.get("isActive"));
            if (status.get("isActive")) {
                serviceResourceService.activateResource(resourceId);
            } else {
                serviceResourceService.deactivateResource(resourceId);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error toggling resource status {}: {}", resourceId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al cambiar el estado del recurso: " + e.getMessage()));
        }
    }


    @GetMapping("/client/{clientId}")
    public ResponseEntity<?> getDirectResourcesByClient(@PathVariable Integer clientId) {
        try {
            Integer currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            log.info("Getting direct resources for client: {}", clientId);
            List<ServiceResourceDto> resources = serviceResourceService.getResourcesByClientId(clientId);
            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            log.error("Error getting direct resources for client {}: {}", clientId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener los recursos directos: " + e.getMessage()));
        }
    }
    
    @GetMapping("/client/{clientId}/type/{resourceType}")
    public ResponseEntity<?> getDirectResourcesByClientAndType(
            @PathVariable Integer clientId,
            @PathVariable ServiceResource.ResourceType resourceType) {
        try {
            Integer currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            log.info("Getting direct resources of type {} for client: {}", resourceType, clientId);
            List<ServiceResourceDto> resources = serviceResourceService.getResourcesByClientIdAndType(clientId, resourceType);
            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            log.error("Error getting direct resources of type {} for client {}: {}", resourceType, clientId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener los recursos directos: " + e.getMessage()));
        }
    }


    @GetMapping("/client/{clientId}/count")
    public ResponseEntity<?> countDirectResourcesByClient(@PathVariable Integer clientId) {
        try {
            Integer currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            log.info("Counting direct resources for client: {}", clientId);
            Long count = serviceResourceService.countActiveResourcesByClientId(clientId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error counting direct resources for client {}: {}", clientId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al contar los recursos directos: " + e.getMessage()));
        }
    }

    // DEBUG: Endpoint temporal para debuggear el problema
    @GetMapping("/debug/client-resources/{clientId}")
    public ResponseEntity<?> debugClientResources(@PathVariable Integer clientId) {
        try {
            log.info("DEBUG: Checking resources for client: {}", clientId);
            
            // 1. Verificar recursos directos
            List<ServiceResource> directResources = serviceResourceRepository.findByClientIdAndResourceTypeAndServiceIdIsNullAndIsActiveTrue(clientId, ServiceResource.ResourceType.DIETA);
            log.info("DEBUG: Direct resources count: {}", directResources.size());
            for (ServiceResource resource : directResources) {
                log.info("DEBUG: Direct resource - ID: {}, Name: {}, ClientId: {}, ServiceId: {}", 
                    resource.getId(), resource.getResourceName(), resource.getClientId(), resource.getServiceId());
            }
            
            // 2. Verificar recursos de servicios
            List<Object[]> serviceResources = serviceResourceRepository.findActiveResourcesByClientAndTypeWithClientInfo(clientId, ServiceResource.ResourceType.DIETA);
            log.info("DEBUG: Service resources count: {}", serviceResources.size());
            for (Object[] result : serviceResources) {
                ServiceResource resource = (ServiceResource) result[0];
                Integer contractClientId = (Integer) result[1];
                String firstName = (String) result[2];
                String lastName = (String) result[3];
                log.info("DEBUG: Service resource - ResourceID: {}, ResourceName: {}, ResourceClientId: {}, ContractClientId: {}, ServiceId: {}, ClientName: {} {}", 
                    resource.getId(), resource.getResourceName(), resource.getClientId(), contractClientId, resource.getServiceId(), firstName, lastName);
            }
            
            Map<String, Object> debugInfo = Map.of(
                "requestedClientId", clientId,
                "directResourcesCount", directResources.size(),
                "serviceResourcesCount", serviceResources.size(),
                "message", "Check logs for detailed information"
            );
            
            return ResponseEntity.ok(debugInfo);
        } catch (Exception e) {
            log.error("DEBUG: Error checking resources for client {}: {}", clientId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Debug error: " + e.getMessage()));
        }
    }

    // ADMIN: Endpoint para corregir recursos de servicios con clientId incorrecto
    @PostMapping("/admin/fix-service-resources")
    public ResponseEntity<?> fixServiceResourcesClientId() {
        try {
            log.info("ADMIN: Starting fix for service resources with incorrect clientId");
            
            // Obtener todos los recursos que tienen serviceId (recursos de servicios)
            List<ServiceResource> serviceResources = serviceResourceRepository.findAll()
                    .stream()
                    .filter(r -> r.getServiceId() != null)
                    .collect(Collectors.toList());
            
            int fixedCount = 0;
            for (ServiceResource resource : serviceResources) {
                if (resource.getClientId() != null) {
                    log.info("ADMIN: Fixing resource ID {} - removing clientId {} (should be determined by contract)", 
                            resource.getId(), resource.getClientId());
                    resource.setClientId(null);
                    serviceResourceRepository.save(resource);
                    fixedCount++;
                }
            }
            
            log.info("ADMIN: Fixed {} service resources", fixedCount);
            
            return ResponseEntity.ok(Map.of(
                "message", "Service resources fixed successfully",
                "fixedCount", fixedCount,
                "totalServiceResources", serviceResources.size()
            ));
        } catch (Exception e) {
            log.error("ADMIN: Error fixing service resources: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fixing service resources: " + e.getMessage()));
        }
    }
} 