package com.cheobs.feing_keycloak_resource.domain.model.ledger_entry;

import java.math.BigDecimal;
import java.util.UUID;

public record LedgerEntryCommand(UUID accountId, BigDecimal amount, LedgerEntryType entryType) {

}