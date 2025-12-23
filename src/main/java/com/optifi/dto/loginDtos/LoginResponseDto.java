package com.optifi.dto.loginDtos;

import com.optifi.services.results.LoginResult;

public record LoginResponseDto(
        String token,
        String type,
        Long id,
        String username,
        String role) {
    private static final String TOKEN_TYPE = "Bearer";

    public static LoginResponseDto fromResult(LoginResult result) {
        return new LoginResponseDto(result.token(), TOKEN_TYPE, result.id(), result.username(), result.role().name());
    }
}