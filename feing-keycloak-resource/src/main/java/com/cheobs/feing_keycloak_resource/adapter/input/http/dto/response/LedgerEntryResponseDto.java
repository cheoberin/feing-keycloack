package com.cheobs.feing_keycloak_resource.adapter.input.http.dto.response;

import com.cheobs.feing_keycloak_resource.domain.model.ledger_entry.LedgerEntry;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record LedgerEntryResponseDto(
        UUID accountId,
        BigDecimal amount,
        String type,
        ZonedDateTime processedAt
) {
    public LedgerEntryResponseDto(LedgerEntry ledgerEntry) {
        this(
                ledgerEntry.getAccountId(),
                ledgerEntry.getAmount(),
                ledgerEntry.getEntryType().name(),
                ledgerEntry.getCreatedAt()
        );
    }
}
