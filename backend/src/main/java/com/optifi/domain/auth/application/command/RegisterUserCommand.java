package com.optifi.domain.auth.application.command;

public record RegisterUserCommand(String username, String password, String email) {
}