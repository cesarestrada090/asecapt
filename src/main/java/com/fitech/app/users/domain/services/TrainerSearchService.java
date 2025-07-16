package com.fitech.app.users.domain.services;

import com.fitech.app.users.application.dto.TrainerSearchDto;
import com.fitech.app.users.application.dto.UserResponseDto;
import com.fitech.app.users.application.dto.PublicTrainerDto;

import java.util.List;

public interface TrainerSearchService {
    List<UserResponseDto> searchTrainers(TrainerSearchDto searchDto);
    List<PublicTrainerDto> searchTrainersPublic(TrainerSearchDto searchDto);
    UserResponseDto getTrainerProfile(Integer trainerId);
    List<UserResponseDto> getAllTrainers();
    List<PublicTrainerDto> getAllTrainersPublic();
} 