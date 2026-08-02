package com.cheobs.feing_keycloak_resource.adapter.input.http.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountBalanceResponseDto(
        UUID accountId,
        BigDecimal accountBalance
) {
}
