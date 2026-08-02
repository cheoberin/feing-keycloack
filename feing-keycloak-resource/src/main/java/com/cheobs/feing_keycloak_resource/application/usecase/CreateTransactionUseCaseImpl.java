package com.cheobs.feing_keycloak_resource.application.usecase;

import com.cheobs.feing_keycloak_resource.domain.model.ledger.Ledger;
import com.cheobs.feing_keycloak_resource.domain.model.ledger.LedgerCommand;
import com.cheobs.feing_keycloak_resource.domain.model.ledger_entry.LedgerEntryCommand;
import com.cheobs.feing_keycloak_resource.domain.port.input.CreateTransactionUseCase;
import com.cheobs.feing_keycloak_resource.domain.port.output.LedgerRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreateTransactionUseCaseImpl implements CreateTransactionUseCase {

    private final LedgerRepositoryPort ledgerRepositoryPort;

    public CreateTransactionUseCaseImpl(LedgerRepositoryPort ledgerRepositoryPort) {
        this.ledgerRepositoryPort = ledgerRepositoryPort;
    }

    @Override
    @Transactional
    public Ledger execute(LedgerCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command must not be null");
        }

        return ledgerRepositoryPort.findByIdempotencyKey(command.idempotencyKey())
                .orElseGet(() -> ledgerRepositoryPort.save(new Ledger(command, ZonedDateTime.now(), currentBalances(command))));
    }

    private Map<UUID, BigDecimal> currentBalances(LedgerCommand command) {
        return command.entries().stream()
                .map(LedgerEntryCommand::accountId)
                .distinct()
                .collect(Collectors.toMap(accountId -> accountId, ledgerRepositoryPort::getBalance));
    }

}
