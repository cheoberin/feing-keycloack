package com.cheobs.feing_keycloak_resource.adapter.input.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ControllerAdviser {

    private final Logger logger = LoggerFactory.getLogger(ControllerAdviser.class);


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception e, HttpServletRequest request) {

        logger.error("Unexpected error on {} {}", request.getMethod(), request.getRequestURI(), e);

        Map<String, Object> body = Map.of(
                "status", 500,
                "error", "Internal Server Error",
                "message", e.getMessage(),
                "traceId", MDC.get("traceId")
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

}
