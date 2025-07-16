package com.fitech.app.trainers.domain.services;

import com.fitech.app.trainers.application.dto.CreateContractDto;
import com.fitech.app.trainers.application.dto.ServiceContractDto;
import com.fitech.app.trainers.domain.entities.ServiceContract;

import java.util.List;
import java.util.Optional;

public interface ServiceContractService {
    
    // Crear nuevo contrato
    ServiceContractDto createContract(CreateContractDto createDto);
    
    // Obtener contrato por ID
    Optional<ServiceContractDto> getContractById(Integer id);
    
    // Obtener contratos por cliente
    List<ServiceContractDto> getContractsByClient(Integer clientId);
    
    // Obtener contratos por trainer
    List<ServiceContractDto> getContractsByTrainer(Integer trainerId);
    
    // Obtener contratos por servicio
    List<ServiceContractDto> getContractsByService(Integer serviceId);
    
    // Obtener contratos activos por cliente
    List<ServiceContractDto> getActiveContractsByClient(Integer clientId);
    
    // Obtener contratos inactivos por cliente (COMPLETED y CANCELLED)
    List<ServiceContractDto> getInactiveContractsByClient(Integer clientId);
    
    // Obtener contratos por cliente filtrados por estado específico
    List<ServiceContractDto> getContractsByClientAndStatus(Integer clientId, String status);
    
    // Obtener contratos activos por trainer
    List<ServiceContractDto> getActiveContractsByTrainer(Integer trainerId);
    
    // Verificar si un cliente puede contratar un servicio
    boolean canClientContractService(Integer clientId, Integer serviceId);
    
    // Activar contrato (cambiar estado a ACTIVE)
    ServiceContractDto activateContract(Integer contractId);
    
    // Completar contrato (cambiar estado a COMPLETED)
    ServiceContractDto completeContract(Integer contractId);
    
    // Cancelar contrato
    ServiceContractDto cancelContract(Integer contractId);
    
    // Obtener estadísticas de contratos
    ContractStats getStatsForTrainer(Integer trainerId);
    
    // Clase para estadísticas
    class ContractStats {
        private Long totalContracts;
        private Long activeContracts;
        private Long completedContracts;
        private Long pendingContracts;
        
        public ContractStats(Long totalContracts, Long activeContracts, Long completedContracts, Long pendingContracts) {
            this.totalContracts = totalContracts;
            this.activeContracts = activeContracts;
            this.completedContracts = completedContracts;
            this.pendingContracts = pendingContracts;
        }
        
        // Getters
        public Long getTotalContracts() { return totalContracts; }
        public Long getActiveContracts() { return activeContracts; }
        public Long getCompletedContracts() { return completedContracts; }
        public Long getPendingContracts() { return pendingContracts; }
    }
} 