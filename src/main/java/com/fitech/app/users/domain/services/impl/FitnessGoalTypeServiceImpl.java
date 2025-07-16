package com.fitech.app.users.domain.services.impl;

import com.fitech.app.commons.util.MapperUtil;
import com.fitech.app.commons.util.PaginationUtil;
import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.domain.entities.FitnessGoalType;
import com.fitech.app.users.application.dto.FitnessGoalTypeDto;
import com.fitech.app.users.domain.services.FitnessGoalTypeService;
import com.fitech.app.users.infrastructure.repository.FitnessGoalTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FitnessGoalTypeServiceImpl implements FitnessGoalTypeService {

    @Autowired
    private FitnessGoalTypeRepository fitnessGoalTypeRepository;


    @Override
    public FitnessGoalTypeDto save(FitnessGoalTypeDto dto) {
        validateFitnessGoalTypeCreation(dto);
        FitnessGoalType entity = createFitnessGoalTypeEntity(dto);
        FitnessGoalType savedEntity = fitnessGoalTypeRepository.save(entity);
        return MapperUtil.map(savedEntity, FitnessGoalTypeDto.class);
    }

    private void validateFitnessGoalTypeCreation(FitnessGoalTypeDto dto) {
        // Add any validation logic here if needed
    }

    private FitnessGoalType createFitnessGoalTypeEntity(FitnessGoalTypeDto dto) {
        return MapperUtil.map(dto, FitnessGoalType.class);
    }

    @Override
    public FitnessGoalTypeDto update(Integer id, FitnessGoalTypeDto dto) {
        Optional<FitnessGoalType> optionalEntity = fitnessGoalTypeRepository.findById(id);
        if (optionalEntity.isPresent()) {
            FitnessGoalType entity = optionalEntity.get();
            entity.setName(dto.getName());
            entity = fitnessGoalTypeRepository.save(entity);
            return MapperUtil.map(entity, FitnessGoalTypeDto.class);
        }
        return null;
    }

    @Override
    public FitnessGoalTypeDto getById(Integer id) {
        Optional<FitnessGoalType> optionalEntity = fitnessGoalTypeRepository.findById(id);
        return optionalEntity.map(entity -> MapperUtil.map(entity, FitnessGoalTypeDto.class)).orElse(null);
    }

    @Override
    public ResultPage<FitnessGoalTypeDto> getAll(Pageable paging) {
        return PaginationUtil.prepareResultWrapper(fitnessGoalTypeRepository.findAll(paging), FitnessGoalTypeDto.class);
    }

    @Override
    public List<FitnessGoalTypeDto> getAll() {
        return MapperUtil.mapAll(fitnessGoalTypeRepository.findAll(), FitnessGoalTypeDto.class);
    }
} 