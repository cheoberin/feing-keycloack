package com.cheobs.feing_keycloak_resource.application.usecase;

import com.cheobs.feing_keycloak_resource.domain.model.ledger.Ledger;
import com.cheobs.feing_keycloak_resource.domain.port.input.GetTransactionUseCase;
import com.cheobs.feing_keycloak_resource.domain.port.output.LedgerRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class GetTransactionUseCaseImpl implements GetTransactionUseCase {

    private final LedgerRepositoryPort ledgerRepositoryPort;

    public GetTransactionUseCaseImpl(LedgerRepositoryPort ledgerRepositoryPort) {
        this.ledgerRepositoryPort = ledgerRepositoryPort;
    }

    @Override
    public Ledger execute(UUID id) {
        return ledgerRepositoryPort.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ledger transaction not found: " + id));
    }

}
