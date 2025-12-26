package com.optifi.services;

import com.optifi.exceptions.*;
import com.optifi.models.User;
import com.optifi.repositories.UserRepository;
import com.optifi.models.Role;
import com.optifi.services.commands.ChangeEmailCommand;
import com.optifi.services.commands.ChangePasswordCommand;
import com.optifi.services.commands.RegisterUserCommand;
import com.optifi.services.results.UserDetailsResult;
import com.optifi.services.results.UserSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        User user = User.builder()
                .username(cmd.username())
                .passwordHash(passwordEncoder.encode(cmd.password()))
                .email(cmd.email())
                .role(role)
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
}
