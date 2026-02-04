package com.optifi.domain.auth.api.response;

import com.optifi.domain.shared.Role;
import lombok.Builder;

@Builder
public record LoginResponseDto(
        String token,
        String type,
        Long id,
        String username,
        Role role
) {
}