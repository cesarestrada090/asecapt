package com.fitech.app.users.domain.services;

import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.application.dto.FitnessGoalTypeDto;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface FitnessGoalTypeService {
    FitnessGoalTypeDto save(FitnessGoalTypeDto dto);
    FitnessGoalTypeDto update(Integer id, FitnessGoalTypeDto dto);
    FitnessGoalTypeDto getById(Integer id);
    ResultPage<FitnessGoalTypeDto> getAll(Pageable paging);
    List<FitnessGoalTypeDto> getAll();
} 