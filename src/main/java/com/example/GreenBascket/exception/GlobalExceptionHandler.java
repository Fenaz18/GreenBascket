package com.example.GreenBascket.config; // Assuming this is where your GlobalExceptionHandler is

import com.example.GreenBascket.dto.MessageResponseDTO;
import com.example.GreenBascket.exception.ResourceNotFoundException; // Import your custom exception if you have one
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles the "User not found" case from UserService.loginUser and loadUserByUsername
    // and also similar authentication-related user not found issues.
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<MessageResponseDTO> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        System.err.println("UsernameNotFoundException: " + ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(new MessageResponseDTO("Invalid email or password."), HttpStatus.UNAUTHORIZED);
    }

    // Handles the "Invalid credentials" case from UserService.loginUser
    // Also catches general BadCredentialsException from Spring Security if you integrate it fully.
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MessageResponseDTO> handleBadCredentialsException(BadCredentialsException ex) {
        System.err.println("BadCredentialsException: " + ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(new MessageResponseDTO("Invalid email or password."), HttpStatus.UNAUTHORIZED);
    }

    // THIS IS THE KEY: Catching the generic RuntimeException you're throwing
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageResponseDTO> handleGenericRuntimeException(RuntimeException ex) {
        System.err.println("Generic RuntimeException: " + ex.getMessage());
        ex.printStackTrace(); // Crucial for debugging server-side

        // You need to decide how to map these.
        // For login-related generic RuntimeExceptions, you might want to treat them as UNAUTHORIZED
        if ("User not found".equals(ex.getMessage()) || "Invalid credentials".equals(ex.getMessage())) {
            return new ResponseEntity<>(new MessageResponseDTO("Invalid email or password."), HttpStatus.UNAUTHORIZED);
        }
        // For other unexpected RuntimeExceptions, treat as INTERNAL_SERVER_ERROR
        return new ResponseEntity<>(new MessageResponseDTO("An internal server error occurred."), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // Handle ProductServiceImpl's ResourceNotFoundException if you have one
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageResponseDTO> handleResourceNotFoundException(ResourceNotFoundException ex) {
        System.err.println("ResourceNotFoundException: " + ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(new MessageResponseDTO(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    // Handles validation errors (e.g., @Valid annotations on DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Catch-all for any other unexpected exceptions.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponseDTO> handleGlobalException(Exception ex) {
        System.err.println("Uncaught Global Exception: " + ex.getMessage());
        ex.printStackTrace(); // Always log the full stack trace

        return new ResponseEntity<>(new MessageResponseDTO("An unexpected server error occurred."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}