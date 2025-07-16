package com.fitech.app.users.application.controllers;

import com.fitech.app.users.application.dto.LoginRequestDto;
import com.fitech.app.users.application.dto.LoginResponseDto;
import com.fitech.app.users.domain.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/app/auth")
@Tag(name = "Authentication", description = "Authentication and authorization operations")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
        summary = "User login",
        description = "Authenticate user with username and password. Returns JWT token for subsequent API calls."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Invalid request format",
            content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto response = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Check username and email availability",
        description = "Check if username and email are already taken in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Availability check completed",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/check-availability")
    public ResponseEntity<Map<String, Boolean>> checkAvailability(
            @Parameter(description = "Username to check", required = true) @RequestParam String username,
            @Parameter(description = "Email to check", required = true) @RequestParam String email) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("usernameExists", userService.usernameAlreadyExistsByOrgId(username, null));
        response.put("emailExists", userService.emailAlreadyExists(email));
        return ResponseEntity.ok(response);
    }
} 