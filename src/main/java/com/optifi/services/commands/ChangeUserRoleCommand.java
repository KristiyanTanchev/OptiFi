package com.optifi.services.commands;

public record ChangeUserRoleCommand(Long targetId, Long currentUserId, RoleChangeAction action) {
}
