package com.optifi.dto.registerDtos;

import com.optifi.services.results.RegisterUserResult;

public record RegisterResponseDto(
        Long id,
        String username,
        String role) {

    public static RegisterResponseDto fromResult(RegisterUserResult result) {
        return new RegisterResponseDto(result.id(), result.username(), result.role().name());
    }
}
