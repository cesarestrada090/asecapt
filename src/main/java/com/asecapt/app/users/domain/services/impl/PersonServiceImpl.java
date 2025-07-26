package com.asecapt.app.users.domain.services.impl;

import com.asecapt.app.commons.util.MapperUtil;
import com.asecapt.app.commons.util.PaginationUtil;
import com.asecapt.app.users.application.dto.PersonDto;
import com.asecapt.app.users.domain.entities.Person;
import com.asecapt.app.users.application.exception.DuplicatedUserException;
import com.asecapt.app.users.application.exception.UserNotFoundException;
import com.asecapt.app.users.infrastructure.repository.PersonRepository;
import com.asecapt.app.users.domain.services.PersonService;
import com.asecapt.app.users.application.dto.ResultPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    @Transactional
    public Person save(PersonDto personDto) {
        validatePersonCreation(personDto);
        Person person = createPersonEntity(personDto);
        return personRepository.save(person);
    }

    private void validatePersonCreation(PersonDto personDto) {
        if (personRepository.findByDocumentNumber(personDto.getDocumentNumber()).isPresent()) {
            throw new DuplicatedUserException("Document number already exists: " + personDto.getDocumentNumber());
        }
    }

    private Person createPersonEntity(PersonDto personDto) {
        return MapperUtil.map(personDto, Person.class);
    }

    @Override
    @Transactional
    public PersonDto update(Integer id, PersonDto personDto) {
        // Get the existing person entity
        Person personEntity = getPersonEntityById(id);
        
        // Validate document number if it's being changed
        if(personDto.hasDifferentDocumentNumber(personEntity.getDocumentNumber())) {
            if(findByDocumentNumber(personDto.getDocumentNumber()).isPresent()) {
                throw new DuplicatedUserException("Document number already exists: " + personDto.getDocumentNumber());
            }
        }
        
        // Map DTO to entity while preserving the ID
        Person updatedPerson = MapperUtil.map(personDto, Person.class);
        updatedPerson.setId(id);  // Ensure ID is preserved
        
        // Save the updated entity
        personEntity = personRepository.save(updatedPerson);
        
        // Map back to DTO and return
        return MapperUtil.map(personEntity, PersonDto.class);
    }
    

    @Override
    public Optional<PersonDto> findByDocumentNumber(String documentNumber){
        Optional<Person> person = personRepository.findByDocumentNumber(documentNumber);
        return person.map(value -> MapperUtil.map(value, PersonDto.class));
    }

    @Override
    public PersonDto getById(Integer id){
        Optional<Person> person = personRepository.findById(id);
        if(person.isEmpty()){
            throw new UserNotFoundException("Person not found with ID: " + id);
        }
        return MapperUtil.map(person.get(), PersonDto.class);
    }

    @Override
    public Person getPersonEntityById(Integer id) {
        Optional<Person> optPerson = personRepository.findById(id);
        return optPerson.orElseThrow(() -> new UserNotFoundException("Person not found with ID: " + id));
    }

    @Override
    public ResultPage<PersonDto> getAll(Pageable paging){
        Page<Person> personList = personRepository.findAll(paging);
        if(personList.isEmpty()){
            throw new UserNotFoundException("No persons found");
        }
        return PaginationUtil.prepareResultWrapper(personList, PersonDto.class);
    }
}
