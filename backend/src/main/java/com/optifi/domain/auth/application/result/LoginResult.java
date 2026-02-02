package com.optifi.domain.auth.application.result;

import com.optifi.domain.shared.Role;

public record LoginResult(
        Long id,
        String username,
        Role role,
        String token
) {
}
