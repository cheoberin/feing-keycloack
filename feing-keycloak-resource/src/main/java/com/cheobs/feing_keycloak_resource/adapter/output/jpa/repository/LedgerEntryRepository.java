package com.cheobs.feing_keycloak_resource.adapter.output.jpa.repository;

import com.cheobs.feing_keycloak_resource.adapter.output.jpa.entity.LedgerEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntryEntity, UUID> {

    List<LedgerEntryEntity> findByLedgerId(UUID ledgerId);

    @Query("SELECT COALESCE(SUM(CASE WHEN e.entryType = com.cheobs.feing_keycloak_resource.domain.model.ledger_entry.LedgerEntryType.DEBIT THEN e.amount ELSE -e.amount END), 0) " +
            "FROM LedgerEntryEntity e " +
            "WHERE e.accountId = :accountId " +
            "AND e.ledger.status = com.cheobs.feing_keycloak_resource.domain.model.ledger.LedgerStatus.ACCEPTED")
    BigDecimal sumBalanceByAccountId(@Param("accountId") UUID accountId);

}
