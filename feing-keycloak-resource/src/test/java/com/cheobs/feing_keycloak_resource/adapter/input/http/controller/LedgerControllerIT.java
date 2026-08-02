package com.cheobs.feing_keycloak_resource.adapter.input.http.controller;

import com.cheobs.feing_keycloak_resource.adapter.input.http.dto.request.LedgerEntryRequestDto;
import com.cheobs.feing_keycloak_resource.adapter.input.http.dto.request.LedgerTransactionRequestDto;
import com.cheobs.feing_keycloak_resource.domain.model.ledger_entry.LedgerEntryType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LedgerControllerIT {

    private static final SimpleGrantedAuthority READER = new SimpleGrantedAuthority("ROLE_LEDGER_READER");
    private static final SimpleGrantedAuthority WRITER = new SimpleGrantedAuthority("ROLE_LEDGER_WRITER");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    private LedgerTransactionRequestDto singleEntryTransaction(UUID accountId, BigDecimal amount, LedgerEntryType type) {
        return new LedgerTransactionRequestDto(UUID.randomUUID(), "single entry", List.of(new LedgerEntryRequestDto(accountId, amount, type)));
    }

    // ---- POST /api/v1/ledger ----

    @Test
    void createLedgerTransaction_withoutAuthentication_isUnauthorized() throws Exception {
        var request = singleEntryTransaction(UUID.randomUUID(), BigDecimal.TEN, LedgerEntryType.DEBIT);

        mockMvc.perform(post("/api/v1/ledger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createLedgerTransaction_withoutWriterRole_isForbidden() throws Exception {
        var request = singleEntryTransaction(UUID.randomUUID(), BigDecimal.TEN, LedgerEntryType.DEBIT);

        mockMvc.perform(post("/api/v1/ledger")
                        .with(jwt().authorities(READER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createLedgerTransaction_withInvalidPayload_isBadRequest() throws Exception {
        var request = new LedgerTransactionRequestDto(null, "missing idempotency key",
                List.of(new LedgerEntryRequestDto(UUID.randomUUID(), BigDecimal.TEN, LedgerEntryType.DEBIT)));

        mockMvc.perform(post("/api/v1/ledger")
                        .with(jwt().authorities(WRITER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("idempotencyKey"));
    }

    @Test
    void createLedgerTransaction_withBalancedEntriesAndSufficientFunds_isAccepted() throws Exception {
        UUID accountA = UUID.randomUUID();
        UUID accountB = UUID.randomUUID();

        // top up accountA so it can fund the CREDIT entry below
        mockMvc.perform(post("/api/v1/ledger")
                        .with(jwt().authorities(WRITER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(singleEntryTransaction(accountA, new BigDecimal("100"), LedgerEntryType.DEBIT))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        var request = new LedgerTransactionRequestDto(UUID.randomUUID(), "transfer accountA -> accountB", List.of(
                new LedgerEntryRequestDto(accountA, new BigDecimal("40"), LedgerEntryType.CREDIT),
                new LedgerEntryRequestDto(accountB, new BigDecimal("40"), LedgerEntryType.DEBIT)
        ));

        var result = mockMvc.perform(post("/api/v1/ledger")
                        .with(jwt().authorities(WRITER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String id = jsonMapper.readTree(result.getResponse().getContentAsString()).get("id").asString();

        mockMvc.perform(get("/api/v1/ledger/{id}", id).with(jwt().authorities(READER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.entries.length()").value(2));

        mockMvc.perform(get("/api/v1/ledger/balance/{accountId}", accountA).with(jwt().authorities(READER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountBalance").value(60));

        mockMvc.perform(get("/api/v1/ledger/balance/{accountId}", accountB).with(jwt().authorities(READER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountBalance").value(40));
    }

    @Test
    void createLedgerTransaction_withUnbalancedEntries_isDeclined() throws Exception {
        UUID accountA = UUID.randomUUID();
        UUID accountB = UUID.randomUUID();

        var request = new LedgerTransactionRequestDto(UUID.randomUUID(), "unbalanced", List.of(
                new LedgerEntryRequestDto(accountA, new BigDecimal("50"), LedgerEntryType.DEBIT),
                new LedgerEntryRequestDto(accountB, new BigDecimal("30"), LedgerEntryType.CREDIT)
        ));

        mockMvc.perform(post("/api/v1/ledger")
                        .with(jwt().authorities(WRITER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DECLINED"));

        mockMvc.perform(get("/api/v1/ledger/balance/{accountId}", accountA).with(jwt().authorities(READER)))
                .andExpect(jsonPath("$.accountBalance").value(0));
    }

    @Test
    void createLedgerTransaction_thatWouldOverdrawAccount_isDeclined() throws Exception {
        UUID accountE = UUID.randomUUID();
        UUID accountF = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/ledger")
                        .with(jwt().authorities(WRITER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(singleEntryTransaction(accountE, new BigDecimal("50"), LedgerEntryType.DEBIT))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        var request = new LedgerTransactionRequestDto(UUID.randomUUID(), "overdraw attempt", List.of(
                new LedgerEntryRequestDto(accountE, new BigDecimal("100"), LedgerEntryType.CREDIT),
                new LedgerEntryRequestDto(accountF, new BigDecimal("100"), LedgerEntryType.DEBIT)
        ));

        mockMvc.perform(post("/api/v1/ledger")
                        .with(jwt().authorities(WRITER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DECLINED"));

        mockMvc.perform(get("/api/v1/ledger/balance/{accountId}", accountE).with(jwt().authorities(READER)))
                .andExpect(jsonPath("$.accountBalance").value(50));
    }

    // ---- GET /api/v1/ledger/{id} ----

    @Test
    void getLedgerTransaction_withoutAuthentication_isUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/ledger/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getLedgerTransaction_withoutReaderRole_isForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/ledger/{id}", UUID.randomUUID()).with(jwt().authorities(WRITER)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getLedgerTransaction_whenNotFound_isNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/ledger/{id}", UUID.randomUUID()).with(jwt().authorities(READER)))
                .andExpect(status().isNotFound());
    }

    // ---- GET /api/v1/ledger/balance/{accountId} ----

    @Test
    void getAccountBalance_withoutAuthentication_isUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/ledger/balance/{accountId}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAccountBalance_withoutReaderRole_isForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/ledger/balance/{accountId}", UUID.randomUUID()).with(jwt().authorities(WRITER)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAccountBalance_forUnknownAccount_isZero() throws Exception {
        UUID accountId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/ledger/balance/{accountId}", accountId).with(jwt().authorities(READER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                .andExpect(jsonPath("$.accountBalance").value(0));
    }

}
