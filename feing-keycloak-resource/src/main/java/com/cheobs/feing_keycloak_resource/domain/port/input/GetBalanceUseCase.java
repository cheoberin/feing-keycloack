package com.cheobs.feing_keycloak_resource.domain.port.input;

import java.math.BigDecimal;
import java.util.UUID;

public interface GetBalanceUseCase {

    BigDecimal execute(UUID accountId);

}
