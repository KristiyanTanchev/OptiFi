package com.optifi.services;

import com.optifi.exceptions.DuplicateEntityException;
import com.optifi.models.User;
import com.optifi.repositories.UserRepository;
import com.optifi.models.Role;
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
    public User createUser(String username, String password, Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateEntityException("User", "username", username);
        }
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        return userRepository.save(user);
    }
}
