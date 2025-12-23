package com.optifi.dto.registerDtos;

import com.optifi.services.commands.RegisterUserCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank(message = "Username cannot be null")
        @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters")
        String username,

        @NotBlank(message = "Password cannot be null")
        @Size(min = 6, max = 32, message = "Password must be between 6 and 32 characters")
        String password) {

    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(username, password);
    }
}
