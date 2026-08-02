package com.cheobs.feing_keycloak_resource.adapter.input.http.dto.request;

import com.cheobs.feing_keycloak_resource.domain.model.ledger.LedgerCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record LedgerTransactionRequestDto(
        @NotNull(message = "idempotencyKey must not be null")
        UUID idempotencyKey,
        String description,
        @Valid
        @NotNull(message = "entries must no be null")
        @Size(min = 1, message = "at least one entry must be submitted")
        List<LedgerEntryRequestDto> entries
) {

    public LedgerCommand toCommand() {
        return new LedgerCommand(idempotencyKey, description, entries.stream().map(LedgerEntryRequestDto::toCommand).toList());
    }

}
