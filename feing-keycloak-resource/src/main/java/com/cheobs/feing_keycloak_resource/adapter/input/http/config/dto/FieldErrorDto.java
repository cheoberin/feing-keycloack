package com.cheobs.feing_keycloak_resource.adapter.input.http.config.dto;

public record FieldErrorDto(
        String field,
        String message
) {
}