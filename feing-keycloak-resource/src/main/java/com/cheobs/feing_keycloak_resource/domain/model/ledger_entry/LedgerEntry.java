package com.cheobs.feing_keycloak_resource.domain.model.ledger_entry;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public class LedgerEntry {

    private UUID id;
    private UUID ledgerId;
    private UUID accountId;
    private BigDecimal amount;
    private LedgerEntryType entryType;
    private ZonedDateTime createdAt;

    public LedgerEntry() {
    }

    public LedgerEntry(UUID id, UUID ledgerId, UUID accountId, BigDecimal amount, LedgerEntryType entryType, ZonedDateTime createdAt) {
        this.id = id;
        this.ledgerId = ledgerId;
        this.accountId = accountId;
        this.amount = amount;
        this.entryType = entryType;
        this.createdAt = createdAt;
    }

    public LedgerEntry(LedgerEntryCommand command, ZonedDateTime createdAt) {

        if (command == null) {
            throw new IllegalArgumentException("entry command must not be null");
        }

        if (command.accountId() == null) {
            throw new IllegalArgumentException("entry accountId must not be null");
        }

        if (command.amount() == null || command.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("entry amount must be greater than zero");
        }

        if (command.entryType() == null) {
            throw new IllegalArgumentException("entry entryType must not be null");
        }
        this.id = null;
        this.ledgerId = null;
        this.accountId = command.accountId();
        this.amount = command.amount();
        this.entryType = command.entryType();
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(UUID ledgerId) {
        this.ledgerId = ledgerId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LedgerEntryType getEntryType() {
        return entryType;
    }

    public void setEntryType(LedgerEntryType entryType) {
        this.entryType = entryType;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
