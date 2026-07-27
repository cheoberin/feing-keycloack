package com.cheobs.feing_keycloak_resource.adapter.input.http.controller;

import com.cheobs.feing_keycloak_resource.adapter.input.http.api.LedgerApi;
import com.cheobs.feing_keycloak_resource.adapter.input.http.dto.response.LedgerResponseDto;
import com.cheobs.feing_keycloak_resource.domain.port.input.CreateTransactionUseCase;
import com.cheobs.feing_keycloak_resource.domain.port.input.GetTransactionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LedgerController implements LedgerApi {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final GetTransactionUseCase getTransactionUseCase;

    public LedgerController(CreateTransactionUseCase createTransactionUseCase, GetTransactionUseCase getTransactionUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.getTransactionUseCase = getTransactionUseCase;
    }


    @Override
    public ResponseEntity<LedgerResponseDto> getLedgerOperation() {
        return null;
    }
}
