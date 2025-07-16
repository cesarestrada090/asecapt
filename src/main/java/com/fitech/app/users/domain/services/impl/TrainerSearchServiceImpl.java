package com.fitech.app.users.domain.services.impl;

import com.fitech.app.commons.util.MapperUtil;
import com.fitech.app.users.application.exception.UserNotFoundException;
import com.fitech.app.users.domain.entities.User;
import com.fitech.app.users.application.dto.TrainerSearchDto;
import com.fitech.app.users.application.dto.UserResponseDto;
import com.fitech.app.users.application.dto.PublicTrainerDto;
import com.fitech.app.users.domain.services.TrainerSearchService;
import com.fitech.app.users.infrastructure.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TrainerSearchServiceImpl implements TrainerSearchService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserResponseDto> searchTrainers(TrainerSearchDto searchDto) {
        log.info("Searching trainers with criteria: {}", searchDto);
        
        List<User> trainers;
        
        // Determinar qué tipo de búsqueda realizar
        boolean hasGoalFilter = searchDto.getFitnessGoalIds() != null && !searchDto.getFitnessGoalIds().isEmpty();
        boolean hasNameFilter = searchDto.getQuery() != null && !searchDto.getQuery().trim().isEmpty();
        
        if (hasGoalFilter && hasNameFilter) {
            // Buscar por objetivos fitness Y nombre
            trainers = searchTrainersByGoalsAndName(searchDto.getFitnessGoalIds(), searchDto.getQuery().trim());
        } else if (hasGoalFilter) {
            // Buscar solo por objetivos fitness
            trainers = userRepository.findTrainersByFitnessGoalIds(searchDto.getFitnessGoalIds());
        } else if (hasNameFilter) {
            // Buscar solo por nombre
            trainers = searchTrainersByName(searchDto.getQuery().trim());
        } else {
            // Sin filtros - devolver todos los entrenadores
            trainers = userRepository.findByType(1);
        }
        
        log.info("Found {} trainers matching criteria", trainers.size());
        
        return trainers.stream()
            .map(trainer -> MapperUtil.map(trainer, UserResponseDto.class))
            .collect(Collectors.toList());
    }
    
    private List<User> searchTrainersByName(String query) {
        // Buscar por nombre y apellido por separado y combinar resultados
        Set<User> results = new HashSet<>();
        
        // Buscar por firstName
        results.addAll(userRepository.findByTypeAndPersonFirstNameContainingIgnoreCase(1, query));
        
        // Buscar por lastName
        results.addAll(userRepository.findByTypeAndPersonLastNameContainingIgnoreCase(1, query));
        
        return results.stream().collect(Collectors.toList());
    }
    
    private List<User> searchTrainersByGoalsAndName(List<Integer> fitnessGoalIds, String query) {
        // Primero obtener todos los trainers con los fitness goals
        List<User> trainersByGoals = userRepository.findTrainersByFitnessGoalIds(fitnessGoalIds);
        
        // Luego filtrar por nombre en Java
        String lowerQuery = query.toLowerCase();
        return trainersByGoals.stream()
            .filter(trainer -> {
                String firstName = trainer.getPerson().getFirstName();
                String lastName = trainer.getPerson().getLastName();
                
                boolean matchesFirstName = firstName != null && firstName.toLowerCase().contains(lowerQuery);
                boolean matchesLastName = lastName != null && lastName.toLowerCase().contains(lowerQuery);
                
                return matchesFirstName || matchesLastName;
            })
            .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getTrainerProfile(Integer trainerId) {
        log.info("Getting trainer profile for ID: {}", trainerId);
        
        User trainer = userRepository.findById(trainerId)
            .orElseThrow(() -> new UserNotFoundException("Trainer not found with id: " + trainerId));
        
        // Verificar que el usuario sea efectivamente un trainer
        if (trainer.getType() != 1) {
            throw new UserNotFoundException("User with id " + trainerId + " is not a trainer");
        }
        
        return MapperUtil.map(trainer, UserResponseDto.class);
    }

    @Override
    public List<UserResponseDto> getAllTrainers() {
        log.info("Getting all trainers");
        
        List<User> trainers = userRepository.findByType(1);
        
        return trainers.stream()
            .map(trainer -> MapperUtil.map(trainer, UserResponseDto.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<PublicTrainerDto> searchTrainersPublic(TrainerSearchDto searchDto) {
        log.info("Searching trainers (public) with criteria: {}", searchDto);
        
        List<User> trainers;
        
        // Usar la misma lógica de búsqueda que el método privado
        boolean hasGoalFilter = searchDto.getFitnessGoalIds() != null && !searchDto.getFitnessGoalIds().isEmpty();
        boolean hasNameFilter = searchDto.getQuery() != null && !searchDto.getQuery().trim().isEmpty();
        
        if (hasGoalFilter && hasNameFilter) {
            trainers = searchTrainersByGoalsAndName(searchDto.getFitnessGoalIds(), searchDto.getQuery().trim());
        } else if (hasGoalFilter) {
            trainers = userRepository.findTrainersByFitnessGoalIds(searchDto.getFitnessGoalIds());
        } else if (hasNameFilter) {
            trainers = searchTrainersByName(searchDto.getQuery().trim());
        } else {
            trainers = userRepository.findByType(1);
        }
        
        log.info("Found {} trainers matching criteria (public)", trainers.size());
        
        return trainers.stream()
            .map(trainer -> MapperUtil.map(trainer, PublicTrainerDto.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<PublicTrainerDto> getAllTrainersPublic() {
        log.info("Getting all trainers (public)");
        
        List<User> trainers = userRepository.findByType(1);
        
        return trainers.stream()
            .map(trainer -> MapperUtil.map(trainer, PublicTrainerDto.class))
            .collect(Collectors.toList());
    }
} 