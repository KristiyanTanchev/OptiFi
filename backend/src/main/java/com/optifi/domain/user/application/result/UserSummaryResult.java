package com.optifi.domain.user.application.result;

import com.optifi.domain.shared.Role;
import com.optifi.domain.user.model.User;

public record UserSummaryResult(
        long id,
        String username,
        Role role
) {

    public static UserSummaryResult fromEntity(User user) {
        return new UserSummaryResult(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }
}
