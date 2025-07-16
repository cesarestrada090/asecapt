package com.fitech.app.users.domain.services;

import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.application.dto.PersonDto;
import com.fitech.app.users.domain.entities.Person;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PersonService {
    Person save(PersonDto personDto);
    PersonDto update(Integer id, PersonDto dto);
    Optional<PersonDto> findByDocumentNumber(String documentNumber);
    PersonDto getById(Integer id);
    Person getPersonEntityById(Integer id);
    ResultPage<PersonDto> getAll(Pageable paging);
}
