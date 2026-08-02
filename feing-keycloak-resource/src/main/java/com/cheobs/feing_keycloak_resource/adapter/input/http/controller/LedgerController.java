package com.cheobs.feing_keycloak_resource.adapter.input.http.controller;

import com.cheobs.feing_keycloak_resource.adapter.input.http.api.LedgerApi;
import com.cheobs.feing_keycloak_resource.adapter.input.http.dto.request.LedgerTransactionRequestDto;
import com.cheobs.feing_keycloak_resource.adapter.input.http.dto.response.AccountBalanceResponseDto;
import com.cheobs.feing_keycloak_resource.adapter.input.http.dto.response.LedgerResponseDetailsDto;
import com.cheobs.feing_keycloak_resource.adapter.input.http.dto.response.LedgerResponseDto;
import com.cheobs.feing_keycloak_resource.domain.port.input.CreateTransactionUseCase;
import com.cheobs.feing_keycloak_resource.domain.port.input.GetBalanceUseCase;
import com.cheobs.feing_keycloak_resource.domain.port.input.GetTransactionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class LedgerController implements LedgerApi {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final GetTransactionUseCase getTransactionUseCase;
    private final GetBalanceUseCase getBalanceUseCase;

    public LedgerController(CreateTransactionUseCase createTransactionUseCase, GetTransactionUseCase getTransactionUseCase, GetBalanceUseCase getBalanceUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.getTransactionUseCase = getTransactionUseCase;
        this.getBalanceUseCase = getBalanceUseCase;
    }

    @Override
    public ResponseEntity<LedgerResponseDetailsDto> getLedgerTransaction(UUID id) {
        var domain = getTransactionUseCase.execute(id);
        var response = new LedgerResponseDetailsDto(domain);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<LedgerResponseDto> createLedgerTransaction(LedgerTransactionRequestDto ledgerTransactionRequestDto) {
        var domain = createTransactionUseCase.execute(ledgerTransactionRequestDto.toCommand());
        var response = new LedgerResponseDto(domain);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AccountBalanceResponseDto> getAccountBalance(UUID accountId) {
        var balance = getBalanceUseCase.execute(accountId);
        var response = new AccountBalanceResponseDto(accountId, balance);
        return ResponseEntity.ok(response);
    }
}
