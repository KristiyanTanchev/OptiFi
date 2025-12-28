package com.optifi.domain.user.application.command;

public record ChangePasswordCommand(Long userId, String oldPassword, String newPassword) {
}
