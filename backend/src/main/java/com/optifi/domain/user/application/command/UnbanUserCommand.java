package com.optifi.domain.user.application.command;

import lombok.Builder;

@Builder
public record UnbanUserCommand(Long targetId, Long currentUserId) {
}
