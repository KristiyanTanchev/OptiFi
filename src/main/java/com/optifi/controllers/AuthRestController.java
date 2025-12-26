package com.optifi.controllers;

import com.optifi.dto.loginDtos.LoginRequestDto;
import com.optifi.dto.loginDtos.LoginResponseDto;
import com.optifi.dto.registerDtos.RegisterRequestDto;
import com.optifi.services.AuthService;
import com.optifi.services.commands.LoginCommand;
import com.optifi.services.commands.RegisterUserCommand;
import com.optifi.services.results.LoginResult;
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

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        RegisterUserCommand cmd = registerRequestDto.toCommand();
        LoginResult result = authService.register(cmd);
        LoginResponseDto response = LoginResponseDto.fromResult(result);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginCommand cmd = loginRequest.toCommand();
        LoginResult result = authService.login(cmd);
        LoginResponseDto response = LoginResponseDto.fromResult(result);
        return ResponseEntity.ok(response);
    }
}


