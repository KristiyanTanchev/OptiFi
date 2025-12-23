package com.optifi.dto.loginDtos;

import com.optifi.services.commands.LoginCommand;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password) {

    public LoginCommand toCommand() {
        return new LoginCommand(username, password);
    }
}

