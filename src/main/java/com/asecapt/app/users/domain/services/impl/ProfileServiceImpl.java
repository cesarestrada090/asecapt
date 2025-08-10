package com.asecapt.app.users.domain.services.impl;

import com.asecapt.app.commons.util.MapperUtil;
import com.asecapt.app.users.application.exception.StorageException;
import com.asecapt.app.users.application.exception.UserNotFoundException;
import com.asecapt.app.users.domain.entities.Person;
import com.asecapt.app.users.domain.entities.User;
import com.asecapt.app.users.domain.entities.UserFiles;
import com.asecapt.app.users.application.dto.ProfileUpdateDto;
import com.asecapt.app.users.application.dto.UserFilesDto;
import com.asecapt.app.users.application.dto.UserResponseDto;
import com.asecapt.app.users.domain.services.FileUploadService;
import com.asecapt.app.users.domain.services.ProfileService;
import com.asecapt.app.users.infrastructure.repository.UserFileRepository;
import com.asecapt.app.users.infrastructure.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFileRepository userFileRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto getProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return MapperUtil.map(user, UserResponseDto.class);
    }

    @Override
    @Transactional
    public UserResponseDto updateProfile(Integer userId, ProfileUpdateDto profileUpdate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Person person = user.getPerson();
        person.setFirstName(profileUpdate.getFirstName());
        person.setLastName(profileUpdate.getLastName());
        person.setPhoneNumber(profileUpdate.getPhoneNumber());
        // No actualizar el email - mantener el actual
        // person.setEmail(profileUpdate.getEmail()); // Comentado para proteger email
        person.setBio(profileUpdate.getBio());
        person.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return MapperUtil.map(savedUser, UserResponseDto.class);
    }

    @Override
    @Transactional
    public UserFilesDto uploadProfilePhoto(Integer userId, MultipartFile file) throws IOException {
        log.info("Uploading profile photo for user ID: {}", userId);

        // Validar que el archivo sea una imagen
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new StorageException("Only image files are allowed for profile photos.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Guardar el ID de la foto anterior para eliminarla después (si existe)
        Integer previousPhotoId = user.getPerson().getProfilePhotoId();

        // Subir el archivo primero
        UserFilesDto uploadedFile = fileUploadService.uploadFile(userId, file);

        // Actualizar la referencia de la foto de perfil
        user.getPerson().setProfilePhotoId(uploadedFile.getId());
        userRepository.save(user);

        // Eliminar la foto anterior después de que la nueva se haya guardado exitosamente
        if (previousPhotoId != null) {
            try {
                fileUploadService.deleteByFileId(previousPhotoId);
            } catch (Exception e) {
                log.warn("Could not delete previous photo file with ID: {}, error: {}", previousPhotoId, e.getMessage());
                // No fallar la operación completa si no se puede eliminar el archivo anterior
            }
        }

        return uploadedFile;
    }

    @Override
    @Transactional
    public UserFilesDto uploadPhoto(Integer userId, MultipartFile file) throws IOException {
        log.info("Uploading photo for user ID: {}", userId);

        // Validar que el archivo sea una imagen
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new StorageException("Only image files are allowed.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Validar límite de fotos según el tipo de usuario
        List<UserFilesDto> currentPhotos = fileUploadService.getAllByUser(userId);
        int maxPhotos = user.getType() == 1 ? 10 : 4; // 1 = TRAINER, 2 = CLIENT
        
        if (currentPhotos.size() >= maxPhotos) {
            String userTypeDesc = user.getType() == 1 ? "trainers" : "usuarios regulares";
            throw new StorageException(String.format("Has alcanzado el límite de %d fotos para %s", maxPhotos, userTypeDesc));
        }

        UserFilesDto uploadedFile = fileUploadService.uploadFile(userId, file);

        // Si no tiene foto de perfil, asignar esta como foto de perfil
        if (user.getPerson().getProfilePhotoId() == null) {
            user.getPerson().setProfilePhotoId(uploadedFile.getId());
            userRepository.save(user);
        }

        return uploadedFile;
    }

    @Override
    public List<UserFilesDto> getUserPhotos(Integer userId) {
        return fileUploadService.getAllByUser(userId);
    }

    @Override
    @Transactional
    public void deletePhoto(Integer userId, Integer photoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Verificar que la foto pertenece al usuario
        UserFiles photo = userFileRepository.findById(photoId)
                .orElseThrow(() -> new StorageException("Photo not found"));

        if (!photo.getUserId().equals(userId)) {
            throw new StorageException("Photo does not belong to user");
        }

        // Si es la foto de perfil, eliminar la referencia
        if (user.getPerson().getProfilePhotoId() != null && 
            user.getPerson().getProfilePhotoId().equals(photoId)) {
            user.getPerson().setProfilePhotoId(null);
            userRepository.save(user);
        }

        // Eliminar la foto
        fileUploadService.deleteByFileId(photoId);
    }

    @Override
    @Transactional
    public void setProfilePhoto(Integer userId, Integer photoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        // Opcional: verifica que la foto pertenezca al usuario
        user.getPerson().setProfilePhotoId(photoId);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserFilesDto uploadPresentationVideo(Integer userId, MultipartFile file) throws IOException {
        log.info("Uploading presentation video for user ID: {}", userId);

        // Validar que el archivo sea un video
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new StorageException("Only video files are allowed for presentation videos.");
        }

        // Validar tamaño del archivo (máximo 50MB)
        long maxSize = 50 * 1024 * 1024; // 50MB
        if (file.getSize() > maxSize) {
            throw new StorageException("Video file size must not exceed 50MB.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Verificar que el usuario sea trainer
        if (user.getType() != 1) {
            throw new StorageException("Only trainers can upload presentation videos.");
        }

        // Guardar el ID del video anterior para eliminarlo después (si existe)
        Integer previousVideoId = user.getPerson().getPresentationVideoId();

        // Subir el nuevo video primero
        UserFilesDto uploadedVideo = fileUploadService.uploadFile(userId, file);

        // Actualizar la referencia del video de presentación
        user.getPerson().setPresentationVideoId(uploadedVideo.getId());
        userRepository.save(user);

        // Eliminar el video anterior después de que el nuevo se haya guardado exitosamente
        if (previousVideoId != null) {
            try {
                fileUploadService.deleteByFileId(previousVideoId);
            } catch (Exception e) {
                log.warn("Could not delete previous video file with ID: {}, error: {}", previousVideoId, e.getMessage());
                // No fallar la operación completa si no se puede eliminar el archivo anterior
            }
        }

        return uploadedVideo;
    }

    @Override
    @Transactional
    public void deletePresentationVideo(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (user.getPerson().getPresentationVideoId() != null) {
            Integer videoId = user.getPerson().getPresentationVideoId();
            
            // Eliminar la referencia primero
            user.getPerson().setPresentationVideoId(null);
            userRepository.save(user);
            
            // Luego intentar eliminar el archivo
            try {
                fileUploadService.deleteByFileId(videoId);
            } catch (Exception e) {
                log.warn("Could not delete video file with ID: {}, error: {}", videoId, e.getMessage());
                // No fallar la operación si no se puede eliminar el archivo físico
                // La referencia ya se eliminó de la base de datos
            }
        }
    }

    @Override
    @Transactional
    public void changePassword(Integer userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validar que la nueva contraseña no esté vacía
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        // Validar que la nueva contraseña tenga al menos 6 caracteres
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long");
        }

        // Encriptar y guardar la nueva contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Password changed successfully for user ID: {}", userId);
    }
}

