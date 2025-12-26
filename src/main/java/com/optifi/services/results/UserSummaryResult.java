package com.optifi.services.results;

import com.optifi.models.Role;
import com.optifi.models.User;

public record UserSummaryResult(long id, String username, Role role) {
    public static UserSummaryResult fromEntity(User user) {
        return new UserSummaryResult(user.getId(), user.getUsername(), user.getRole());
    }
}
