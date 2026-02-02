package com.optifi.domain.auth.api.response;

import com.optifi.domain.auth.application.result.LoginResult;
import com.optifi.domain.shared.Role;

public record LoginResponseDto(
        String token,
        String type,
        Long id,
        String username,
        Role role) {
    private static final String TOKEN_TYPE = "Bearer";

    public static LoginResponseDto fromResult(LoginResult result) {
        return new LoginResponseDto(result.token(), TOKEN_TYPE, result.id(), result.username(), result.role());
    }
}