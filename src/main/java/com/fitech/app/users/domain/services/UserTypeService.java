package com.fitech.app.users.domain.services;

import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.application.dto.UserTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserTypeService {
    UserTypeDto save(UserTypeDto dto);
    UserTypeDto update(Integer id, UserTypeDto dto);
    UserTypeDto getById(Integer id);
    ResultPage<UserTypeDto> getAll(Pageable paging);
    List<UserTypeDto> getAll();
} 