package com.optifi.domain.user.application.command;

public record ChangeUserRoleCommand(Long targetId, Long currentUserId, RoleChangeAction action) {
}
