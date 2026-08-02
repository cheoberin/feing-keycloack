package com.cheobs.feing_keycloak_resource.adapter.input.http.dto.request;

import com.cheobs.feing_keycloak_resource.domain.model.ledger_entry.LedgerEntryCommand;
import com.cheobs.feing_keycloak_resource.domain.model.ledger_entry.LedgerEntryType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record LedgerEntryRequestDto(
        @NotNull(message = "accountId must not be null")
        UUID accountId,
        @NotNull(message = "amount must not be null")
        BigDecimal amount,
        @NotNull(message = "entries must not be null")
        LedgerEntryType entryType
) {

    public LedgerEntryCommand toCommand() {
        return new LedgerEntryCommand(
                accountId,
                amount,
                entryType
        );
    }
}
