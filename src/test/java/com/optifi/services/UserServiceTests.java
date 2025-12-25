package com.optifi.services;

import com.optifi.exceptions.DuplicateEntityException;
import com.optifi.exceptions.EntityNotFoundException;
import com.optifi.exceptions.SameEmailException;
import com.optifi.exceptions.SamePasswordException;
import com.optifi.models.Role;
import com.optifi.models.User;
import com.optifi.repositories.UserRepository;
import com.optifi.services.commands.ChangeEmailCommand;
import com.optifi.services.commands.ChangePasswordCommand;
import com.optifi.services.commands.RegisterUserCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

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

    @Test
    void changePassword_Should_throwEntityNotFoundException_When_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.changePassword(
                        new ChangePasswordCommand(1L, "old", "new"))
        );
    }

    @Test
    void changePassword_Should_throwSamePasswordException_When_oldPasswordMatchesNewPassword() {
        User user = User.builder()
                .id(1L)
                .passwordHash("encoded-pass")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", user.getPasswordHash())).thenReturn(true);

        assertThrows(SamePasswordException.class,
                () -> userService.changePassword(new ChangePasswordCommand(1L, "pass", "pass")));
    }

    @Test
    void changePassword_Should_changePassword_When_oldPasswordDoesNotMatchNewPassword() {
        User user = User.builder()
                .id(1L)
                .passwordHash("encoded-pass")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", user.getPasswordHash())).thenReturn(false);

        userService.changePassword(new ChangePasswordCommand(1L, "old", "new"));
        verify(userRepository).save(user);
    }

    @Test
    void changeEmail_Should_throwEntityNotFoundException_When_UserNotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.changeEmail(
                        new ChangeEmailCommand(1L, "new@email.com"))
        );
    }

    @Test
    void changeEmail_Should_throwSameEmailException_When_emailMatches() {
        User user = User.builder()
                .id(1L)
                .email("")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThrows(SameEmailException.class,
                () -> userService.changeEmail(
                        new ChangeEmailCommand(1L, user.getEmail()))
        );
    }

    @Test
    void changeEmail_Should_throwDuplicateEntityException_When_emailExists() {
        User user = User.builder()
                .id(1L)
                .email("")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(any())).thenReturn(true);
        assertThrows(DuplicateEntityException.class,
                () -> userService.changeEmail(
                        new ChangeEmailCommand(1L, "new@email.com"))
        );
    }


    @Test
    void changeEmail_Should_changeEmail_When_emailDoesNotMatch() {
        User user = User.builder()
                .id(1L)
                .email("")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(any())).thenReturn(false);

        userService.changeEmail(new ChangeEmailCommand(1L, "new@email.com"));
        verify(userRepository).save(user);
        assertEquals("new@email.com", user.getEmail());
    }
}
