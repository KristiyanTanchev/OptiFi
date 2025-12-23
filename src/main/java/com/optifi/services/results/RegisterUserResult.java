package com.optifi.services.results;

import com.optifi.models.Role;
import com.optifi.models.User;

public record RegisterUserResult (Long id, String username, Role role){

    public static RegisterUserResult fromEntity(User user) {
        return new RegisterUserResult(user.getId(), user.getUsername(), user.getRole());
    }
}
