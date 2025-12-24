package com.optifi.services;

import com.optifi.exceptions.DuplicateEntityException;
import com.optifi.models.User;
import com.optifi.repositories.UserRepository;
import com.optifi.models.Role;
import com.optifi.services.commands.RegisterUserCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
