package com.optifi.dto.userDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDto(
        @NotBlank(message = "Old password cannot be blank")
        String oldPassword,

        @NotBlank(message = "New password cannot be blank")
        @Size(min = 6, max = 32, message = "Password must be between 6 and 32 characters")
        String newPassword
) {
}
