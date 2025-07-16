package com.fitech.app.users.application.controllers;

import com.fitech.app.commons.application.controllers.BaseController;
import com.fitech.app.users.application.dto.LoginRequestDto;
import com.fitech.app.users.application.dto.LoginResponseDto;
import com.fitech.app.users.application.exception.UserNotFoundException;
import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.application.dto.UserDto;
import com.fitech.app.users.application.dto.UserResponseDto;
import com.fitech.app.users.domain.entities.User;
import com.fitech.app.users.domain.services.UserService;
import com.fitech.app.users.infrastructure.email.EmailService;
import com.fitech.app.users.infrastructure.security.PasswordEncoderUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.logging.Logger;

@RequestMapping("v1/app/user")
@RestController
@Tag(name = "User Management", description = "User account management and operations")
@SecurityRequirement(name = "bearerAuth")
public class UserController extends BaseController {
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoderUtil passwordEncoder;
    private static final Logger log = Logger.getLogger(UserController.class.getName());
    
    @Autowired
    public UserController(UserService userService, EmailService emailService, PasswordEncoderUtil passwordEncoder) {
        this.userService = userService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            LoginResponseDto userResponseDto = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(userResponseDto);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping(value="/{id}", produces = "application/json")
    public ResponseEntity<UserResponseDto> getById(@PathVariable(value = "id") Integer id){
        UserResponseDto userDto = userService.getById(id);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> save(@Valid @RequestBody UserDto userDto){
        userDto = userService.save(userDto);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PutMapping(value="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> update(@PathVariable(value = "id") int id, @Valid @RequestBody UserDto userDto){
        userDto = userService.update(id, userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Map<String,Object>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size){
        Pageable paging = PageRequest.of(page-1, size);
        ResultPage<UserResponseDto> resultPageWrapper = userService.getAll(paging);
        Map<String, Object> response = prepareResponse(resultPageWrapper);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            UserResponseDto user = userService.verifyEmail(token);
            return ResponseEntity.ok(Map.of(
                "message", "Email verificado exitosamente",
                "user", user
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/test-email")
    public ResponseEntity<?> testEmail(@RequestParam String email) {
        try {
            emailService.sendTestEmail(email);
            return ResponseEntity.ok(Map.of(
                "message", "Email de prueba enviado exitosamente a " + email
            ));
        } catch (Exception e) {
            log.severe("Error al enviar email de prueba: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al enviar el email: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}/upgrade-premium")
    public ResponseEntity<?> upgradeToPremium(@PathVariable("id") Integer userId, @RequestBody Map<String, String> body) {
        try {
            String planType = body.get("planType");
            
            UserResponseDto updatedUser = userService.upgradeToPremium(userId, planType);
            return ResponseEntity.ok(Map.of(
                "message", "Usuario actualizado a Premium exitosamente",
                "user", updatedUser
            ));
        } catch (Exception e) {
            log.severe("Error al actualizar usuario a premium: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al actualizar a premium: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateActiveStatus(@PathVariable("id") Integer id, @RequestBody Map<String, Boolean> body) {
        Boolean active = body.get("active");
        if (active == null) {
            return ResponseEntity.badRequest().build();
        }
        User userEntity = userService.getUserEntityById(id);
        UserDto userDto = com.fitech.app.commons.util.MapperUtil.map(userEntity, UserDto.class);
        userDto.setActive(active);
        userService.update(id, userDto);
        UserResponseDto response = userService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resetPassword(@PathVariable("id") Integer id) {
        User user = userService.getUserEntityById(id);
        String encoded = passwordEncoder.encode("welcome");
        user.setPassword(encoded);
        user.setUpdatedAt(LocalDateTime.now());
        userService.saveUserEntity(user);
        return ResponseEntity.ok(Map.of("message", "Contraseña reseteada a 'welcome'"));
    }

    @PutMapping("/{id}/force-verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> forceVerifyEmail(@PathVariable("id") Integer id) {
        User user = userService.getUserEntityById(id);
        try {
            if (!user.getIsEmailVerified()) {
                user.setIsEmailVerified(true);
                user.setUpdatedAt(java.time.LocalDateTime.now());
                userService.saveUserEntity(user);
                // Enviar email de verificación
                emailService.sendVerificationEmail(user.getPerson().getEmail(), "Tu cuenta ha sido verificada manualmente por un administrador. ¡Bienvenido!");
            } else {
                // Usuario ya verificado, enviar email informativo
                emailService.sendVerificationEmail(user.getPerson().getEmail(), "Tu cuenta ya está verificada. ¡Bienvenido nuevamente!");
            }
            return ResponseEntity.ok(Map.of("message", "Email de verificación enviado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "No se pudo enviar el email de verificación: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/resend-verification")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resendVerificationEmail(@PathVariable("id") Integer id) {
        User user = userService.getUserEntityById(id);
        if (user.getIsEmailVerified()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El usuario ya está verificado"));
        }
        try {
            emailService.sendVerificationEmail(user.getPerson().getEmail(), "Por favor confirma tu cuenta haciendo clic en el enlace de verificación.");
            return ResponseEntity.ok(Map.of("message", "Email de verificación reenviado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "No se pudo enviar el email de verificación: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/unverify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unverifyUser(@PathVariable("id") Integer id) {
        User user = userService.getUserEntityById(id);
        if (!user.getIsEmailVerified()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El usuario ya está no verificado"));
        }
        user.setIsEmailVerified(false);
        user.setUpdatedAt(java.time.LocalDateTime.now());
        userService.saveUserEntity(user);
        return ResponseEntity.ok(Map.of("message", "Usuario desverificado correctamente"));
    }

    @Override
    protected String getResource() {
        return "users";
    }
}
