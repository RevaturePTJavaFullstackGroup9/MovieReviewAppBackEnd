package com.revature.movie_review_back_end.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST API controllers.
 * <p>
 * Handles specific exceptions and returns structured error responses.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Handles {@link NotFoundException} and returns a 404 Not Found response.
     *
     * @param ex  the thrown NotFoundException
     * @param req the current HTTP request
     * @return a map containing error details such as timestamp, status, error, message, and path
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", 404);
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", req.getRequestURI());
        return body;
    }

    /**
     * Handles {@link MethodArgumentNotValidException} and returns a 400 Bad Request response.
     * <p>
     * Includes validation error messages for each invalid field.
     *
     * @param ex  the thrown MethodArgumentNotValidException
     * @param req the current HTTP request
     * @return a map containing error details such as timestamp, status, error, message, fields, and path
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", "Validation failed");
        body.put("fields", fieldErrors);
        body.put("path", req.getRequestURI());
        return body;
    }
}