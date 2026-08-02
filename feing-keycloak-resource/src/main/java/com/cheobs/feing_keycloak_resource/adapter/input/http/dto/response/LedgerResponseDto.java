package com.cheobs.feing_keycloak_resource.adapter.input.http.dto.response;

import com.cheobs.feing_keycloak_resource.domain.model.ledger.Ledger;

import java.time.ZonedDateTime;
import java.util.UUID;

public record LedgerResponseDto(UUID id, String status, ZonedDateTime processedAt) {

    public LedgerResponseDto(Ledger ledger) {
        this(ledger.getId(), ledger.getStatus().name(), ledger.getCreatedAt());
    }

}
