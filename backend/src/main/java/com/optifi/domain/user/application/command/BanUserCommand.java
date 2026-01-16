package com.optifi.domain.user.application.command;

public record BanUserCommand(Long targetId, Long currentUserId) {
}
