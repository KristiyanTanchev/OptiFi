package com.optifi.controllers;

import com.optifi.dto.userDtos.*;
import com.optifi.security.CustomUserDetails;
import com.optifi.services.UserService;
import com.optifi.services.commands.*;
import com.optifi.services.results.UserDetailsResult;
import com.optifi.services.results.UserSummaryResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<UserSummaryResponseDto>> getUsers() {
        List<UserSummaryResult> users = userService.getAllUsers();
        List<UserSummaryResponseDto> userDtos = users.stream().map(UserSummaryResponseDto::fromResult).toList();
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or #id == authentication.principal.id")
    public ResponseEntity<UserDetailsResponseDto> getUser(
            @PathVariable long id) {
        UserDetailsResult userDetailsResult = userService.getUser(id);
        UserDetailsResponseDto userDetailsResponseDto = UserDetailsResponseDto.fromResult(userDetailsResult);
        return ResponseEntity.ok(userDetailsResponseDto);
    }

    @PutMapping("/{id}/promote-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> promoteToAdmin(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        ChangeUserRoleCommand cmd = new ChangeUserRoleCommand(id, principal.getId(), RoleChangeAction.PROMOTE_TO_ADMIN);
        userService.changeUserRole(cmd);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/promote-moderator")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> promoteToModerator(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        ChangeUserRoleCommand cmd =
                new ChangeUserRoleCommand(id, principal.getId(), RoleChangeAction.PROMOTE_TO_MODERATOR);
        userService.changeUserRole(cmd);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/demote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> demoteToUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        ChangeUserRoleCommand cmd = new ChangeUserRoleCommand(id, principal.getId(), RoleChangeAction.DEMOTE_TO_USER);
        userService.changeUserRole(cmd);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> banUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        BanUserCommand cmd = new BanUserCommand(id, principal.getId());
        userService.banUser(cmd);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/unban")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> unbanUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        UnbanUserCommand cmd = new UnbanUserCommand(id, principal.getId());
        userService.unbanUser(cmd);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        userService.deleteUser(id, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDetailsResponseDto> getOwnUser(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        UserDetailsResult userDetailsResult = userService.getUser(principal.getId());
        UserDetailsResponseDto userDetailsResponseDto = UserDetailsResponseDto.fromResult(userDetailsResult);
        return ResponseEntity.ok(userDetailsResponseDto);
    }

    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        ChangePasswordCommand cmd = ChangePasswordCommand.fromDto(changePasswordRequestDto, principal.getId());
        userService.changePassword(cmd);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/email")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changeEmail(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody ChangeEmailRequestDto changeEmailRequestDto) {
        ChangeEmailCommand cmd = ChangeEmailCommand.fromDto(changeEmailRequestDto, principal.getId());
        userService.changeEmail(cmd);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/preferences")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePreferences(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody UserPreferencesUpdateRequestDto dto) {
        SetUserPreferenceCommand cmd = SetUserPreferenceCommand.fromDto(dto, principal.getId());
        userService.setPreferences(cmd, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal CustomUserDetails principal) {
        userService.deleteUser(principal.getId(), principal.getId());
        return ResponseEntity.noContent().build();
    }
}
