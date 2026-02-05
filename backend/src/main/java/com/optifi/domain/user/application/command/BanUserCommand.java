package com.optifi.domain.user.application.command;

import lombok.Builder;

@Builder
public record BanUserCommand(Long targetId, Long currentUserId) {
}
