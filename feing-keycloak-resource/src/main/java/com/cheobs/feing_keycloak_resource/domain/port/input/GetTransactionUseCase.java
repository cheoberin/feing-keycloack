package com.cheobs.feing_keycloak_resource.domain.port.input;

import com.cheobs.feing_keycloak_resource.domain.model.ledger.Ledger;

import java.util.UUID;

public interface GetTransactionUseCase {

    Ledger execute(UUID id);

}
