package com.cheobs.feing_keycloak_resource.adapter.input.http.dto.response;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record LedgerEntryResponseDto(
        UUID accountId,
        BigDecimal amount,
        String type,
        ZonedDateTime processedAt
) {
}
