package com.optifi.services.commands;

public record BanUserCommand(Long targetId, Long currentUserId) {
}
