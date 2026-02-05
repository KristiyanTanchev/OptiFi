package com.optifi.domain.user.application.command;

import lombok.Builder;

@Builder
public record ChangeEmailCommand(Long userId, String email) {
}
