package com.optifi.services;

import com.optifi.exceptions.DuplicateEntityException;
import com.optifi.exceptions.EntityNotFoundException;
import com.optifi.exceptions.SameEmailException;
import com.optifi.exceptions.SamePasswordException;
import com.optifi.models.User;
import com.optifi.repositories.UserRepository;
import com.optifi.models.Role;
import com.optifi.services.commands.ChangeEmailCommand;
import com.optifi.services.commands.ChangePasswordCommand;
import com.optifi.services.commands.RegisterUserCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

        if (passwordEncoder.matches(cmd.oldPassword(), user.getPasswordHash())) {
            throw new SamePasswordException();
        }

        user.setPasswordHash(passwordEncoder.encode(cmd.newPassword()));
        userRepository.save(user);
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
        userRepository.save(user);
    }
}
