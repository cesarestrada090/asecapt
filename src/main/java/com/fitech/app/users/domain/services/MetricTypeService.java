package com.fitech.app.users.domain.services;

import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.application.dto.MetricTypeDto;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface MetricTypeService {
    MetricTypeDto save(MetricTypeDto dto);
    MetricTypeDto update(Integer id, MetricTypeDto dto);
    MetricTypeDto getById(Integer id);
    ResultPage<MetricTypeDto> getAll(Pageable paging);
    List<MetricTypeDto> getAll();
} 