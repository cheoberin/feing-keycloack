package com.cheobs.feing_keycloak_resource.adapter.input.http.dto.response;

import com.cheobs.feing_keycloak_resource.domain.model.ledger.Ledger;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record LedgerResponseDetailsDto(
        UUID id,
        String status,
        String description,
        List<LedgerEntryResponseDto> entries,
        ZonedDateTime processedAt) {

    public LedgerResponseDetailsDto(Ledger ledger) {
        this(
                ledger.getId(),
                ledger.getStatus().name(),
                ledger.getDescription(),
                ledger.getEntries().stream().map(LedgerEntryResponseDto::new).toList(),
                ledger.getCreatedAt()
        );
    }

}
