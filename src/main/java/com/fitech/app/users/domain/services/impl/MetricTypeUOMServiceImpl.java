package com.fitech.app.users.domain.services.impl;

import com.fitech.app.users.application.exception.DuplicatedMetricTypeUOMException;
import com.fitech.app.users.application.dto.MetricTypeUOMDto;
import com.fitech.app.users.application.dto.MetricTypeUOMDetailDto;
import com.fitech.app.users.application.exception.MetricTypeUomNotFoundException;
import com.fitech.app.users.application.exception.UnitOfMeasureNotFoundException;
import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.domain.entities.MetricType;
import com.fitech.app.users.domain.entities.MetricTypeUOM;
import com.fitech.app.users.domain.entities.UnitOfMeasure;
import com.fitech.app.users.domain.services.MetricTypeUOMService;
import com.fitech.app.users.infrastructure.repository.MetricTypeRepository;
import com.fitech.app.users.infrastructure.repository.MetricTypeUOMRepository;
import com.fitech.app.users.infrastructure.repository.UnitOfMeasureRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MetricTypeUOMServiceImpl implements MetricTypeUOMService {

    private final MetricTypeUOMRepository metricTypeUOMRepository;
    private final MetricTypeRepository metricTypeRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;

    @Autowired
    public MetricTypeUOMServiceImpl(
            MetricTypeUOMRepository metricTypeUOMRepository,
            MetricTypeRepository metricTypeRepository,
            UnitOfMeasureRepository unitOfMeasureRepository) {
        this.metricTypeUOMRepository = metricTypeUOMRepository;
        this.metricTypeRepository = metricTypeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Override
    @Transactional
    public MetricTypeUOMDto create(MetricTypeUOMDto metricTypeUOMDto) {
        if (existsByMetricTypeAndUnitOfMeasure(metricTypeUOMDto.getMetricTypeId(), metricTypeUOMDto.getUnitOfMeasureId())) {
            throw new DuplicatedMetricTypeUOMException("A relationship already exists between metric type ID: " + metricTypeUOMDto.getMetricTypeId() + " and unit of measure ID: " + metricTypeUOMDto.getUnitOfMeasureId());
        }

        MetricType metricType = metricTypeRepository.findById(metricTypeUOMDto.getMetricTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Metric type not found"));
        
        UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findById(metricTypeUOMDto.getUnitOfMeasureId())
                .orElseThrow(() -> new UnitOfMeasureNotFoundException("Unit of measure not found with ID: " + metricTypeUOMDto.getUnitOfMeasureId()));

        MetricTypeUOM metricTypeUOM = new MetricTypeUOM();
        metricTypeUOM.setMetricType(metricType);
        metricTypeUOM.setUnitOfMeasure(unitOfMeasure);
        metricTypeUOM.setDefault(metricTypeUOMDto.isDefault());

        MetricTypeUOM saved = metricTypeUOMRepository.save(metricTypeUOM);
        return convertToDto(saved);
    }

    @Override
    @Transactional
    public MetricTypeUOMDto update(Integer id, MetricTypeUOMDto metricTypeUOMDto) {
        MetricTypeUOM existing = metricTypeUOMRepository.findById(id)
                .orElseThrow(() -> new MetricTypeUomNotFoundException("Metric type - unit of measure relationship not found with ID: " + id));

        if (!existing.getMetricType().getId().equals(metricTypeUOMDto.getMetricTypeId()) ||
            !existing.getUnitOfMeasure().getId().equals(metricTypeUOMDto.getUnitOfMeasureId())) {
            if (existsByMetricTypeAndUnitOfMeasure(metricTypeUOMDto.getMetricTypeId(), metricTypeUOMDto.getUnitOfMeasureId())) {
                throw new DuplicatedMetricTypeUOMException("A relationship already exists between metric type ID: " + metricTypeUOMDto.getMetricTypeId() + " and unit of measure ID: " + metricTypeUOMDto.getUnitOfMeasureId());
            }
        }

        MetricType metricType = metricTypeRepository.findById(metricTypeUOMDto.getMetricTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Metric type not found"));
        
        UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findById(metricTypeUOMDto.getUnitOfMeasureId())
                .orElseThrow(() -> new UnitOfMeasureNotFoundException("Unit of measure not found with ID: " + metricTypeUOMDto.getUnitOfMeasureId()));

        existing.setMetricType(metricType);
        existing.setUnitOfMeasure(unitOfMeasure);
        existing.setDefault(metricTypeUOMDto.isDefault());

        MetricTypeUOM updated = metricTypeUOMRepository.save(existing);
        return convertToDto(updated);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!metricTypeUOMRepository.existsById(id)) {
            throw new MetricTypeUomNotFoundException("Metric type - unit of measure relationship not found with ID: " + id);
        }
        metricTypeUOMRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public MetricTypeUOMDto findById(Integer id) {
        MetricTypeUOM metricTypeUOM = metricTypeUOMRepository.findById(id)
                .orElseThrow(() -> new MetricTypeUomNotFoundException("Metric type - unit of measure relationship not found with ID: " + id));
        return convertToDto(metricTypeUOM);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPage<MetricTypeUOMDetailDto> findAll(Pageable pageable) {
        Page<MetricTypeUOM> unitsPage = metricTypeUOMRepository.findAll(pageable);
        List<MetricTypeUOMDetailDto> dtos = unitsPage.getContent().stream()
            .map(this::convertToDetailDto)
            .collect(Collectors.toList());
        ResultPage<MetricTypeUOMDetailDto> resultPage = new ResultPage<>();
        resultPage.setPagesResult(dtos);
        resultPage.setCurrentPage(unitsPage.getNumber());
        resultPage.setTotalItems(unitsPage.getTotalElements());
        resultPage.setTotalPages(unitsPage.getTotalPages());
        return resultPage;
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPage<MetricTypeUOMDetailDto> findByMetricType(Integer metricTypeId, Pageable pageable) {
        Page<MetricTypeUOM> unitsPage = metricTypeUOMRepository.findByMetricTypeId(metricTypeId, pageable);
        List<MetricTypeUOMDetailDto> dtos = unitsPage.getContent().stream()
            .map(this::convertToDetailDto)
            .collect(Collectors.toList());
        ResultPage<MetricTypeUOMDetailDto> resultPage = new ResultPage<>();
        resultPage.setPagesResult(dtos);
        resultPage.setCurrentPage(unitsPage.getNumber());
        resultPage.setTotalItems(unitsPage.getTotalElements());
        resultPage.setTotalPages(unitsPage.getTotalPages());
        return resultPage;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByMetricTypeAndUnitOfMeasure(Integer metricTypeId, Integer unitOfMeasureId) {
        return metricTypeUOMRepository.existsByMetricTypeIdAndUnitOfMeasureId(metricTypeId, unitOfMeasureId);
    }

    private MetricTypeUOMDto convertToDto(MetricTypeUOM metricTypeUOM) {
        MetricTypeUOMDto dto = new MetricTypeUOMDto();
        dto.setId(metricTypeUOM.getId());
        dto.setMetricTypeId(metricTypeUOM.getMetricType() != null ? metricTypeUOM.getMetricType().getId() : null);
        dto.setUnitOfMeasureId(metricTypeUOM.getUnitOfMeasure() != null ? metricTypeUOM.getUnitOfMeasure().getId() : null);
        dto.setDefault(metricTypeUOM.isDefault());
        return dto;
    }

    private MetricTypeUOMDetailDto convertToDetailDto(MetricTypeUOM metricTypeUOM) {
        MetricTypeUOMDetailDto dto = new MetricTypeUOMDetailDto();
        dto.setId(metricTypeUOM.getId());
        
        if (metricTypeUOM.getMetricType() != null) {
            dto.setMetricTypeId(metricTypeUOM.getMetricType().getId());
            dto.setMetricTypeName(metricTypeUOM.getMetricType().getName());
        }
        
        if (metricTypeUOM.getUnitOfMeasure() != null) {
            dto.setUnitOfMeasureId(metricTypeUOM.getUnitOfMeasure().getId());
            dto.setUnitOfMeasureName(metricTypeUOM.getUnitOfMeasure().getName());
            dto.setUnitOfMeasureSymbol(metricTypeUOM.getUnitOfMeasure().getSymbol());
            dto.setUnitOfMeasureDescription(metricTypeUOM.getUnitOfMeasure().getDescription());
        }
        
        dto.setDefault(metricTypeUOM.isDefault());
        return dto;
    }
} 