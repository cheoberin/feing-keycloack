package com.cheobs.feing_keycloak_resource.domain.model.ledger;

import com.cheobs.feing_keycloak_resource.domain.model.ledger_entry.LedgerEntry;
import com.cheobs.feing_keycloak_resource.domain.model.ledger_entry.LedgerEntryType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Ledger {

    private UUID id;
    private UUID idempotencyKey;
    private String description;
    private LedgerStatus status;
    private ZonedDateTime createdAt;
    private List<LedgerEntry> entries;

    public Ledger() {
    }

    public Ledger(UUID id, UUID idempotencyKey, String description, LedgerStatus status, ZonedDateTime createdAt, List<LedgerEntry> entries) {
        this.id = id;
        this.idempotencyKey = idempotencyKey;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.entries = entries;
    }

    public Ledger(LedgerCommand command, ZonedDateTime createdAt, Map<UUID, BigDecimal> currentBalances) {

        if (command == null) {
            throw new IllegalArgumentException("command must not be null");
        }

        if (command.idempotencyKey() == null) {
            throw new IllegalArgumentException("idempotencyKey must not be null");
        }

        if (command.entries() == null || command.entries().isEmpty()) {
            throw new IllegalArgumentException("entries must not be empty");
        }

        this.id = null;
        this.idempotencyKey = command.idempotencyKey();
        this.description = command.description();
        this.createdAt = createdAt;
        this.entries = command.entries().stream()
                .map(entryCommand -> new LedgerEntry(entryCommand, createdAt))
                .collect(Collectors.toList());
        this.status = isBalanced(this.entries) && hasSufficientFunds(this.entries, currentBalances)
                ? LedgerStatus.ACCEPTED
                : LedgerStatus.DECLINED;
    }

    private static boolean isBalanced(List<LedgerEntry> entries) {
        if (entries.size() <= 1) {
            return true;
        }
        BigDecimal credits = sumByType(entries, LedgerEntryType.CREDIT);
        BigDecimal debits = sumByType(entries, LedgerEntryType.DEBIT);
        return credits.compareTo(debits) == 0;
    }

    private static BigDecimal sumByType(List<LedgerEntry> entries, LedgerEntryType type) {
        return entries.stream()
                .filter(entry -> entry.getEntryType() == type)
                .map(LedgerEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static boolean hasSufficientFunds(List<LedgerEntry> entries, Map<UUID, BigDecimal> currentBalances) {
        Map<UUID, BigDecimal> projectedMovement = new HashMap<>();
        for (LedgerEntry entry : entries) {
            BigDecimal movement = entry.getEntryType() == LedgerEntryType.DEBIT ? entry.getAmount() : entry.getAmount().negate();
            projectedMovement.merge(entry.getAccountId(), movement, BigDecimal::add);
        }

        for (Map.Entry<UUID, BigDecimal> movement : projectedMovement.entrySet()) {
            BigDecimal currentBalance = currentBalances.getOrDefault(movement.getKey(), BigDecimal.ZERO);
            if (currentBalance.add(movement.getValue()).compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }
        }

        return true;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(UUID idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LedgerStatus getStatus() {
        return status;
    }

    public void setStatus(LedgerStatus status) {
        this.status = status;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<LedgerEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<LedgerEntry> entries) {
        this.entries = entries;
    }

}
