package com.optifi.domain.account.api.request;

import com.optifi.domain.shared.AccountType;
import com.optifi.domain.shared.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Account creation request")
public record AccountCreateRequestDto(
        @NotNull
        @Size(min = 3, max = 32, message = "Account name must be between 3 and 32 characters")
        String name,

        @NotNull
        AccountType type,

        @NotNull
        Currency currency,

        String institution) {
}
