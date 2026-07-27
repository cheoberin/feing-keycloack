package com.cheobs.feing_keycloak_resource.adapter.output.jpa.implementation;

import com.cheobs.feing_keycloak_resource.adapter.output.jpa.repository.LedgerRepository;
import org.springframework.stereotype.Service;

@Service
public class LedgerAdapter {

    private final LedgerRepository ledgerRepository;

    public LedgerAdapter(LedgerRepository ledgerRepository) {
        this.ledgerRepository = ledgerRepository;
    }


}
