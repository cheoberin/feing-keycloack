package com.cheobs.feing_keycloak_resource.adapter.output.jpa.implementation;

import com.cheobs.feing_keycloak_resource.adapter.output.jpa.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;

@Service
public class LedgerEntryAdapter {

    private final LedgerEntryRepository ledgerEntryRepository;

    public LedgerEntryAdapter(LedgerEntryRepository ledgerEntryRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
    }


}
