package com.asecapt.app.users.domain.services;

import com.asecapt.app.users.application.dto.ClientProfileDTO;
import com.asecapt.app.users.domain.entities.User;
import com.asecapt.app.users.domain.entities.Person;
import com.asecapt.app.users.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

@Service
public class ClientProfileService {

    @Autowired
    private UserRepository userRepository;

    public ClientProfileDTO getClientProfile(Long clientId) {
        User user = userRepository.findById(clientId.intValue())
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

 


        return new ClientProfileDTO(
                clientId,
                person.getFirstName(),
                person.getLastName(),
                person.getDocumentNumber(),
                person.getDocumentType(),
                person.getBio(),
                person.getProfilePhotoId(),
                person.getGender(),
                birthDateStr
        );
    }
} 