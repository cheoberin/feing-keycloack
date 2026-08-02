package com.cheobs.feing_keycloak_resource.domain.model.ledger;

import com.cheobs.feing_keycloak_resource.domain.model.ledger_entry.LedgerEntryCommand;

import java.util.List;
import java.util.UUID;

public record LedgerCommand(UUID idempotencyKey, String description, List<LedgerEntryCommand> entries) {
}