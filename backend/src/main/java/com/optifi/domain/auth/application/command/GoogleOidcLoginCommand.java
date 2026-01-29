package com.optifi.domain.auth.application.command;

public record GoogleOidcLoginCommand(
        String idToken
) {
}
