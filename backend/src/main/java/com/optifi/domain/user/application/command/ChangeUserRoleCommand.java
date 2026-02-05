package com.optifi.domain.user.application.command;

import lombok.Builder;

@Builder
public record ChangeUserRoleCommand(Long targetId, Long currentUserId, RoleChangeAction action) {
}
