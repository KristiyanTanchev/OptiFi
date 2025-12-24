package com.optifi.services;

import com.optifi.exceptions.DuplicateEntityException;
import com.optifi.models.Role;
import com.optifi.models.User;
import com.optifi.repositories.UserRepository;
import com.optifi.services.commands.RegisterUserCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void createUser_Should_throwDuplicateEntityException_When_usernameExists() {
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(DuplicateEntityException.class,
                () -> userService.createUser(
                        new RegisterUserCommand("john", "pass", ""), Role.USER)
        );

        verify(userRepository).existsByUsername("john");
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_Should_saveUserWithGivenFields_When_usernameDoesNotExist() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded-pass");

        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.createUser(
                new RegisterUserCommand("john", "pass", ""),
                Role.ADMIN
        );

        assertEquals("john", saved.getUsername());
        assertEquals("encoded-pass", saved.getPasswordHash());
        assertEquals(Role.ADMIN, saved.getRole());

        verify(passwordEncoder).encode("pass");
        verify(userRepository).save(any(User.class));
    }
}
