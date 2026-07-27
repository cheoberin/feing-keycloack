package com.cheobs.feing_keycloak_resource.adapter.input.http.dto.response;

import java.util.List;
import java.util.UUID;

public record LedgerResponseDetailsDto(
        UUID id,
        String status,
        String description,
        List<LedgerEntryResponseDto> entries
) {
}
