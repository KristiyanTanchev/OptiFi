package com.optifi.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(basePackages = "com.optifi.domain")
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(
            HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, "Invalid username or password", request, null);
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ApiError> handleDuplicateEntityException(
            DuplicateEntityException e,
            HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, e.getMessage(), request, null);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(
            EntityNotFoundException e,
            HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, e.getMessage(), request, null);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ApiError> handleAuthorizationException(
            AuthorizationException e,
            HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, e.getMessage(), request, null);
    }

    @ExceptionHandler(SamePasswordException.class)
    public ResponseEntity<ApiError> handleSamePassWordException(
            SamePasswordException e,
            HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, e.getMessage(), request, null);
    }

    @ExceptionHandler(IllegalStateTransitionException.class)
    public ResponseEntity<ApiError> handleIllegalStateTransitionException(
            IllegalStateTransitionException e,
            HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, e.getMessage(), request, null);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiError> handleLockedException(
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.FORBIDDEN, "User is banned", request, null);
    }

    @ExceptionHandler(SameEmailException.class)
    public ResponseEntity<ApiError> handleSameEmailException(
            SameEmailException e,
            HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, e.getMessage(), request, null);
    }

    @ExceptionHandler(EnumParsingError.class)
    public ResponseEntity<ApiError> handleEnumParsingError(
            EnumParsingError e,
            HttpServletRequest request) {
        Map<String, String> fieldErrors = Map.of(e.getField(), e.getError());
        return buildError(HttpStatus.BAD_REQUEST, e.getMessage(), request, fieldErrors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(
            DataIntegrityViolationException e,
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.BAD_REQUEST, e.getMessage(), request, null);
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<ApiError> handleInvalidDateException(
            InvalidDateException e,
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.BAD_REQUEST, e.getMessage(), request, null);
    }

    @ExceptionHandler(InvalidTimeZoneException.class)
    public ResponseEntity<ApiError> handleInvalidTimeZoneException(
            InvalidTimeZoneException e,
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.BAD_REQUEST, e.getMessage(), request, null);
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

        return buildError(HttpStatus.BAD_REQUEST, "Validation failed", request, fieldErrors);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiError> handleAuthorizationDeniedException(
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.FORBIDDEN,
                "Access denied",
                request,
                null
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpServletRequest req) {
        return buildError(HttpStatus.BAD_REQUEST, "Invalid request", req, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAnyException(Exception e, HttpServletRequest request) {
        log.error("Unexpected error", e);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request, null);
    }

    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> fieldErrors
    ) {
        ApiError body = new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.status(status).body(body);
    }
}