package com.cheobs.feing_keycloak_resource.domain.port.input;

import com.cheobs.feing_keycloak_resource.domain.model.ledger.Ledger;
import com.cheobs.feing_keycloak_resource.domain.model.ledger.LedgerCommand;

public interface CreateTransactionUseCase {

    Ledger execute(LedgerCommand command);

}
