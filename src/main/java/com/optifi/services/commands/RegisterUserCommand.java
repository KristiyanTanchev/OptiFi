package com.optifi.services.commands;

import com.optifi.dto.registerDtos.RegisterRequestDto;

public record RegisterUserCommand(String username, String password, String email) {

    public static RegisterUserCommand from(RegisterRequestDto dto) {
        return new RegisterUserCommand(
                dto.username(),
                dto.password(),
                dto.email()
        );
    }
}