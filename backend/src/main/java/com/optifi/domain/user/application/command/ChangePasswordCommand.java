package com.optifi.domain.user.application.command;

import lombok.Builder;

@Builder
public record ChangePasswordCommand(Long userId, String oldPassword, String newPassword) {
}
