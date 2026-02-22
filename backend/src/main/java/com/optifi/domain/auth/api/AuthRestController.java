package com.optifi.domain.auth.api;

import com.optifi.config.FeatureProperties;
import com.optifi.config.openApi.ApiConflict;
import com.optifi.config.openApi.ApiForbidden;
import com.optifi.config.openApi.ApiUnauthorized;
import com.optifi.config.openApi.ApiValidationError;
import com.optifi.domain.auth.api.mapper.AuthMapper;
import com.optifi.domain.auth.api.request.GoogleOidcLoginRequestDto;
import com.optifi.domain.auth.api.request.LoginRequestDto;
import com.optifi.domain.auth.api.response.LoginResponseDto;
import com.optifi.domain.auth.api.request.RegisterRequestDto;
import com.optifi.domain.auth.application.AuthService;
import com.optifi.domain.auth.application.command.GoogleOidcLoginCommand;
import com.optifi.domain.auth.application.command.LoginCommand;
import com.optifi.domain.auth.application.command.RegisterUserCommand;
import com.optifi.domain.auth.application.result.LoginResult;
import com.optifi.exceptions.AuthorizationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth")
@SecurityRequirements
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {
    private final AuthService authService;
    private final AuthMapper mapper;
    private final FeatureProperties features;

    @Operation(summary = "Register", description = "Creates a new user account and returns a JWT.")
    @ApiResponse(responseCode = "201", description = "User registered")
    @ApiValidationError
    @ApiForbidden
    @ApiConflict(description = "Username already exists")
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        if (!features.registrationEnabled()) {
            throw new AuthorizationException("Registration is disabled on this deployment.");
        }
        RegisterUserCommand cmd = new RegisterUserCommand(
                registerRequestDto.username(),
                registerRequestDto.password(),
                registerRequestDto.email());
        LoginResult result = authService.register(cmd);
        LoginResponseDto response = mapper.toLoginResponseDto(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT.")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiValidationError
    @ApiUnauthorized
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginCommand cmd = new LoginCommand(loginRequest.username(), loginRequest.password());
        LoginResult result = authService.login(cmd);
        LoginResponseDto response = mapper.toLoginResponseDto(result);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Login with Google", description = "Authenticates using a Google OIDC ID token, returns JWT.")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiValidationError
    @ApiUnauthorized
    @PostMapping("/oidc/google")
    public ResponseEntity<LoginResponseDto> loginWithGoogle(@Valid @RequestBody GoogleOidcLoginRequestDto req) {
        LoginResult result = authService.loginWithGoogle(new GoogleOidcLoginCommand(req.idToken()));
        LoginResponseDto response = mapper.toLoginResponseDto(result);
        return ResponseEntity.ok(response);
    }
}
