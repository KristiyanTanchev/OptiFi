package com.optifi.services.commands;

public record UnbanUserCommand(Long targetId, Long currentUserId) {
}
