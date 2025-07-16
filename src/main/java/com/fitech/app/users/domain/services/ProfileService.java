package com.fitech.app.users.domain.services;

import com.fitech.app.users.application.dto.ProfileUpdateDto;
import com.fitech.app.users.application.dto.UserFilesDto;
import com.fitech.app.users.application.dto.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProfileService {
    UserResponseDto getProfile(Integer userId);
    UserResponseDto updateProfile(Integer userId, ProfileUpdateDto profileUpdate);
    UserFilesDto uploadProfilePhoto(Integer userId, MultipartFile file) throws IOException;
    UserFilesDto uploadPhoto(Integer userId, MultipartFile file) throws IOException;
    UserFilesDto uploadPresentationVideo(Integer userId, MultipartFile file) throws IOException;
    List<UserFilesDto> getUserPhotos(Integer userId);
    void deletePhoto(Integer userId, Integer photoId);
    void setProfilePhoto(Integer userId, Integer photoId);
    void deletePresentationVideo(Integer userId);
    UserResponseDto updateFitnessGoal(Integer userId, Integer fitnessGoalTypeId);
    UserResponseDto updateFitnessGoals(Integer userId, List<Integer> fitnessGoalTypeIds);
} 