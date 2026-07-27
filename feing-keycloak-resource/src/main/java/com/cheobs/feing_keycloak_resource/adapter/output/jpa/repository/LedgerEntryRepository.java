package com.cheobs.feing_keycloak_resource.adapter.output.jpa.repository;

import com.cheobs.feing_keycloak_resource.adapter.output.jpa.entity.LedgerEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntryEntity, UUID> {
}
