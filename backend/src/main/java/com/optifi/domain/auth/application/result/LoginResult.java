package com.optifi.domain.auth.application.result;

public record LoginResult(Long id, String username, String role, String token) {
}
