package com.fitech.app.users.domain.services.impl;

import com.fitech.app.commons.util.MapperUtil;
import com.fitech.app.commons.util.PaginationUtil;
import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.domain.entities.MetricType;
import com.fitech.app.users.application.dto.MetricTypeDto;
import com.fitech.app.users.domain.services.MetricTypeService;
import com.fitech.app.users.infrastructure.repository.MetricTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MetricTypeServiceImpl implements MetricTypeService {

    @Autowired
    private MetricTypeRepository metricTypeRepository;

    @Override
    public MetricTypeDto save(MetricTypeDto dto) {
        validateMetricTypeCreation(dto);
        MetricType entity = createMetricTypeEntity(dto);
        MetricType savedEntity = metricTypeRepository.save(entity);
        return MapperUtil.map(savedEntity, MetricTypeDto.class);
    }

    private void validateMetricTypeCreation(MetricTypeDto dto) {
        // Add any validation logic here if needed
    }

    private MetricType createMetricTypeEntity(MetricTypeDto dto) {
        return MapperUtil.map(dto, MetricType.class);
    }

    @Override
    public MetricTypeDto update(Integer id, MetricTypeDto dto) {
        Optional<MetricType> optionalEntity = metricTypeRepository.findById(id);
        if (optionalEntity.isPresent()) {
            MetricType entity = optionalEntity.get();
            entity.setName(dto.getName());
            entity = metricTypeRepository.save(entity);
            return MapperUtil.map(entity, MetricTypeDto.class);
        }
        return null;
    }

    @Override
    public MetricTypeDto getById(Integer id) {
        Optional<MetricType> optionalEntity = metricTypeRepository.findById(id);
        return optionalEntity.map(entity -> MapperUtil.map(entity, MetricTypeDto.class)).orElse(null);
    }

    @Override
    public ResultPage<MetricTypeDto> getAll(Pageable paging) {
        return PaginationUtil.prepareResultWrapper(metricTypeRepository.findAll(paging), MetricTypeDto.class);
    }

    @Override
    public List<MetricTypeDto> getAll() {
        return MapperUtil.mapAll(metricTypeRepository.findAll(), MetricTypeDto.class);
    }
} 