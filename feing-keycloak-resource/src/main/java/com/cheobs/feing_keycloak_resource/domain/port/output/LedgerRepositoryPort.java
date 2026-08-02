package com.cheobs.feing_keycloak_resource.domain.port.output;

import com.cheobs.feing_keycloak_resource.domain.model.ledger.Ledger;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface LedgerRepositoryPort {

    Optional<Ledger> findByIdempotencyKey(UUID idempotencyKey);

    Optional<Ledger> findById(UUID id);

    Ledger save(Ledger ledger);

    BigDecimal getBalance(UUID accountId);

}
