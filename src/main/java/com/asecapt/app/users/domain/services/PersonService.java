package com.asecapt.app.users.domain.services;

import com.asecapt.app.users.application.dto.ResultPage;
import com.asecapt.app.users.application.dto.PersonDto;
import com.asecapt.app.users.domain.entities.Person;
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
