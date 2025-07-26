package com.asecapt.app.users.application.controllers;

import com.asecapt.app.users.application.dto.ResultPage;
import com.asecapt.app.users.application.dto.UserTypeDto;
import com.asecapt.app.users.domain.services.UserTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/app/user-type")
@Tag(name = "User Types", description = "Management of user type categories (client/trainer)")
@SecurityRequirement(name = "bearerAuth")
public class UserTypeController {

    @Autowired
    private UserTypeService userTypeService;

    @PostMapping
    public ResponseEntity<UserTypeDto> create(@RequestBody UserTypeDto dto) {
        return ResponseEntity.ok(userTypeService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserTypeDto> update(@PathVariable Integer id, @RequestBody UserTypeDto dto) {
        UserTypeDto updated = userTypeService.update(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserTypeDto> getById(@PathVariable Integer id) {
        UserTypeDto dto = userTypeService.getById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<ResultPage<UserTypeDto>> getAll(Pageable paging) {
        return ResponseEntity.ok(userTypeService.getAll(paging));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserTypeDto>> getAll() {
        return ResponseEntity.ok(userTypeService.getAll());
    }
    
} 