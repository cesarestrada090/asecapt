package com.fitech.app.users.domain.services;

import com.fitech.app.users.application.dto.UnitOfMeasureDto;

import com.fitech.app.users.application.dto.ResultPage;
import org.springframework.data.domain.Pageable;

public interface UnitOfMeasureService {
    UnitOfMeasureDto create(UnitOfMeasureDto unitOfMeasureDto);
    UnitOfMeasureDto update(Integer id, UnitOfMeasureDto unitOfMeasureDto);
    void delete(Integer id);
    UnitOfMeasureDto findById(Integer id);
    ResultPage<UnitOfMeasureDto> findAll(Pageable pageable);
    boolean existsByName(String name);
    boolean existsBySymbol(String symbol);
} 