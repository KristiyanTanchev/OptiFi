package com.optifi.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(
        @Schema(example = "2026-02-21T20:49:31Z", format="date-time")
        LocalDateTime timestamp,
        @Schema(example = "400")
        int status,
        @Schema(example = "Bad Request")
        String error,
        @Schema(example = "Validation error")
        String message,
        @Schema(example = "/api/...")
        String path,
        @Schema(description="Field validation errors")
        Map<String, String> fieldErrors
) {
}
