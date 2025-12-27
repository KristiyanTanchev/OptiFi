package com.optifi.services.commands;

import com.optifi.dto.loginDtos.LoginRequestDto;

public record LoginCommand(String username, String password) {
    public static LoginCommand from(LoginRequestDto dto) {
        return new LoginCommand(dto.username(), dto.password());
    }
}
