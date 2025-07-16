package com.fitech.app.users.domain.services.impl;

import com.fitech.app.commons.util.PaginationUtil;
import com.fitech.app.users.application.dto.UnitOfMeasureDto;
import com.fitech.app.users.application.exception.UnitOfMeasurerNotFoundException;
import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.domain.entities.UnitOfMeasure;
import com.fitech.app.users.domain.services.UnitOfMeasureService;
import com.fitech.app.users.infrastructure.repository.UnitOfMeasureRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {

    private final UnitOfMeasureRepository unitOfMeasureRepository;

    @Autowired
    public UnitOfMeasureServiceImpl(UnitOfMeasureRepository unitOfMeasureRepository) {
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Override
    @Transactional
    public UnitOfMeasureDto create(UnitOfMeasureDto unitOfMeasureDto) {
        if (unitOfMeasureRepository.existsByName(unitOfMeasureDto.getName())) {
            throw new IllegalArgumentException("Unit of measure with this name already exists");
        }
        if (unitOfMeasureRepository.existsBySymbol(unitOfMeasureDto.getSymbol())) {
            throw new IllegalArgumentException("Unit of measure with this symbol already exists");
        }

        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setName(unitOfMeasureDto.getName());
        unitOfMeasure.setSymbol(unitOfMeasureDto.getSymbol());
        unitOfMeasure.setDescription(unitOfMeasureDto.getDescription());

        UnitOfMeasure savedUnit = unitOfMeasureRepository.save(unitOfMeasure);
        return convertToDto(savedUnit);
    }

    @Override
    @Transactional
    public UnitOfMeasureDto update(Integer id, UnitOfMeasureDto unitOfMeasureDto) {
        UnitOfMeasure existingUnit = unitOfMeasureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unit of measure not found"));

        if (!existingUnit.getName().equals(unitOfMeasureDto.getName()) && 
            unitOfMeasureRepository.existsByName(unitOfMeasureDto.getName())) {
            throw new IllegalArgumentException("Unit of measure with this name already exists");
        }
        if (!existingUnit.getSymbol().equals(unitOfMeasureDto.getSymbol()) && 
            unitOfMeasureRepository.existsBySymbol(unitOfMeasureDto.getSymbol())) {
            throw new IllegalArgumentException("Unit of measure with this symbol already exists");
        }

        existingUnit.setName(unitOfMeasureDto.getName());
        existingUnit.setSymbol(unitOfMeasureDto.getSymbol());
        existingUnit.setDescription(unitOfMeasureDto.getDescription());

        UnitOfMeasure updatedUnit = unitOfMeasureRepository.save(existingUnit);
        return convertToDto(updatedUnit);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!unitOfMeasureRepository.existsById(id)) {
            throw new EntityNotFoundException("Unit of measure not found");
        }
        unitOfMeasureRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UnitOfMeasureDto findById(Integer id) {
        UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unit of measure not found"));
        return convertToDto(unitOfMeasure);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPage<UnitOfMeasureDto> findAll(Pageable pageable) {
        Page<UnitOfMeasure> unitsPage = unitOfMeasureRepository.findAll(pageable);
        if(unitsPage.isEmpty()){
            throw new UnitOfMeasurerNotFoundException("No units of measure found");
        }
        return PaginationUtil.prepareResultWrapper(unitsPage, UnitOfMeasureDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return unitOfMeasureRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySymbol(String symbol) {
        return unitOfMeasureRepository.existsBySymbol(symbol);
    }

    private UnitOfMeasureDto convertToDto(UnitOfMeasure unitOfMeasure) {
        UnitOfMeasureDto dto = new UnitOfMeasureDto();
        dto.setId(unitOfMeasure.getId());
        dto.setName(unitOfMeasure.getName());
        dto.setSymbol(unitOfMeasure.getSymbol());
        dto.setDescription(unitOfMeasure.getDescription());
        return dto;
    }
} 