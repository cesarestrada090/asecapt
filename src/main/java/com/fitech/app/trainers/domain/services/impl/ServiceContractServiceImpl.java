package com.fitech.app.trainers.domain.services.impl;

import com.fitech.app.trainers.application.dto.CreateContractDto;
import com.fitech.app.trainers.application.dto.ServiceContractDto;
import com.fitech.app.trainers.domain.entities.ServiceContract;
import com.fitech.app.trainers.domain.entities.TrainerService;
import com.fitech.app.trainers.domain.services.ServiceContractService;
import com.fitech.app.trainers.infrastructure.repository.ServiceContractRepository;
import com.fitech.app.trainers.infrastructure.repository.TrainerServiceRepository;
import com.fitech.app.users.infrastructure.repository.UserRepository;
import com.fitech.app.users.domain.entities.User;
import com.fitech.app.users.application.dto.PremiumBy;
import com.fitech.app.memberships.application.services.MembershipService;
import com.fitech.app.memberships.infrastructure.repositories.MembershipPaymentRepository;
import com.fitech.app.memberships.domain.entities.MembershipPayment;
import com.fitech.app.memberships.domain.entities.UserMembership;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServiceContractServiceImpl implements ServiceContractService {

    private final ServiceContractRepository contractRepository;
    private final TrainerServiceRepository trainerServiceRepository;
    private final UserRepository userRepository;
    private final MembershipService membershipService;
    private final MembershipPaymentRepository membershipPaymentRepository;

    @Override
    public ServiceContractDto createContract(CreateContractDto createDto) {
        log.info("Creating contract for service {} and client {}", createDto.getServiceId(), createDto.getClientId());
        
        // Verificar que los términos fueron aceptados
        if (!createDto.getTermsAccepted()) {
            throw new RuntimeException("Debe aceptar los términos y condiciones para continuar");
        }
        
        // Verificar que el servicio existe y está activo
        TrainerService service = trainerServiceRepository.findById(createDto.getServiceId())
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        
        if (!service.getIsActive()) {
            throw new RuntimeException("El servicio no está disponible");
        }
        
        // Verificar que el cliente puede contratar este servicio
        if (!canClientContractService(createDto.getClientId(), createDto.getServiceId())) {
            throw new RuntimeException("Ya tienes un contrato activo para este servicio");
        }
        
        // Crear contrato
        ServiceContract contract = new ServiceContract();
        contract.setClientId(createDto.getClientId());
        contract.setTrainerId(service.getTrainerId());
        contract.setServiceId(createDto.getServiceId());
        contract.setStartDate(createDto.getStartDate() != null ? createDto.getStartDate() : LocalDate.now());
        contract.setTotalAmount(service.getTotalPrice());
        contract.setNotes(createDto.getNotes());
        contract.setTermsAcceptedAt(LocalDateTime.now());
        
        // Flujo simplificado: crear contrato directamente como ACTIVE con pago COMPLETED
        contract.setContractStatus(ServiceContract.ContractStatus.ACTIVE);
        contract.setPaymentStatus(ServiceContract.PaymentStatus.COMPLETED);
        
        ServiceContract savedContract = contractRepository.save(contract);
        
        // Actualizar contador en el servicio
        updateServiceContractCount(createDto.getServiceId());
        
        try {
            // Crear membresía por contrato y actualizar estado premium del usuario
            LocalDate endDate = calculateContractEndDate(savedContract.getStartDate(), service);
            String contractDetails = buildContractDetails(savedContract, service);
            
            UserMembership membership = membershipService.createContractMembership(
                createDto.getClientId(),
                service.getTrainerId(),
                savedContract.getStartDate(),
                endDate,
                contractDetails,
                service.getTotalPrice(),
                "CONTRACT_PAYMENT", // Método de pago específico para contratos
                savedContract.getId() // Service contract ID for payment linking
            );
            
            // Actualizar estado premium del usuario
            User client = userRepository.findById(createDto.getClientId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            
            client.setPremium(true);
            client.setPremiumBy(PremiumBy.CONTRACT);
            client.setUpdatedAt(LocalDateTime.now());
            userRepository.save(client);
            
            log.info("Contract created with ID: {} and membership ID: {}", savedContract.getId(), membership.getId());
            
        } catch (Exception e) {
            log.error("Error creating membership for contract {}: {}", savedContract.getId(), e.getMessage());
            // No fallar la creación del contrato, pero registrar el error
            log.warn("Contract {} created but membership creation failed", savedContract.getId());
        }
        
        return ServiceContractDto.fromEntity(savedContract);
    }

    @Override
    public Optional<ServiceContractDto> getContractById(Integer id) {
        return contractRepository.findById(id)
            .map(ServiceContractDto::fromEntity);
    }

    @Override
    public List<ServiceContractDto> getContractsByClient(Integer clientId) {
        return contractRepository.findByClientIdWithService(clientId)
            .stream()
            .map(ServiceContractDto::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<ServiceContractDto> getContractsByTrainer(Integer trainerId) {
        List<ServiceContract> contracts = contractRepository.findByTrainerIdWithService(trainerId);
        return contracts.stream()
            .map(this::mapToContractDtoWithClientInfo)
            .collect(Collectors.toList());
    }

    @Override
    public List<ServiceContractDto> getContractsByService(Integer serviceId) {
        return contractRepository.findByServiceIdOrderByCreatedAtDesc(serviceId)
            .stream()
            .map(ServiceContractDto::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<ServiceContractDto> getActiveContractsByClient(Integer clientId) {
        return contractRepository.findActiveContractsByClient(clientId)
            .stream()
            .map(ServiceContractDto::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<ServiceContractDto> getInactiveContractsByClient(Integer clientId) {
        return contractRepository.findInactiveContractsByClient(clientId)
            .stream()
            .map(ServiceContractDto::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<ServiceContractDto> getContractsByClientAndStatus(Integer clientId, String status) {
        try {
            ServiceContract.ContractStatus contractStatus = ServiceContract.ContractStatus.valueOf(status.toUpperCase());
            return contractRepository.findContractsByClientAndStatus(clientId, contractStatus)
                .stream()
                .map(ServiceContractDto::fromEntity)
                .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado de contrato inválido: " + status);
        }
    }

    @Override
    public List<ServiceContractDto> getActiveContractsByTrainer(Integer trainerId) {
        return contractRepository.findActiveContractsByTrainer(trainerId)
            .stream()
            .map(ServiceContractDto::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public boolean canClientContractService(Integer clientId, Integer serviceId) {
        // Un cliente puede contratar un servicio si no tiene un contrato activo o pendiente para ese servicio
        Optional<ServiceContract> existingContract = contractRepository
            .findActiveContractByClientAndService(clientId, serviceId);
        
        return existingContract.isEmpty();
    }

    @Override
    public ServiceContractDto activateContract(Integer contractId) {
        ServiceContract contract = contractRepository.findById(contractId)
            .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));
        
        if (!contract.canBeModified()) {
            throw new RuntimeException("Este contrato no puede ser modificado");
        }
        
        contract.setContractStatus(ServiceContract.ContractStatus.ACTIVE);
        contract.setPaymentStatus(ServiceContract.PaymentStatus.COMPLETED);
        
        ServiceContract updated = contractRepository.save(contract);
        log.info("Contract {} activated", contractId);
        
        return ServiceContractDto.fromEntity(updated);
    }

    @Override
    public ServiceContractDto completeContract(Integer contractId) {
        ServiceContract contract = contractRepository.findById(contractId)
            .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));
        
        contract.setContractStatus(ServiceContract.ContractStatus.COMPLETED);
        contract.setCompletionDate(LocalDateTime.now()); // Record actual completion date
        ServiceContract updated = contractRepository.save(contract);
        
        // Update associated payments to AVAILABLE_FOR_COLLECTION
        updatePaymentsToAvailableForCollection(contractId);
        
        log.info("Contract {} completed on {} and payments updated to AVAILABLE_FOR_COLLECTION", 
                contractId, contract.getCompletionDate());
        return ServiceContractDto.fromEntity(updated);
    }

    @Override
    public ServiceContractDto cancelContract(Integer contractId) {
        ServiceContract contract = contractRepository.findById(contractId)
            .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));
        
        contract.setContractStatus(ServiceContract.ContractStatus.CANCELLED);
        ServiceContract updated = contractRepository.save(contract);
        
        // Actualizar contador en el servicio
        updateServiceContractCount(contract.getServiceId());
        
        log.info("Contract {} cancelled", contractId);
        return ServiceContractDto.fromEntity(updated);
    }

    @Override
    public ContractStats getStatsForTrainer(Integer trainerId) {
        List<ServiceContract> allContracts = contractRepository.findByTrainerIdOrderByCreatedAtDesc(trainerId);
        
        long total = allContracts.size();
        long active = allContracts.stream()
            .mapToLong(c -> c.getContractStatus() == ServiceContract.ContractStatus.ACTIVE ? 1 : 0)
            .sum();
        long completed = allContracts.stream()
            .mapToLong(c -> c.getContractStatus() == ServiceContract.ContractStatus.COMPLETED ? 1 : 0)
            .sum();
        long pending = allContracts.stream()
            .mapToLong(c -> c.getContractStatus() == ServiceContract.ContractStatus.PENDING ? 1 : 0)
            .sum();
        
        return new ContractStats(total, active, completed, pending);
    }

    private void updateServiceContractCount(Integer serviceId) {
        // Actualizar el contador de contratos del servicio
        Long contractCount = contractRepository.countContractsByService(serviceId);
        
        TrainerService service = trainerServiceRepository.findById(serviceId).orElse(null);
        if (service != null) {
            service.setEnrolledUsersCount(contractCount.intValue());
            trainerServiceRepository.save(service);
        }
    }

    private ServiceContractDto mapToContractDtoWithClientInfo(ServiceContract contract) {
        ServiceContractDto dto = ServiceContractDto.fromEntity(contract);
        
        // Obtener información del cliente
        try {
            Optional<User> clientUser = userRepository.findById(contract.getClientId());
            if (clientUser.isPresent() && clientUser.get().getPerson() != null) {
                var person = clientUser.get().getPerson();
                dto.setClientFirstName(person.getFirstName());
                dto.setClientLastName(person.getLastName());
                dto.setClientName(person.getFirstName() + " " + person.getLastName());
                dto.setClientProfilePhotoId(person.getProfilePhotoId());
                dto.setClientEmail(person.getEmail());
                
                // Agregar objetivos de fitness del cliente
                if (person.getFitnessGoalTypes() != null && !person.getFitnessGoalTypes().isEmpty()) {
                    List<String> fitnessGoals = person.getFitnessGoalTypes().stream()
                        .map(goal -> goal.getName())
                        .collect(Collectors.toList());
                    dto.setClientFitnessGoals(fitnessGoals);
                }
            }
        } catch (Exception e) {
            log.warn("Error loading client info for contract {}: {}", contract.getId(), e.getMessage());
        }
        
        return dto;
    }
    
    private LocalDate calculateContractEndDate(LocalDate startDate, TrainerService service) {
        
        int durationDays = 90; // 3 meses por defecto
        
        return startDate.plusDays(durationDays);
    }
    
    private String buildContractDetails(ServiceContract contract, TrainerService service) {
        StringBuilder details = new StringBuilder();
        details.append("Contrato de Servicio - ").append(service.getName()).append("\n");
        details.append("Fecha de inicio: ").append(contract.getStartDate()).append("\n");
        details.append("Importe total: €").append(contract.getTotalAmount()).append("\n");
        details.append("Descripción: ").append(service.getDescription()).append("\n");
        
        if (contract.getNotes() != null && !contract.getNotes().trim().isEmpty()) {
            details.append("Notas adicionales: ").append(contract.getNotes()).append("\n");
        }
        
        details.append("Estado del contrato: ").append(contract.getContractStatus()).append("\n");
        details.append("Estado del pago: ").append(contract.getPaymentStatus());
        
        return details.toString();
    }
    
    /**
     * Update payments associated with a contract to AVAILABLE_FOR_COLLECTION status
     * when the client marks the contract as completed
     */
    private void updatePaymentsToAvailableForCollection(Integer contractId) {
        try {
            // Find all payments associated with this contract
            List<MembershipPayment> contractPayments = membershipPaymentRepository
                .findByServiceContractId(contractId);
            
            if (contractPayments.isEmpty()) {
                log.warn("No payments found for contract {}", contractId);
                return;
            }
            
            int updatedPayments = 0;
            for (MembershipPayment payment : contractPayments) {
                // Only update payments that are in PENDING_CLIENT_APPROVAL status
                if (MembershipPayment.COLLECTION_PENDING_CLIENT_APPROVAL.equals(payment.getCollectionStatus())) {
                    payment.setCollectionStatus(MembershipPayment.COLLECTION_AVAILABLE);
                    membershipPaymentRepository.save(payment);
                    updatedPayments++;
                    
                    log.info("Payment {} for contract {} updated to AVAILABLE_FOR_COLLECTION", 
                            payment.getId(), contractId);
                }
            }
            
            log.info("Updated {} payments to AVAILABLE_FOR_COLLECTION for contract {}", 
                    updatedPayments, contractId);
            
        } catch (Exception e) {
            log.error("Error updating payments for contract {}: {}", contractId, e.getMessage(), e);
            // Don't throw exception to avoid failing the contract completion
            // Log the error and continue
        }
    }
} 