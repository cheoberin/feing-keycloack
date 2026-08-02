package com.cheobs.feing_keycloak_resource.adapter.output.jpa.repository;

import com.cheobs.feing_keycloak_resource.adapter.output.jpa.entity.LedgerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LedgerRepository extends JpaRepository<LedgerEntity, UUID> {

    Optional<LedgerEntity> findByIdempotencyKey(UUID idempotencyKey);

}
