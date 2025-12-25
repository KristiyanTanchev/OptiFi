package com.optifi.services.commands;

public record ChangePasswordCommand(Long userId, String oldPassword, String newPassword) {
}
