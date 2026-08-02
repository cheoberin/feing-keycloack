package com.cheobs.feing_keycloak_resource.adapter.output.jpa.implementation;

import com.cheobs.feing_keycloak_resource.adapter.output.jpa.entity.LedgerEntity;
import com.cheobs.feing_keycloak_resource.adapter.output.jpa.entity.LedgerEntryEntity;
import com.cheobs.feing_keycloak_resource.adapter.output.jpa.repository.LedgerEntryRepository;
import com.cheobs.feing_keycloak_resource.adapter.output.jpa.repository.LedgerRepository;
import com.cheobs.feing_keycloak_resource.domain.model.ledger.Ledger;
import com.cheobs.feing_keycloak_resource.domain.model.ledger_entry.LedgerEntry;
import com.cheobs.feing_keycloak_resource.domain.port.output.LedgerRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LedgerAdapter implements LedgerRepositoryPort {

    private final LedgerRepository ledgerRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public LedgerAdapter(LedgerRepository ledgerRepository, LedgerEntryRepository ledgerEntryRepository) {
        this.ledgerRepository = ledgerRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Override
    public Optional<Ledger> findByIdempotencyKey(UUID idempotencyKey) {
        return ledgerRepository.findByIdempotencyKey(idempotencyKey)
                .map(this::toDomain);
    }

    @Override
    public Optional<Ledger> findById(UUID id) {
        return ledgerRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public Ledger save(Ledger ledger) {
        LedgerEntity savedLedger = ledgerRepository.save(toEntity(ledger));

        List<LedgerEntryEntity> savedEntries = ledgerEntryRepository.saveAll(
                ledger.getEntries().stream()
                        .map(entry -> toEntity(entry, savedLedger))
                        .collect(Collectors.toList())
        );

        return toDomain(savedLedger, savedEntries);
    }

    @Override
    public BigDecimal getBalance(UUID accountId) {
        return ledgerEntryRepository.sumBalanceByAccountId(accountId);
    }

    private Ledger toDomain(LedgerEntity entity) {
        List<LedgerEntryEntity> entries = ledgerEntryRepository.findByLedgerId(entity.getId());
        return toDomain(entity, entries);
    }

    private Ledger toDomain(LedgerEntity entity, List<LedgerEntryEntity> entries) {
        return new Ledger(
                entity.getId(),
                entity.getIdempotencyKey(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entries.stream().map(this::toDomain).collect(Collectors.toList())
        );
    }

    private LedgerEntry toDomain(LedgerEntryEntity entity) {
        return new LedgerEntry(
                entity.getId(),
                entity.getLedger().getId(),
                entity.getAccountId(),
                entity.getAmount(),
                entity.getEntryType(),
                entity.getCreatedAt()
        );
    }

    private LedgerEntity toEntity(Ledger ledger) {
        return new LedgerEntity(
                ledger.getId(),
                ledger.getIdempotencyKey(),
                ledger.getDescription(),
                ledger.getStatus(),
                ledger.getCreatedAt()
        );
    }

    private LedgerEntryEntity toEntity(LedgerEntry entry, LedgerEntity ledger) {
        return new LedgerEntryEntity(
                entry.getId(),
                ledger,
                entry.getAccountId(),
                entry.getAmount(),
                entry.getEntryType(),
                entry.getCreatedAt()
        );
    }

}
