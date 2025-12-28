package com.optifi.domain.user.application.command;

public record ChangeEmailCommand(Long userId, String email) {
}
