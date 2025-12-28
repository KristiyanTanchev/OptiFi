package com.optifi.domain.user.application;

import com.optifi.domain.auth.application.command.RegisterUserCommand;
import com.optifi.domain.user.application.command.*;
import com.optifi.domain.user.model.Role;
import com.optifi.domain.user.model.User;
import com.optifi.domain.user.application.result.UserDetailsResult;
import com.optifi.domain.user.application.result.UserSummaryResult;

import java.util.List;

public interface UserService {
    UserDetailsResult getUser(Long userId);

    List<UserSummaryResult> getAllUsers();

    User createUser(RegisterUserCommand cmd, Role role);

    void changePassword(ChangePasswordCommand cmd);

    void changeEmail(ChangeEmailCommand cmd);

    void deleteUser(Long userId, Long currentUserId);

    void setPreferences(SetUserPreferenceCommand cmd, Long userId);

    void changeUserRole(ChangeUserRoleCommand cmd);

    void banUser(BanUserCommand cmd);

    void unbanUser(UnbanUserCommand cmd);
}
