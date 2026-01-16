package com.optifi.domain.account.api.request;

import com.optifi.domain.account.application.command.CreateAccountCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountCreateRequestDto(
        @NotNull
        @Size(min = 3, max = 32, message = "Account name must be between 3 and 32 characters")
        String name,

        @NotNull @NotBlank
        String type,

        @NotNull @NotBlank
        String currency,

        String institution) {

    public CreateAccountCommand toCreateCommand(long userId) {
        return CreateAccountCommand.from(userId, name, type, currency, institution);
    }
}
