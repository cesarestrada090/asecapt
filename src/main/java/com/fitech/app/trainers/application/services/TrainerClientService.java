package com.fitech.app.trainers.application.services;

import com.fitech.app.trainers.application.dto.ClientServiceDTO;
import com.fitech.app.trainers.application.dto.TrainerClientDTO;
import com.fitech.app.trainers.domain.entities.ServiceContract;
import com.fitech.app.trainers.domain.entities.TrainerService;
import com.fitech.app.trainers.infrastructure.repository.ServiceContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrainerClientService {

    @Autowired
    private ServiceContractRepository serviceContractRepository;

    public Page<TrainerClientDTO> getTrainerClientsGrouped(Long trainerId, Pageable pageable, 
                                                          String search, String status, String modality) {
        
        // Obtener todos los contratos del trainer con información del servicio y cliente
        List<Object[]> contractsWithClientInfo = serviceContractRepository.findByTrainerIdWithServiceAndClientInfo(trainerId.intValue());
        
        // Obtener IDs únicos de personas para consultar fitness goals
        Set<Integer> personIds = new HashSet<>();
        Map<Long, TrainerClientDTO> clientsMap = new LinkedHashMap<>();
        Map<Integer, List<String>> fitnessGoalsMap = new HashMap<>();
        Map<Long, Integer> clientToPersonMap = new HashMap<>(); // Mapeo clientId -> personId
        
        // Primera pasada: recopilar IDs de personas y crear estructura básica de clientes
        for (Object[] result : contractsWithClientInfo) {
            ServiceContract contract = (ServiceContract) result[0];
            String firstName = (String) result[1];
            String lastName = (String) result[2];
            String email = (String) result[3];
            String phone = (String) result[4];
            Integer profilePhotoId = (Integer) result[5];
            Integer personId = (Integer) result[6];
            
            personIds.add(personId);
            
            Long clientId = contract.getClientId().longValue();
            clientToPersonMap.put(clientId, personId); // Guardar el mapeo
            
            // Crear el servicio para este contrato
            ClientServiceDTO service = createClientServiceDTO(contract);
            
            // Si el cliente ya existe, agregar el servicio
            if (clientsMap.containsKey(clientId)) {
                TrainerClientDTO existingClient = clientsMap.get(clientId);
                existingClient.getServices().add(service);
                
                // Actualizar totales
                existingClient.setTotalServicesCount(existingClient.getTotalServicesCount() + 1);
                if ("ACTIVE".equals(contract.getContractStatus().toString())) {
                    existingClient.setActiveServicesCount(existingClient.getActiveServicesCount() + 1);
                }
                existingClient.setTotalAmountPaid(
                    existingClient.getTotalAmountPaid().add(contract.getTotalAmount())
                );
            } else {
                // Crear nuevo cliente con información básica (sin fitness goals aún)
                TrainerClientDTO newClient = createTrainerClientDTOWithClientInfo(
                    contract, service, firstName, lastName, email, phone, 
                    profilePhotoId != null ? profilePhotoId.longValue() : null);
                clientsMap.put(clientId, newClient);
            }
        }
        
        // Obtener fitness goals para todas las personas
        if (!personIds.isEmpty()) {
            List<Object[]> fitnessGoalsResults = serviceContractRepository.findFitnessGoalsByPersonIds(new ArrayList<>(personIds));
            
            // Agrupar fitness goals por persona ID
            for (Object[] fgResult : fitnessGoalsResults) {
                Integer personId = (Integer) fgResult[0];
                String goalName = (String) fgResult[1];
                
                fitnessGoalsMap.computeIfAbsent(personId, k -> new ArrayList<>()).add(goalName);
            }
        }
        
        // Segunda pasada: asignar fitness goals a los clientes
        for (TrainerClientDTO client : clientsMap.values()) {
            Integer personId = clientToPersonMap.get(client.getClientId());
            List<String> fitnessGoals = fitnessGoalsMap.getOrDefault(personId, new ArrayList<>());
            client.setFitnessGoals(fitnessGoals);
        }
        
        // Convertir a lista
        List<TrainerClientDTO> allClients = new ArrayList<>(clientsMap.values());
        
        // Aplicar filtros
        List<TrainerClientDTO> filteredClients = applyFilters(allClients, search, status, modality);
        
        // Aplicar paginación manual
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredClients.size());
        
        List<TrainerClientDTO> pageContent = filteredClients.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, filteredClients.size());
    }
    
    private ClientServiceDTO createClientServiceDTO(ServiceContract contract) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        
        // Obtener información del servicio si está disponible
        TrainerService service = contract.getService();
        String serviceName = service != null ? service.getName() : "Servicio";
        String serviceDescription = service != null ? service.getDescription() : "";
        BigDecimal pricePerSession = service != null ? service.getPricePerSession() : BigDecimal.ZERO;
        Boolean isInPerson = service != null ? service.getIsInPerson() : true;
        Boolean transportIncluded = service != null ? service.getTransportIncluded() : false;
        BigDecimal transportCost = service != null ? service.getTransportCostPerSession() : BigDecimal.ZERO;
        
        return new ClientServiceDTO(
            contract.getServiceId().longValue(),
            serviceName,
            serviceDescription,
            pricePerSession,
            "package", // Tipo por defecto
            isInPerson ? "presencial" : "remoto",
            transportIncluded,
            transportCost,
            mapContractStatus(contract.getContractStatus().toString()),
            contract.getStartDate() != null ? contract.getStartDate().atStartOfDay().format(formatter) : null,
            contract.getEndDate() != null ? contract.getEndDate().atStartOfDay().format(formatter) : null,
            contract.getTotalAmount(),
            contract.getId().longValue()
        );
    }
    
    private TrainerClientDTO createTrainerClientDTO(ServiceContract contract, ClientServiceDTO service) {
        String clientName = "Cliente ID: " + contract.getClientId(); // Nombre por defecto usando el ID
        List<String> fitnessGoals = new ArrayList<>(); // Lista vacía por ahora
        
        return new TrainerClientDTO(
            contract.getClientId().longValue(),
            clientName,
            "", // Email - no disponible en ServiceContract
            "", // Phone - no disponible en ServiceContract  
            null, // profilePhotoId - no disponible
            fitnessGoals,
            new ArrayList<>(Arrays.asList(service)),
            null, // lastContactDate
            null, // nextSessionDate
            "", // lastTrainerNote
            null, // lastNoteDate
            1, // totalServicesCount
            "ACTIVE".equals(contract.getContractStatus().toString()) ? 1 : 0, // activeServicesCount
            contract.getTotalAmount() // totalAmountPaid
        );
    }
    
    private TrainerClientDTO createTrainerClientDTOWithClientInfo(ServiceContract contract, ClientServiceDTO service,
                                                                 String firstName, String lastName, String email, String phone, Long profilePhotoId) {
        String clientName = firstName + " " + lastName;
        
        return new TrainerClientDTO(
            contract.getClientId().longValue(),
            clientName,
            email,
            phone,
            profilePhotoId,
            new ArrayList<>(),
            new ArrayList<>(Arrays.asList(service)),
            null, // lastContactDate
            null, // nextSessionDate
            "", // lastTrainerNote
            null, // lastNoteDate
            1, // totalServicesCount
            "ACTIVE".equals(contract.getContractStatus().toString()) ? 1 : 0, // activeServicesCount
            contract.getTotalAmount() // totalAmountPaid
        );
    }
    
    private String mapContractStatus(String contractStatus) {
        if (contractStatus == null) return "paused";
        
        switch (contractStatus.toUpperCase()) {
            case "ACTIVE": return "active";
            case "COMPLETED": return "completed";
            case "CANCELLED": return "cancelled";
            default: return "paused";
        }
    }
    
    private List<TrainerClientDTO> applyFilters(List<TrainerClientDTO> clients, String search, 
                                               String status, String modality) {
        return clients.stream()
                .filter(client -> {
                    // Filtro de búsqueda
                    if (search != null && !search.trim().isEmpty()) {
                        String searchLower = search.toLowerCase();
                        if (!client.getClientName().toLowerCase().contains(searchLower)) {
                            return false;
                        }
                    }
                    
                    // Filtro de estado
                    if (status != null && !status.equals("all")) {
                        boolean hasMatchingStatus = client.getServices().stream()
                                .anyMatch(service -> service.getStatus().equals(status));
                        if (!hasMatchingStatus) {
                            return false;
                        }
                    }
                    
                    // Filtro de modalidad
                    if (modality != null && !modality.equals("all")) {
                        boolean hasMatchingModality = client.getServices().stream()
                                .anyMatch(service -> service.getModality().equals(modality));
                        if (!hasMatchingModality) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
    }
} 