package com.optifi.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(basePackages = "com.optifi.domain")
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApi(ApiException ex, HttpServletRequest req) {
        return buildError(
                ex.code().status(),
                ex.getMessage(),
                req,
                ex.code().name(),
                ex.details(),
                null
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(
            DataIntegrityViolationException e,
            HttpServletRequest request
    ) {
        log.warn("Data integrity violation: {}", e.getMostSpecificCause().getMessage());
        ErrorCode errorCode = ErrorCode.DATA_INTEGRITY;
        return buildError(
                errorCode.status(),
                errorCode.defaultMessage(),
                request,
                errorCode.name(),
                null,
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {

        Map<String, String> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage,
                        (msg1, msg2) -> msg1,
                        java.util.LinkedHashMap::new
                ));
        ErrorCode code = ErrorCode.VALIDATION;
        return buildError(code.status(),
                code.defaultMessage(),
                request,
                code.name(),
                null,
                fieldErrors
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiError> handleAuthorizationDeniedException(
            HttpServletRequest request
    ) {
        ErrorCode code = ErrorCode.FORBIDDEN;
        return buildError(code.status(),
                code.defaultMessage(),
                request,
                code.name(),
                null,
                null
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request
    ) {
        ErrorCode code = ErrorCode.BAD_REQUEST;
        return buildError(
                code.status(),
                "Invalid value for parameter '" + e.getName() + "'",
                request,
                code.name(),
                Map.of("parameter", e.getName(), "value", String.valueOf(e.getValue())),
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAnyException(Exception e, HttpServletRequest request) {
        log.error("Unexpected error", e);
        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;
        return buildError(
                code.status(),
                code.defaultMessage(),
                request,
                code.name(),
                null,
                null
        );
    }

    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            String code,
            Map<String, Object> details,
            Map<String, String> fieldErrors
    ) {
        Map<String, Object> mergedDetails = new LinkedHashMap<>();
        if (details != null) {
            mergedDetails = new LinkedHashMap<>(details);
        }
        if (fieldErrors != null && !fieldErrors.isEmpty()) {
            mergedDetails.put("fieldErrors", fieldErrors);
        }

        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                code,
                Map.copyOf(mergedDetails)
        );
        return ResponseEntity.status(status).body(body);
    }
}