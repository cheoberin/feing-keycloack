package com.cheobs.feing_keycloak_resource.adapter.input.http.api;

import com.cheobs.feing_keycloak_resource.adapter.input.http.dto.response.LedgerResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Ledger", description = "Endpoints for Ledger Usage")
@RequestMapping("/api/v1/ledger")
public interface LedgerApi {

    @GetMapping
    @PreAuthorize("hasRole('LEDGER_READER')")
    @Operation(summary = "Return a list with all documents")
    @ApiResponse(responseCode = "200", description = "Ledger operation successfully retrieved", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LedgerResponseDto.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(hidden = true)))
    ResponseEntity<LedgerResponseDto> getLedgerOperation();


}
