package com.optifi.services.commands;

import com.optifi.dto.userDtos.ChangePasswordRequestDto;

public record ChangePasswordCommand(Long userId, String oldPassword, String newPassword) {
    public static ChangePasswordCommand fromDto(ChangePasswordRequestDto dto, Long userId) {
        return new ChangePasswordCommand(userId, dto.oldPassword(), dto.newPassword());
    }
}
