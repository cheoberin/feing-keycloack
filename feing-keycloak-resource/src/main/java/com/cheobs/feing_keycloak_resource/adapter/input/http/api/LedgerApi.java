package com.cheobs.feing_keycloak_resource.adapter.input.http.api;

import com.cheobs.feing_keycloak_resource.adapter.input.http.dto.request.LedgerTransactionRequestDto;
import com.cheobs.feing_keycloak_resource.adapter.input.http.dto.response.AccountBalanceResponseDto;
import com.cheobs.feing_keycloak_resource.adapter.input.http.dto.response.LedgerResponseDetailsDto;
import com.cheobs.feing_keycloak_resource.adapter.input.http.dto.response.LedgerResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@Tag(name = "Ledger", description = "Endpoints for Ledger Usage")
@RequestMapping("/api/v1/ledger")
public interface LedgerApi {

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LEDGER_READER')")
    @Operation(summary = "Get ledger transaction", description = "Return the details of a specific transaction")
    @ApiResponse(responseCode = "200", description = "Ledger transaction successfully retrieved", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LedgerResponseDto.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(hidden = true)))
    ResponseEntity<LedgerResponseDetailsDto> getLedgerTransaction(@PathVariable(name = "id") UUID id);

    @PostMapping
    @PreAuthorize("hasRole('LEDGER_WRITER')")
    @Operation(summary = "Create ledger transaction", description = "Write a new ledger transaction")
    @ApiResponse(responseCode = "200", description = "Ledger transaction successfully retrieved", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LedgerResponseDto.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(hidden = true)))
    ResponseEntity<LedgerResponseDto> createLedgerTransaction(@RequestBody @Valid LedgerTransactionRequestDto ledgerTransactionRequestDto);

    @GetMapping("/balance/{accountId}")
    @PreAuthorize("hasRole('LEDGER_READER')")
    @Operation(summary = "Get Account Balance")
    @ApiResponse(responseCode = "200", description = "Ledger transaction successfully retrieved", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LedgerResponseDto.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(hidden = true)))
    ResponseEntity<AccountBalanceResponseDto> getAccountBalance(@PathVariable(name = "accountId") UUID accountId);

}
