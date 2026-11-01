package com.university.club.common.exception;

import com.university.club.events.exception.InvalidEventStateException;
import com.university.club.events.exception.RegistrationNotFoundException;
import com.university.club.membership.exception.InvalidApplicationStateException;
import com.university.club.membership.exception.MembershipAlreadyExistsException;
import com.university.club.membership.exception.PendingApplicationExistsException;
import com.university.club.membership.exception.ProtectedRoleException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public record ApiError(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            Map<String, String> validationErrors
    ) {}

    // ---------------------------------------------------------------
    // Authentication failures → 401
    // BadCredentialsException, DisabledException, LockedException,
    // UsernameNotFoundException, InternalAuthenticationServiceException,
    // and any other AuthenticationException subclass all land here.
    // ---------------------------------------------------------------
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "Invalid email or password",
                Map.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "Please check the submitted information",
                errors
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleDuplicateEmail(EmailAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), Map.of());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), Map.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDatabaseConflict(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return buildResponse(
                HttpStatus.CONFLICT,
                "Conflict",
                "The submitted information conflicts with an existing record",
                Map.of()
        );
    }

    // ---------------------------------------------------------------
    // Catch-all. MUST log — otherwise any future 500 is invisible.
    // The exception as the last argument to log.error prints the
    // full stack trace.
    // ---------------------------------------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex) {
        log.error("Unhandled exception", ex);          // ← the fix
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred",
                Map.of()
        );
    }

    @ExceptionHandler({
            PendingApplicationExistsException.class,
            MembershipAlreadyExistsException.class,
            InvalidApplicationStateException.class
    })
    public ResponseEntity<Map<String, String>> handleMembershipConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ProtectedRoleException.class)
    public ResponseEntity<Map<String, String>> handleProtectedRole(ProtectedRoleException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEventNotFound(EventNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidEventStateException.class)
    public ResponseEntity<Map<String, String>> handleInvalidEventState(InvalidEventStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RegistrationNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleRegistrationNotFound(RegistrationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    // ---------------------------------------------------------------
    // Helper — fills in timestamp so no caller can forget it
    // ---------------------------------------------------------------
    private ResponseEntity<ApiError> buildResponse(
            HttpStatus status,
            String error,
            String message,
            Map<String, String> validationErrors) {

        ApiError body = new ApiError(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                validationErrors
        );
        return ResponseEntity.status(status).body(body);
    }
}