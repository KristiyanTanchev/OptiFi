package com.optifi.services;

import com.optifi.models.Role;
import com.optifi.models.User;
import com.optifi.services.commands.ChangeEmailCommand;
import com.optifi.services.commands.ChangePasswordCommand;
import com.optifi.services.commands.RegisterUserCommand;
import com.optifi.services.results.GetUserResult;

import java.util.List;

public interface UserService {
    GetUserResult getUser(Long userId);

    List<GetUserResult> getAllUsers();

    User createUser(RegisterUserCommand cmd, Role role);

    void changePassword(ChangePasswordCommand cmd);

    void changeEmail(ChangeEmailCommand cmd);

    void deleteUser(Long userId, Long currentUserId);
}
