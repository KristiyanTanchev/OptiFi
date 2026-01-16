package com.optifi.domain.user.application.command;

public record UnbanUserCommand(Long targetId, Long currentUserId) {
}
