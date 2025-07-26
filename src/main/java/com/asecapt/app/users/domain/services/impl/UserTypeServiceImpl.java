package com.asecapt.app.users.domain.services.impl;

import com.asecapt.app.commons.util.MapperUtil;
import com.asecapt.app.commons.util.PaginationUtil;
import com.asecapt.app.users.application.dto.ResultPage;
import com.asecapt.app.users.domain.entities.UserType;
import com.asecapt.app.users.application.dto.UserTypeDto;
import com.asecapt.app.users.domain.services.UserTypeService;
import com.asecapt.app.users.infrastructure.repository.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserTypeServiceImpl implements UserTypeService {

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Override
    public UserTypeDto save(UserTypeDto dto) {
        validateUserTypeCreation(dto);
        UserType entity = createUserTypeEntity(dto);
        UserType savedEntity = userTypeRepository.save(entity);
        return MapperUtil.map(savedEntity, UserTypeDto.class);
    }

    private void validateUserTypeCreation(UserTypeDto dto) {
        // Add any validation logic here if needed
    }

    private UserType createUserTypeEntity(UserTypeDto dto) {
        return MapperUtil.map(dto, UserType.class);
    }

    @Override
    public UserTypeDto update(Integer id, UserTypeDto dto) {
        Optional<UserType> optionalEntity = userTypeRepository.findById(id);
        if (optionalEntity.isPresent()) {
            UserType entity = optionalEntity.get();
            entity.setName(dto.getName());
            entity = userTypeRepository.save(entity);
            return MapperUtil.map(entity, UserTypeDto.class);
        }
        return null;
    }

    @Override
    public UserTypeDto getById(Integer id) {
        Optional<UserType> optionalEntity = userTypeRepository.findById(id);
        return optionalEntity.map(entity -> MapperUtil.map(entity, UserTypeDto.class)).orElse(null);
    }

    @Override
    public ResultPage<UserTypeDto> getAll(Pageable paging) {
        return PaginationUtil.prepareResultWrapper(userTypeRepository.findAll(paging), UserTypeDto.class);
    }

    @Override
    public List<UserTypeDto> getAll() {
        return MapperUtil.mapAll(userTypeRepository.findAll(), UserTypeDto.class);
    }
} 