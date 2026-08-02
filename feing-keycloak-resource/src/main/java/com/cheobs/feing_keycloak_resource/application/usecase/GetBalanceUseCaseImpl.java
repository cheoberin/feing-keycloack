package com.cheobs.feing_keycloak_resource.application.usecase;

import com.cheobs.feing_keycloak_resource.domain.port.input.GetBalanceUseCase;
import com.cheobs.feing_keycloak_resource.domain.port.output.LedgerRepositoryPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class GetBalanceUseCaseImpl implements GetBalanceUseCase {

    private final LedgerRepositoryPort ledgerRepositoryPort;

    public GetBalanceUseCaseImpl(LedgerRepositoryPort ledgerRepositoryPort) {
        this.ledgerRepositoryPort = ledgerRepositoryPort;
    }

    @Override
    public BigDecimal execute(UUID accountId) {
        return ledgerRepositoryPort.getBalance(accountId);
    }

}
