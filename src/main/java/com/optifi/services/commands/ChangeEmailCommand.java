package com.optifi.services.commands;

import com.optifi.dto.userDtos.ChangeEmailRequestDto;

public record ChangeEmailCommand(Long userId, String email) {
    public static ChangeEmailCommand fromDto(ChangeEmailRequestDto dto, Long userId) {
        return new ChangeEmailCommand(userId, dto.email());
    }
}
