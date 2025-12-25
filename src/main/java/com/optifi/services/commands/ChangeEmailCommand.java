package com.optifi.services.commands;

public record ChangeEmailCommand(Long userId, String email) {
}
