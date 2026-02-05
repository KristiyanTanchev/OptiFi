package com.optifi.domain.user.api;

import com.optifi.config.web.CurrentUser;
import com.optifi.domain.shared.UserContext;
import com.optifi.domain.user.api.mapper.UserMapper;
import com.optifi.domain.user.application.command.*;
import com.optifi.domain.user.application.UserService;
import com.optifi.domain.user.application.result.UserDetailsResult;
import com.optifi.domain.user.application.result.UserSummaryResult;
import com.optifi.domain.user.api.request.ChangeEmailRequestDto;
import com.optifi.domain.user.api.request.ChangePasswordRequestDto;
import com.optifi.domain.user.api.response.UserDetailsResponseDto;
import com.optifi.domain.user.api.request.UserPreferencesUpdateRequestDto;
import com.optifi.domain.user.api.response.UserSummaryResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final UserMapper mapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<UserSummaryResponseDto>> getUsers() {
        List<UserSummaryResult> users = userService.getAllUsers();
        List<UserSummaryResponseDto> userDtos = users.stream().map(mapper::toSummaryDto).toList();
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or #id == authentication.principal.id")
    public ResponseEntity<UserDetailsResponseDto> getUser(
            @PathVariable @NotNull @Positive Long id) {
        UserDetailsResult userDetailsResult = userService.getUser(id);
        UserDetailsResponseDto userDetailsResponseDto = mapper.toDetailsDto(userDetailsResult);
        return ResponseEntity.ok(userDetailsResponseDto);
    }

    @PutMapping("/{id}/promote-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> promoteToAdmin(
            @PathVariable @NotNull @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        ChangeUserRoleCommand cmd = mapper.toChangeUserRoleCommand(id, RoleChangeAction.PROMOTE_TO_ADMIN, ctx);
        userService.changeUserRole(cmd);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/promote-moderator")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> promoteToModerator(
            @PathVariable @NotNull @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        ChangeUserRoleCommand cmd = mapper.toChangeUserRoleCommand(id, RoleChangeAction.PROMOTE_TO_MODERATOR, ctx);
        userService.changeUserRole(cmd);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/demote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> demoteToUser(
            @PathVariable @NotNull @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        ChangeUserRoleCommand cmd = mapper.toChangeUserRoleCommand(id, RoleChangeAction.DEMOTE_TO_USER, ctx);
        userService.changeUserRole(cmd);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> banUser(
            @PathVariable @NotNull @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        BanUserCommand cmd = mapper.toBanUserCommand(id, ctx);
        userService.banUser(cmd);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/unban")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> unbanUser(
            @PathVariable @NotNull @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        UnbanUserCommand cmd = mapper.toUnbanUserCommand(id, ctx);
        userService.unbanUser(cmd);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteUser(
            @PathVariable @NotNull @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        userService.deleteUser(id, ctx.userId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDetailsResponseDto> getOwnUser(
            @CurrentUser UserContext ctx
    ) {
        UserDetailsResult userDetailsResult = userService.getUser(ctx.userId());
        UserDetailsResponseDto userDetailsResponseDto = mapper.toDetailsDto(userDetailsResult);
        return ResponseEntity.ok(userDetailsResponseDto);
    }

    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequestDto dto,
            @CurrentUser UserContext ctx
    ) {
        ChangePasswordCommand cmd = mapper.toChangePasswordCommand(dto, ctx);
        userService.changePassword(cmd);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/email")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changeEmail(
            @Valid @RequestBody ChangeEmailRequestDto changeEmailRequestDto,
            @CurrentUser UserContext ctx) {
        ChangeEmailCommand cmd = mapper.toChangeEmailCommand(changeEmailRequestDto, ctx);
        userService.changeEmail(cmd);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/preferences")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePreferences(
            @Valid @RequestBody UserPreferencesUpdateRequestDto dto,
            @CurrentUser UserContext ctx) {
        SetUserPreferenceCommand cmd = mapper.toSetUserPreferenceCommand(dto, ctx);
        userService.setPreferences(cmd);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteUser(
            @CurrentUser UserContext ctx) {
        userService.deleteUser(ctx.userId(), ctx.userId());
        return ResponseEntity.noContent().build();
    }
}
