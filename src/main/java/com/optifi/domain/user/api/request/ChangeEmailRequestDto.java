package com.optifi.domain.user.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailRequestDto(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email format is incorrect")
        String email
) {
}
