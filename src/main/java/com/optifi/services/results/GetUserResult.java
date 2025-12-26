package com.optifi.services.results;

import com.optifi.models.Role;
import com.optifi.models.User;

public record GetUserResult(long id, String username, Role role) {
    public static GetUserResult fromEntity(User user) {
        return new GetUserResult(user.getId(), user.getUsername(), user.getRole());
    }
}
