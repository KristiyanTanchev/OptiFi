package com.optifi.domain.user.application;

import com.optifi.config.AppDefaults;
import com.optifi.domain.auth.application.command.RegisterUserCommand;
import com.optifi.domain.shared.Currency;
import com.optifi.domain.shared.SupportedLocale;
import com.optifi.domain.user.application.command.*;
import com.optifi.exceptions.*;
import com.optifi.domain.user.model.User;
import com.optifi.domain.user.repository.UserRepository;
import com.optifi.domain.shared.Role;
import com.optifi.domain.user.application.result.UserDetailsResult;
import com.optifi.domain.user.application.result.UserSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppDefaults appDefaults;

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryResult> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "username"))
                .stream()
                .map(UserSummaryResult::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailsResult getUser(Long userId) {
        return userRepository.findById(userId)
                .map(UserDetailsResult::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
    }

    @Override
    public User createUser(RegisterUserCommand cmd, Role role) {
        if (userRepository.existsByUsername(cmd.username())) {
            throw new DuplicateEntityException("User", "username", cmd.username());
        }
        if (userRepository.existsByEmail(cmd.email())) {
            throw new DuplicateEntityException("User", "email", cmd.email());
        }
        User user = User.builder()
                .username(cmd.username())
                .passwordHash(passwordEncoder.encode(cmd.password()))
                .email(cmd.email())
                .authProvider("LOCAL")
                .role(role)
                .baseCurrency(Currency.from(appDefaults.userCurrency()))
                .locale(SupportedLocale.from(appDefaults.userLocale()))
                .timeZoneId(appDefaults.userTimezone())
                .build();

        return userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordCommand cmd) {
        User user = userRepository.findById(cmd.userId())
                .orElseThrow(() -> new EntityNotFoundException("User", cmd.userId()));

        if (!passwordEncoder.matches(cmd.oldPassword(), user.getPasswordHash())) {
            throw new AuthorizationException("Old password is incorrect");
        }

        if (passwordEncoder.matches(cmd.newPassword(), user.getPasswordHash())) {
            throw new SamePasswordException();
        }

        user.setPasswordHash(passwordEncoder.encode(cmd.newPassword()));
    }

    @Override
    public void changeEmail(ChangeEmailCommand cmd) {
        User user = userRepository.findById(cmd.userId())
                .orElseThrow(() -> new EntityNotFoundException("User", cmd.userId()));

        if (Objects.equals(cmd.email(), user.getEmail())) {
            throw new SameEmailException();
        }

        if (userRepository.existsByEmail(cmd.email())) {
            throw new DuplicateEntityException("User", "email", cmd.email());
        }

        user.setEmail(cmd.email());
    }

    @Override
    public void deleteUser(Long userId, Long currentUserId) {
        validateCanDeleteUser(userId, currentUserId);
        validateUserExists(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public void setPreferences(SetUserPreferenceCommand cmd) {
        User user = userRepository.findById(cmd.userId())
                .orElseThrow(() -> new EntityNotFoundException("User", cmd.userId()));

        ZoneId zoneId;
        try {
            zoneId = ZoneId.of(cmd.timezone());
        } catch (DateTimeException e) {
            throw new InvalidTimeZoneException("Time zone not supported");
        }
        user.setBaseCurrency(cmd.baseCurrency());
        user.setLocale(cmd.locale());
        user.setTimeZoneId(zoneId.getId());
    }

    @Override
    public void changeUserRole(ChangeUserRoleCommand cmd) {
        User currentUser = userRepository.findById(cmd.currentUserId())
                .orElseThrow(() -> new EntityNotFoundException("User", cmd.currentUserId()));
        User targetUser = userRepository.findById(cmd.targetId())
                .orElseThrow(() -> new EntityNotFoundException("User", cmd.targetId()));

        validateRoleChange(targetUser, currentUser, cmd.action());
        switch (cmd.action()) {
            case PROMOTE_TO_ADMIN:
                targetUser.setRole(Role.ADMIN);
                break;
            case PROMOTE_TO_MODERATOR:
                targetUser.setRole(Role.MODERATOR);
                break;
            case DEMOTE_TO_USER:
                targetUser.setRole(Role.USER);
                break;
        }
    }

    @Override
    public void banUser(BanUserCommand cmd) {
        User currentUser = userRepository.findById(cmd.currentUserId())
                .orElseThrow(() -> new EntityNotFoundException("User", cmd.currentUserId()));
        User targetUser = userRepository.findById(cmd.targetId())
                .orElseThrow(() -> new EntityNotFoundException("User", cmd.targetId()));

        validateBanUser(currentUser, targetUser);
        targetUser.setRole(Role.BLOCKED);
    }

    @Override
    public void unbanUser(UnbanUserCommand cmd) {
        User currentUser = userRepository.findById(cmd.currentUserId())
                .orElseThrow(() -> new EntityNotFoundException("User", cmd.currentUserId()));
        User targetUser = userRepository.findById(cmd.targetId())
                .orElseThrow(() -> new EntityNotFoundException("User", cmd.targetId()));

        validateUnbanUser(currentUser, targetUser);
        targetUser.setRole(Role.USER);
    }

    @Override
    public User createGoogleUser(String email, String sub) {
        String username = email.substring(0, email.indexOf('@'));

        User user = User.builder()
                .username(username)
                .passwordHash(null)
                .authProvider("GOOGLE")
                .providerSubject(sub)
                .email(email)
                .role(Role.USER)
                .build();
        return userRepository.save(user);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", userId);
        }
    }

    private void validateCanDeleteUser(Long userId, Long currentUserId) {
        User requester = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User", currentUserId));

        boolean isSelf = userId.equals(requester.getId());
        boolean isAdmin = requester.getRole() == Role.ADMIN;

        if (!isSelf && !isAdmin) {
            throw new AuthorizationException("You can not allowed to delete this user");
        }

        if (isSelf && isAdmin && userRepository.countByRole(Role.ADMIN) == 1) {
            throw new AuthorizationException("You can not delete last admin");
        }
    }

    private void validateRoleChange(User targetUser, User currentUser, RoleChangeAction action) {
        if (!currentUser.isAdmin()) {
            throw new AuthorizationException("Only admins can change user roles");
        }
        if (currentUser.equals(targetUser)) {
            throw new AuthorizationException("Cannot change role of self");
        }
        if (targetUser.getRole() == Role.BLOCKED) {
            throw new IllegalStateTransitionException("User is blocked");
        }
        if (action == RoleChangeAction.PROMOTE_TO_ADMIN && targetUser.getRole() == Role.ADMIN) {
            throw new IllegalStateTransitionException("User is already admin");
        }
        if (action == RoleChangeAction.PROMOTE_TO_MODERATOR &&
                (targetUser.getRole() == Role.MODERATOR || targetUser.getRole() == Role.ADMIN)) {
            throw new IllegalStateTransitionException("User is already moderator or admin");
        }
        if (action == RoleChangeAction.DEMOTE_TO_USER && targetUser.getRole() != Role.ADMIN
                && targetUser.getRole() != Role.MODERATOR) {
            throw new IllegalStateTransitionException("User is not admin or moderator");
        }
    }

    private void validateBanUser(User currentUser, User targetUser) {
        if (currentUser.equals(targetUser)) {
            throw new AuthorizationException("Cannot ban self");
        }
        if (!currentUser.isModeratorOrAdmin()) {
            throw new AuthorizationException("Only moderators and admins can ban users");
        }
        if (targetUser.getRole() == Role.BLOCKED) {
            throw new IllegalStateTransitionException("User is already banned");
        }
        if (targetUser.isModeratorOrAdmin()) {
            throw new IllegalStateTransitionException("Cannot ban moderators or admins");
        }
    }

    private void validateUnbanUser(User currentUser, User targetUser) {
        if (currentUser.equals(targetUser)) {
            throw new AuthorizationException("Cannot unban self");
        }
        if (!currentUser.isModeratorOrAdmin()) {
            throw new AuthorizationException("Only moderators and admins can unban users");
        }
        if (targetUser.getRole() != Role.BLOCKED) {
            throw new IllegalStateTransitionException("User is not banned");
        }
    }
}
