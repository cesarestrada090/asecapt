package com.fitech.app.users.domain.services.impl;

import com.fitech.app.commons.util.MapperUtil;
import com.fitech.app.commons.util.PaginationUtil;
import com.fitech.app.users.application.dto.LoginResponseDto;
import com.fitech.app.users.domain.entities.Person;
import com.fitech.app.users.application.dto.PersonDto;
import com.fitech.app.users.application.dto.UserLoginRequest;
import com.fitech.app.users.domain.services.PersonService;
import com.fitech.app.users.domain.services.UserService;
import com.fitech.app.users.domain.entities.User;
import com.fitech.app.users.infrastructure.repository.UserRepository;
import com.fitech.app.users.application.dto.ResultPage;
import com.fitech.app.users.application.exception.InvalidPasswordException;
import com.fitech.app.users.application.exception.UserNotFoundException;
import com.fitech.app.users.application.exception.DuplicatedUserException;
import com.fitech.app.users.application.dto.UserDto;
import com.fitech.app.users.application.dto.UserResponseDto;
import com.fitech.app.users.infrastructure.security.JwtTokenProvider;
import com.fitech.app.users.infrastructure.security.PasswordEncoderUtil;
import com.fitech.app.users.infrastructure.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fitech.app.users.application.exception.EmailNotVerifiedException;
import com.fitech.app.users.application.dto.PremiumBy;
import com.fitech.app.memberships.application.services.MembershipService;
import com.fitech.app.memberships.domain.entities.UserMembership;
import com.fitech.app.memberships.domain.entities.MembershipPlan;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PersonService personService;
    private final PasswordEncoderUtil passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final MembershipService membershipService;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PersonService personService,
                           PasswordEncoderUtil passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           EmailService emailService,
                           MembershipService membershipService) {
        this.userRepository = userRepository;
        this.personService = personService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
        this.membershipService = membershipService;
    }

    @Override
    @Transactional
    public UserDto save(UserDto userDto) {
        validateUserCreation(userDto);
        Person person = personService.save(userDto.getPerson());
        
        // Create and configure the User entity
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setType(userDto.getType());
        user.setPerson(person);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        generateVerificationToken(user);
        
        User savedUser = userRepository.save(user);

        // Enviar email de verificación en un hilo separado
        try {
            emailService.sendVerificationEmail(person.getEmail(), user.getEmailVerificationToken());
        } catch (IOException e) {
            // Log the error but don't fail the registration
            // The user can request a new verification email later
            log.error("Error sending verification email to " + person.getEmail(), e);
        }

        return MapperUtil.map(savedUser, UserDto.class);
    }

    private void validateUserCreation(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new DuplicatedUserException("Username already exists: " + userDto.getUsername());
        }
    }

    private void generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        user.setEmailTokenExpiresAt(LocalDateTime.now().plusHours(24));
        user.setIsEmailVerified(false);
    }

    @Override
    @Transactional
    public UserDto update(Integer id, UserDto userDto) {
        validateUserUpdate(id, userDto);
        User user = updateUserEntity(id, userDto);
        User savedUser = userRepository.save(user);
        return MapperUtil.map(savedUser, UserDto.class);
    }

    private void validateUserUpdate(Integer id, UserDto userDto) {
        User existingUser = getUserEntityById(id);
        
        if (userDto.hasDifferentUserName(existingUser.getUsername())) {
            if (usernameAlreadyExistsByOrgId(userDto.getUsername(), null)) {
                throw new DuplicatedUserException("Username already exists: " + userDto.getUsername());
            }
        }
    }

    private User updateUserEntity(Integer id, UserDto userDto) {
        // Get the existing user entity
        User existingUser = getUserEntityById(id);
        
        // Update the Person entity if needed
        if (userDto.getPerson() != null) {
            PersonDto updatedPerson = personService.update(existingUser.getPerson().getId(), userDto.getPerson());
            existingUser.setPerson(MapperUtil.map(updatedPerson, Person.class));
        }
        
        // Map the DTO to the existing entity
        MapperUtil.map(userDto, existingUser);
        existingUser.setId(id);  // Ensure ID is preserved
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        return existingUser;
    }

    @Override
    public UserResponseDto getByUsernameAndPassword(UserLoginRequest loginRequest) {
        Optional<User> user = userRepository.
                findByUsernameAndPassword(loginRequest.getUsername(), loginRequest.getPassword());
        if(user.isEmpty()) {
            throw new InvalidPasswordException("Invalid credentials for username: " + loginRequest.getUsername());
        }
        return MapperUtil.map(user.get(), UserResponseDto.class);
    }

    @Override
    public Boolean usernameAlreadyExistsByOrgId(String username, Integer orgId) {
        Optional<User> user = userRepository.
                findByUsername(username);
        return user.isPresent();
    }

    @Override
    public UserResponseDto getById(Integer id){
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new UserNotFoundException("User not found with ID: " + id);
        }
        return MapperUtil.map(user.get(), UserResponseDto.class);
    }

    public User getUserEntityById(Integer usedId) {
        Optional<User> existingUser = userRepository.findById(usedId);
        return existingUser.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + usedId));
    }

    @Override
    public User getUserEntityByUsername(String username) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        return existingUser.orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }
    
    @Override
    public ResultPage<UserResponseDto> getAll(Pageable paging){
        Page<User> users = userRepository.findAll(paging);
        if(users.isEmpty()){
            throw new UserNotFoundException("No users found");
        }
        return PaginationUtil.prepareResultWrapper(users, UserResponseDto.class);
    }

    @Override
    @Transactional
    public UserResponseDto verifyEmail(String token) throws Exception {
        User user = userRepository.findByEmailVerificationToken(token)
            .orElseThrow(() -> new Exception("Token de verificación inválido"));

        if (user.getEmailTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new Exception("Token de verificación expirado");
        }

        user.setIsEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailTokenExpiresAt(null);
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        return MapperUtil.map(updatedUser, UserResponseDto.class);
    }

    @Override
    public LoginResponseDto login(String username, String password) {
        if (username == null || password == null) {
            throw new InvalidPasswordException("Usuario y contraseña son requeridos");
        }

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new InvalidPasswordException("Usuario o contraseña incorrectos"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidPasswordException("Usuario o contraseña incorrectos");
        }

        if (!user.isActive()) {
            throw new InvalidPasswordException("Tu cuenta está deshabilitada. Contacta al administrador.");
        }

        if (!user.getIsEmailVerified()) {
            throw new EmailNotVerifiedException("Tu cuenta no está verificada. Por favor revisa tu correo electrónico para verificar tu cuenta antes de iniciar sesión");
        }

        String token = jwtTokenProvider.generateToken(username);
        
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setUser(MapperUtil.map(user, UserResponseDto.class));
        
        return response;
    }

    @Override
    public Boolean emailAlreadyExists(String email) {
        return userRepository.existsByPersonEmail(email);
    }

    @Override
    @Transactional
    public UserResponseDto upgradeToPremium(Integer userId, String planType) {
        User user = getUserEntityById(userId);
        
        try {
            UserMembership membership;
            
            // Verificar si el usuario ya tiene una membresía activa
            if (user.isPremium() && membershipService.hasActiveMembership(userId)) {
                throw new RuntimeException("El usuario ya tiene una membresía Premium activa");
            }
            
            // Verificar si tiene una membresía expirada que se puede reactivar
            Optional<UserMembership> latestMembership = membershipService.getLatestMembership(userId);
            
            if (latestMembership.isPresent() && 
                latestMembership.get().getMembershipType() == UserMembership.MembershipType.PAYMENT &&
                (latestMembership.get().getStatus() == UserMembership.MembershipStatus.EXPIRED ||
                 latestMembership.get().getStatus() == UserMembership.MembershipStatus.CANCELLED)) {
                
                // Reactivar membresía existente
                log.info("Reactivating expired membership for user: {}", userId);
                membership = membershipService.reactivateExpiredMembership(userId, "STRIPE");
                
            } else {
                // Crear nueva membresía
                log.info("Creating new membership for user: {}", userId);
                
                // Buscar el plan por nombre (planType viene del frontend)
                Long planId = getPlanIdByType(planType);
                
                // Obtener detalles del plan
                MembershipPlan plan = membershipService.getPlanById(planId)
                    .orElseThrow(() -> new RuntimeException("Plan no encontrado: " + planType));
                
                // Crear la membresía de pago
                membership = membershipService.createPaymentMembership(
                    userId, 
                    planId, 
                    plan.getPrice(), 
                    "STRIPE" // Método de pago por defecto
                );
            }
            
            // Actualizar el estado premium del usuario
            user.setPremium(true);
            user.setPremiumBy(PremiumBy.PAYMENT);
            user.setUpdatedAt(LocalDateTime.now());
            
            User updatedUser = userRepository.save(user);
            
            log.info("User {} upgraded/reactivated premium with plan: {} (membership ID: {})", 
                    userId, planType, membership.getId());
            
            return MapperUtil.map(updatedUser, UserResponseDto.class);
            
        } catch (Exception e) {
            log.error("Error upgrading user {} to premium: {}", userId, e.getMessage());
            throw new RuntimeException("Error al procesar la actualización a Premium: " + e.getMessage());
        }
    }
    
    private Long getPlanIdByType(String planType) {
        // Buscar el plan por tipo de facturación en lugar de usar IDs hardcodeados
        try {
            switch (planType.toLowerCase()) {
                case "monthly":
                case "mensual":
                    return membershipService.getMonthlyPlans().stream()
                            .findFirst()
                            .map(plan -> plan.getId())
                            .orElseThrow(() -> new RuntimeException("Plan mensual no encontrado"));
                case "annual":
                case "anual":
                    return membershipService.getAnnualPlans().stream()
                            .findFirst()
                            .map(plan -> plan.getId())
                            .orElseThrow(() -> new RuntimeException("Plan anual no encontrado"));
                default:
                    throw new RuntimeException("Tipo de plan no válido: " + planType);
            }
        } catch (Exception e) {
            log.error("Error finding plan by type {}: {}", planType, e.getMessage());
            // Fallback a IDs por defecto si falla la búsqueda dinámica
            switch (planType.toLowerCase()) {
                case "monthly":
                case "mensual":
                    return 1L;
                case "annual":
                case "anual":
                    return 2L;
                default:
                    throw new RuntimeException("Tipo de plan no válido: " + planType);
            }
        }
    }

    @Override
    @Transactional
    public void saveUserEntity(User user) {
        userRepository.save(user);
    }
}
