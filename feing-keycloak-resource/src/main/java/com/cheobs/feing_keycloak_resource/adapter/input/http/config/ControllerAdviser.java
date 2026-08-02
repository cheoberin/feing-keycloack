package com.cheobs.feing_keycloak_resource.adapter.input.http.config;

import com.cheobs.feing_keycloak_resource.adapter.input.http.config.dto.FieldErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ControllerAdviser {

    private final Logger logger = LoggerFactory.getLogger(ControllerAdviser.class);

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> notFound(NoSuchElementException error) {

        logger.warn(error.getMessage());

        Map<String, Object> body = Map.of(
                "status", 404,
                "error", "Not Found",
                "message", error.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> accessDenied(AccessDeniedException error) {

        logger.warn(error.getMessage());

        Map<String, Object> body = Map.of(
                "status", 403,
                "error", "Forbidden",
                "message", error.getMessage()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<FieldErrorDto>> argumentNotValid(MethodArgumentNotValidException error) {

        logger.warn(error.getMessage());

        var fields = error.getFieldErrors().stream().map(
                fieldError -> new FieldErrorDto(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                )).toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(fields);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception e, HttpServletRequest request) {

        logger.error("Unexpected error on {} {}", request.getMethod(), request.getRequestURI(), e);

        Map<String, Object> body = Map.of(
                "status", 500,
                "error", "Internal Server Error",
                "message", e.getMessage(),
                "traceId", MDC.get("traceId")
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

}
