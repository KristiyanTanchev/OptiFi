package com.optifi.services;

import com.optifi.models.Role;
import com.optifi.models.User;
import com.optifi.services.commands.*;
import com.optifi.services.results.UserDetailsResult;
import com.optifi.services.results.UserSummaryResult;

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
