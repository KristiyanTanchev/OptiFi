package com.optifi.services;

import com.optifi.exceptions.*;
import com.optifi.models.Role;
import com.optifi.models.User;
import com.optifi.repositories.UserRepository;
import com.optifi.services.commands.ChangeEmailCommand;
import com.optifi.services.commands.ChangePasswordCommand;
import com.optifi.services.commands.RegisterUserCommand;
import com.optifi.services.results.GetUserResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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


    private User admin;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        admin = User.builder()
                .id(1L)
                .username("admin")
                .email("admin@test.com")
                .passwordHash("hash")
                .role(Role.ADMIN)
                .build();

        user1 = User.builder()
                .id(2L)
                .username("alice")
                .email("alice@test.com")
                .passwordHash("hash")
                .role(Role.USER)
                .build();

        user2 = User.builder()
                .id(3L)
                .username("bob")
                .email("bob@test.com")
                .passwordHash("hash")
                .role(Role.USER)
                .build();
    }

    @Test
    void getUser_Should_returnUser_When_userExists() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user1));

        GetUserResult result = userService.getUser(2L);

        assertEquals(2L, result.id());
        assertEquals("alice", result.username());
        assertEquals(Role.USER, result.role());
    }

    @Test
    void getUser_Should_throwException_When_userDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.getUser(99L));
    }


    @Test
    void getAllUsers_Should_returnUsersSortedByUsername_When_usersExist() {
        when(userRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(admin, user1, user2));

        List<GetUserResult> results = userService.getAllUsers();

        assertEquals(3, results.size());
        assertEquals("admin", results.get(0).username());
        assertEquals("alice", results.get(1).username());
        assertEquals("bob", results.get(2).username());
    }

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
    void changePassword_Should_throwAuthorizationException_When_oldPasswordIsIncorrect() {
        User user = User.builder()
                .id(1L)
                .passwordHash("encoded-pass")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", user.getPasswordHash())).thenReturn(false);

        assertThrows(AuthorizationException.class, () -> userService.changePassword(
                new ChangePasswordCommand(1L, "old", "new"))
        );
    }

    @Test
    void changePassword_Should_changePassword_When_oldPasswordIsCorrectAndDifferentFromNewPassword() {
        User user = User.builder()
                .id(1L)
                .passwordHash("encoded-pass")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", user.getPasswordHash())).thenReturn(true);

        userService.changePassword(new ChangePasswordCommand(1L, "old", "new"));
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
        assertEquals("new@email.com", user.getEmail());
    }

    @Test
    void deleteUser_Should_deleteUser_When_userDeletesSelf() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user1));
        when(userRepository.existsById(2L)).thenReturn(true);

        userService.deleteUser(2L, 2L);

        verify(userRepository).deleteById(2L);
    }

    @Test
    void deleteUser_Should_deleteUser_When_adminDeletesOtherUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.existsById(2L)).thenReturn(true);

        userService.deleteUser(2L, 1L);

        verify(userRepository).deleteById(2L);
    }

    @Test
    void deleteUser_Should_throwException_When_nonAdminDeletesOtherUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user1));

        assertThrows(AuthorizationException.class,
                () -> userService.deleteUser(3L, 2L));

        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteUser_Should_throwException_When_lastAdminDeletesSelf() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(1L);

        assertThrows(AuthorizationException.class,
                () -> userService.deleteUser(1L, 1L));

        verify(userRepository, never()).deleteById(anyLong());
    }
}
