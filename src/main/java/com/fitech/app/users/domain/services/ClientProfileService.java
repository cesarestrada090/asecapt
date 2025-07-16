package com.fitech.app.users.domain.services;

import com.fitech.app.users.application.dto.ClientProfileDTO;
import com.fitech.app.users.domain.entities.User;
import com.fitech.app.users.domain.entities.Person;
import com.fitech.app.users.domain.entities.FitnessGoalType;
import com.fitech.app.users.infrastructure.repository.UserRepository;
import com.fitech.app.trainers.infrastructure.repository.ServiceContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class ClientProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceContractRepository serviceContractRepository;

    public ClientProfileDTO getClientProfile(Long clientId) {
        User user = userRepository.findByIdWithPersonAndGoals(clientId.intValue())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Person person = user.getPerson();
        if (person == null) {
            throw new RuntimeException("Información del cliente no encontrada");
        }

        // Formatear fecha de nacimiento
        String birthDateStr = null;
        if (person.getBirthDate() != null) {
            birthDateStr = person.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        // Obtener nombres de los objetivos de fitness
        List<String> fitnessGoals = person.getFitnessGoalTypes().stream()
                .map(FitnessGoalType::getName)
                .collect(Collectors.toList());

        // Obtener servicios contratados por el cliente
        List<Object[]> serviceContracts = serviceContractRepository.findClientServicesWithTrainerInfo(clientId.intValue());
        
        List<ClientProfileDTO.ClientServiceInfo> services = serviceContracts.stream()
                .map(row -> new ClientProfileDTO.ClientServiceInfo(
                        (String) row[0], // serviceName
                        (String) row[1], // trainerName
                        (String) row[2], // contractStatus
                        (String) row[3], // modality
                        row[4] != null ? row[4].toString() : null, // startDate
                        row[5] != null ? ((BigDecimal) row[5]).doubleValue() : null  // totalAmount
                ))
                .collect(Collectors.toList());

        return new ClientProfileDTO(
                clientId,
                person.getFirstName(),
                person.getLastName(),
                person.getDocumentNumber(),
                person.getDocumentType(),
                person.getBio(),
                person.getProfilePhotoId(),
                person.getGender(),
                birthDateStr,
                fitnessGoals,
                services
        );
    }
} 