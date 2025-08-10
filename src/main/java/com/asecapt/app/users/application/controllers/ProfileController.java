package com.asecapt.app.users.application.controllers;

import com.asecapt.app.users.application.dto.ProfileUpdateDto;
import com.asecapt.app.users.application.dto.UserFilesDto;
import com.asecapt.app.users.application.dto.UserResponseDto;
import com.asecapt.app.users.domain.services.ProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/app/profile")
@Tag(name = "User Profile", description = "User profile management, photos, and personal information")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getProfile(@PathVariable("userId") Integer userId) {
        UserResponseDto profile = profileService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateProfile(
            @PathVariable("userId") Integer userId,
            @Valid @RequestBody ProfileUpdateDto profileUpdate
    ) {
        UserResponseDto updatedProfile = profileService.updateProfile(userId, profileUpdate);
        return ResponseEntity.ok(updatedProfile);
    }

    @PostMapping("/{userId}/photo")
    public ResponseEntity<UserFilesDto> uploadProfilePhoto(
            @PathVariable("userId") Integer userId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        UserFilesDto photo = profileService.uploadProfilePhoto(userId, file);
        return ResponseEntity.ok(photo);
    }

    @PostMapping("/{userId}/photos")
    public ResponseEntity<UserFilesDto> uploadPhoto(
            @PathVariable("userId") Integer userId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        UserFilesDto photo = profileService.uploadPhoto(userId, file);
        return ResponseEntity.ok(photo);
    }

    @GetMapping("/{userId}/photos")
    public ResponseEntity<List<UserFilesDto>> getUserPhotos(
            @PathVariable("userId") Integer userId
    ) {
        List<UserFilesDto> photos = profileService.getUserPhotos(userId);
        return ResponseEntity.ok(photos);
    }

    @DeleteMapping("/{userId}/photos/{photoId}")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable("userId") Integer userId,
            @PathVariable("photoId") Integer photoId
    ) {
        profileService.deletePhoto(userId, photoId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/profile-photo")
    public ResponseEntity<Void> setProfilePhoto(@PathVariable("userId") Integer userId, @RequestBody java.util.Map<String, Integer> body) {
        Integer photoId = body.get("photoId");
        profileService.setProfilePhoto(userId, photoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/presentation-video")
    public ResponseEntity<UserFilesDto> uploadPresentationVideo(
            @PathVariable("userId") Integer userId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        UserFilesDto video = profileService.uploadPresentationVideo(userId, file);
        return ResponseEntity.ok(video);
    }

    @DeleteMapping("/{userId}/presentation-video")
    public ResponseEntity<Void> deletePresentationVideo(
            @PathVariable("userId") Integer userId
    ) {
        profileService.deletePresentationVideo(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @PathVariable("userId") Integer userId,
            @RequestBody ChangePasswordRequest request
    ) {
        try {
            profileService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password changed successfully");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // DTO for change password request
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
        private String confirmPassword;

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
}
