package com.optifi.domain.auth.api;

import com.optifi.config.FeatureProperties;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {
    private final AuthService authService;
    private final FeatureProperties features;

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
        LoginResponseDto response = LoginResponseDto.fromResult(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginCommand cmd = new LoginCommand(loginRequest.username(), loginRequest.password());
        LoginResult result = authService.login(cmd);
        LoginResponseDto response = LoginResponseDto.fromResult(result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/oidc/google")
    public ResponseEntity<LoginResponseDto> loginWithGoogle(@Valid @RequestBody GoogleOidcLoginRequestDto req) {
        LoginResult result = authService.loginWithGoogle(new GoogleOidcLoginCommand(req.idToken()));
        return ResponseEntity.ok(LoginResponseDto.fromResult(result));
    }
}


