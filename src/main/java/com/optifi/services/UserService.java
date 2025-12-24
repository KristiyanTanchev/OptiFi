package com.optifi.services;

import com.optifi.models.Role;
import com.optifi.models.User;
import com.optifi.services.commands.RegisterUserCommand;

public interface UserService {

    User createUser(RegisterUserCommand cmd, Role role);
}
