package com.optifi.domain.user.api.mapper;

import com.optifi.domain.shared.UserContext;
import com.optifi.domain.user.api.request.ChangeEmailRequestDto;
import com.optifi.domain.user.api.request.ChangePasswordRequestDto;
import com.optifi.domain.user.api.request.UserPreferencesUpdateRequestDto;
import com.optifi.domain.user.api.response.UserDetailsResponseDto;
import com.optifi.domain.user.api.response.UserSummaryResponseDto;
import com.optifi.domain.user.application.command.*;
import com.optifi.domain.user.application.result.UserDetailsResult;
import com.optifi.domain.user.application.result.UserSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    public UserDetailsResponseDto toDetailsDto(UserDetailsResult result) {
        return UserDetailsResponseDto.builder()
                .id(result.id())
                .username(result.username())
                .currency(result.baseCurrency())
                .locale(result.locale())
                .email(result.email())
                .role(result.role())
                .build();
    }

    public UserSummaryResponseDto toSummaryDto(UserSummaryResult result) {
        return UserSummaryResponseDto.builder()
                .id(result.id())
                .username(result.username())
                .role(result.role())
                .build();
    }

    public BanUserCommand toBanUserCommand(Long userId, UserContext ctx) {
        return BanUserCommand.builder()
                .targetId(userId)
                .currentUserId(ctx.userId())
                .build();
    }

    public UnbanUserCommand toUnbanUserCommand(Long userId, UserContext ctx) {
        return UnbanUserCommand.builder()
                .targetId(userId)
                .currentUserId(ctx.userId())
                .build();
    }

    public ChangeUserRoleCommand toChangeUserRoleCommand(Long userId, RoleChangeAction action, UserContext ctx) {
        return ChangeUserRoleCommand.builder()
                .targetId(userId)
                .currentUserId(ctx.userId())
                .action(action)
                .build();
    }

    public ChangePasswordCommand toChangePasswordCommand(ChangePasswordRequestDto dto, UserContext ctx) {
        return ChangePasswordCommand.builder()
                .userId(ctx.userId())
                .oldPassword(dto.oldPassword())
                .newPassword(dto.newPassword())
                .build();
    }

    public ChangeEmailCommand toChangeEmailCommand(ChangeEmailRequestDto dto, UserContext ctx) {
        return ChangeEmailCommand.builder()
                .userId(ctx.userId())
                .email(dto.email())
                .build();
    }

    public SetUserPreferenceCommand toSetUserPreferenceCommand(UserPreferencesUpdateRequestDto dto, UserContext ctx) {
        return SetUserPreferenceCommand.builder()
                .userId(ctx.userId())
                .locale(dto.locale())
                .baseCurrency(dto.currency())
                .build();
    }
}
