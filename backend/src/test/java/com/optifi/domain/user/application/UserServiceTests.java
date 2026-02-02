package com.optifi.domain.user.application;

import com.optifi.domain.shared.Currency;
import com.optifi.domain.user.application.command.*;
import com.optifi.domain.shared.SupportedLocale;
import com.optifi.exceptions.*;
import com.optifi.domain.shared.Role;
import com.optifi.domain.user.model.User;
import com.optifi.domain.user.repository.UserRepository;
import com.optifi.domain.auth.application.command.RegisterUserCommand;
import com.optifi.domain.user.application.result.UserDetailsResult;
import com.optifi.domain.user.application.result.UserSummaryResult;
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

        UserDetailsResult result = userService.getUser(2L);

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

        List<UserSummaryResult> results = userService.getAllUsers();

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
    void createUser_Should_throwDuplicateEntityException_When_emailExists() {
        when(userRepository.existsByEmail("email")).thenReturn(true);
        assertThrows(DuplicateEntityException.class,
                () -> userService.createUser(
                        new RegisterUserCommand("john", "pass", "email"), Role.USER)
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
    void deleteUser_Should_deleteUser_When_adminDeletesSelfAndHasMoreThanOneAdmin() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(2L);

        userService.deleteUser(1L, 1L);

        verify(userRepository).deleteById(1L);
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

    @Test
    void deleteUser_Should_throwException_When_targetDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.existsById(2L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> userService.deleteUser(2L, 1L));

        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void setPreferences_Should_throwException_When_userDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.setPreferences(
                        new SetUserPreferenceCommand(
                                1L,
                                Currency.values()[0],
                                SupportedLocale.values()[0]
                        )
                )
        );
    }

    @Test
    void setPreferences_Should_savePreferences_When_userExists() {
        User user = User.builder().build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.setPreferences(
                new SetUserPreferenceCommand(
                        1L,
                        Currency.USD,
                        SupportedLocale.BG_BG
                )
        );
        assertEquals(Currency.USD, user.getBaseCurrency());
        assertEquals(SupportedLocale.BG_BG, user.getLocale());
    }

    @Test
    void changeUserRole_Should_throwException_When_currentUserDoesNotExist() {
        RoleChangeAction action = RoleChangeAction.PROMOTE_TO_ADMIN;
        ChangeUserRoleCommand cmd = new ChangeUserRoleCommand(1L, 2L, action);
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.changeUserRole(cmd)
        );
    }

    @Test
    void changeUserRole_Should_throwException_When_targetUserDoesNotExist() {
        RoleChangeAction action = RoleChangeAction.PROMOTE_TO_ADMIN;
        ChangeUserRoleCommand cmd = new ChangeUserRoleCommand(1L, 2L, action);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.changeUserRole(cmd)
        );
    }

    @Test
    void changeUserRole_Should_throwException_When_currentUserIsNotAdmin() {
        RoleChangeAction action = RoleChangeAction.PROMOTE_TO_ADMIN;
        ChangeUserRoleCommand cmd = new ChangeUserRoleCommand(1L, 2L, action);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        assertThrows(AuthorizationException.class,
                () -> userService.changeUserRole(cmd)
        );
    }

    @Test
    void changeUserRole_Should_throwException_When_currentUserMatchesTargetUser() {
        RoleChangeAction action = RoleChangeAction.PROMOTE_TO_ADMIN;
        ChangeUserRoleCommand cmd = new ChangeUserRoleCommand(1L, 1L, action);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        assertThrows(AuthorizationException.class,
                () -> userService.changeUserRole(cmd)
        );
    }

    @Test
    void changeUserRole_Should_throwException_When_targetUserIsBlocked() {
        RoleChangeAction action = RoleChangeAction.PROMOTE_TO_ADMIN;
        user2.setRole(Role.BLOCKED);
        ChangeUserRoleCommand cmd = new ChangeUserRoleCommand(1L, 2L, action);
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        assertThrows(IllegalStateTransitionException.class,
                () -> userService.changeUserRole(cmd)
        );
    }

    @Test
    void changeUserRole_Should_throwException_When_promotingToAdminAndTargetUserIsAlreadyAdmin() {
        RoleChangeAction action = RoleChangeAction.PROMOTE_TO_ADMIN;
        user2.setRole(Role.ADMIN);
        ChangeUserRoleCommand cmd = new ChangeUserRoleCommand(1L, 2L, action);
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        assertThrows(IllegalStateTransitionException.class,
                () -> userService.changeUserRole(cmd)
        );
    }

    @Test
    void changeUserRole_Should_throwException_When_promotingToModeratorAndTargetUserIsAlreadyAdmin() {
        RoleChangeAction action = RoleChangeAction.PROMOTE_TO_MODERATOR;
        user2.setRole(Role.ADMIN);
        ChangeUserRoleCommand cmd = new ChangeUserRoleCommand(1L, 2L, action);
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        assertThrows(IllegalStateTransitionException.class,
                () -> userService.changeUserRole(cmd)
        );
    }

    @Test
    void changeUserRole_Should_throwException_When_promotingToModeratorAndTargetUserIsAlreadyModerator() {
        RoleChangeAction action = RoleChangeAction.PROMOTE_TO_MODERATOR;
        user2.setRole(Role.MODERATOR);
        ChangeUserRoleCommand cmd = new ChangeUserRoleCommand(1L, 2L, action);
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        assertThrows(IllegalStateTransitionException.class,
                () -> userService.changeUserRole(cmd)
        );
    }

    @Test
    void changeUserRole_Should_throwException_When_demoteToUserAndTargetUserIsNotModeratorOrAdmin() {
        RoleChangeAction action = RoleChangeAction.DEMOTE_TO_USER;
        user2.setRole(Role.USER);
        ChangeUserRoleCommand cmd = new ChangeUserRoleCommand(1L, 2L, action);
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        assertThrows(IllegalStateTransitionException.class,
                () -> userService.changeUserRole(cmd)
        );
    }

    @Test
    void changeUserRole_Should_succeed_When_promotingToAdminValid() {
        RoleChangeAction action = RoleChangeAction.PROMOTE_TO_ADMIN;
        user2.setRole(Role.USER);
        ChangeUserRoleCommand cmd = new ChangeUserRoleCommand(1L, 2L, action);
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        userService.changeUserRole(cmd);
        assertEquals(Role.ADMIN, user2.getRole());
    }

    @Test
    void changeUserRole_Should_succeed_When_promotingToModeratorValid() {
        RoleChangeAction action = RoleChangeAction.PROMOTE_TO_MODERATOR;
        user2.setRole(Role.USER);
        ChangeUserRoleCommand cmd = new ChangeUserRoleCommand(1L, 2L, action);
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        userService.changeUserRole(cmd);
        assertEquals(Role.MODERATOR, user2.getRole());
    }

    @Test
    void changeUserRole_Should_succeed_When_demoteAdminToUserValid() {
        RoleChangeAction action = RoleChangeAction.DEMOTE_TO_USER;
        user2.setRole(Role.ADMIN);
        ChangeUserRoleCommand cmd = new ChangeUserRoleCommand(1L, 2L, action);
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        userService.changeUserRole(cmd);
        assertEquals(Role.USER, user2.getRole());
    }

    @Test
    void changeUserRole_Should_succeed_When_demoteModeratorToUserValid() {
        RoleChangeAction action = RoleChangeAction.DEMOTE_TO_USER;
        user2.setRole(Role.MODERATOR);
        ChangeUserRoleCommand cmd = new ChangeUserRoleCommand(1L, 2L, action);
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        userService.changeUserRole(cmd);
        assertEquals(Role.USER, user2.getRole());
    }

    @Test
    void banUser_Should_throwError_When_currentUserDoesNotExist() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.banUser(new BanUserCommand(1L, 2L)));
    }

    @Test
    void banUser_Should_throwError_When_targetUserDoesNotExist() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.banUser(new BanUserCommand(1L, 2L)));
    }

    @Test
    void banUser_Should_throwError_When_currentUserMatchesTargetUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        assertThrows(AuthorizationException.class,
                () -> userService.banUser(new BanUserCommand(2L, 2L)));
    }

    @Test
    void banUser_Should_throwError_When_currentUserIsNotAdminOrModerator() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        assertThrows(AuthorizationException.class,
                () -> userService.banUser(new BanUserCommand(1L, 2L)));
    }

    @Test
    void banUser_Should_throwError_When_targetUserIsBlocked() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        user2.setRole(Role.BLOCKED);
        assertThrows(IllegalStateTransitionException.class,
                () -> userService.banUser(new BanUserCommand(1L, 2L)));
    }

    @Test
    void banUser_Should_throwError_When_targetUserIsAdmin() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        user2.setRole(Role.ADMIN);
        assertThrows(IllegalStateTransitionException.class,
                () -> userService.banUser(new BanUserCommand(1L, 2L)));
    }

    @Test
    void banUser_Should_succeed_When_targetValid() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        userService.banUser(new BanUserCommand(1L, 2L));
        assertEquals(Role.BLOCKED, user2.getRole());
    }

    @Test
    void unbanUser_Should_throwError_When_currentUserDoesNotExist() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.unbanUser(new UnbanUserCommand(1L, 2L)));
    }

    @Test
    void unbanUser_Should_throwError_When_targetUserDoesNotExist() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.unbanUser(new UnbanUserCommand(1L, 2L)));
    }

    @Test
    void unbanUser_Should_throwError_When_currentUserMatchesTargetUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        assertThrows(AuthorizationException.class,
                () -> userService.unbanUser(new UnbanUserCommand(2L, 2L)));
    }

    @Test
    void unbanUser_Should_throwError_When_currentUserIsNotAdminOrModerator() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        assertThrows(AuthorizationException.class,
                () -> userService.unbanUser(new UnbanUserCommand(1L, 2L)));
    }

    @Test
    void unbanUser_Should_throwError_When_targetUserIsNotBlocked() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        assertThrows(IllegalStateTransitionException.class,
                () -> userService.unbanUser(new UnbanUserCommand(1L, 2L)));
    }

    @Test
    void unbanUser_Should_succeed_When_targetValid() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user2));
        user2.setRole(Role.BLOCKED);
        userService.unbanUser(new UnbanUserCommand(1L, 2L));
        assertEquals(Role.USER, user2.getRole());
    }
}
