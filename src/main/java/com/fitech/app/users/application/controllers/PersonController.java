package com.fitech.app.users.application.controllers;

import com.fitech.app.commons.application.controllers.BaseController;
import com.fitech.app.commons.util.MapperUtil;
import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.application.dto.PersonDto;
import com.fitech.app.users.domain.services.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.logging.Logger;

@RequestMapping("v1/app/person")
@RestController
@Tag(name = "Person Management", description = "Personal information and profile data management")
@SecurityRequirement(name = "bearerAuth")
public class PersonController extends BaseController {
    private final PersonService personService;
    private static final Logger log = Logger.getLogger(PersonController.class.getName());

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
    public ResponseEntity<PersonDto> save(@Valid @RequestBody PersonDto personDto) {
        log.info("Creating person: " + personDto);
        personDto = MapperUtil.map(personService.save(personDto), PersonDto.class);
        return new ResponseEntity<>(personDto, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
    public ResponseEntity<PersonDto> update(@PathVariable(value = "id") int id, @Valid @RequestBody PersonDto personDto) {
        log.info("Updating person with id: " + id);
        personDto = personService.update(id, personDto);
        return new ResponseEntity<>(personDto, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<PersonDto> getById(@PathVariable(value = "id") Integer id) {
        log.info("Getting person with id: " + id);
        PersonDto personDto = personService.getById(id);
        return new ResponseEntity<>(personDto, HttpStatus.OK);
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Getting all persons with page: " + page + " and size: " + size);
        Pageable paging = PageRequest.of(page-1, size);
        ResultPage<PersonDto> resultPageWrapper = personService.getAll(paging);
        Map<String, Object> response = prepareResponse(resultPageWrapper);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    protected String getResource() {
        return "persons";
    }
} 