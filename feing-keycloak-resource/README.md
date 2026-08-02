# feing-keycloak-resource

Spring Boot resource server that exposes a simple double-entry ledger API, secured with Keycloak-issued JWTs.

## Architecture

The module follows a hexagonal (ports & adapters) layout:

```
domain/
  model/            business objects (Ledger, LedgerEntry, commands, enums)
  port/input/        use case interfaces called by adapters (e.g. CreateTransactionUseCase)
  port/output/        interfaces implemented by outbound adapters (e.g. LedgerRepositoryPort)
application/usecase/  use case implementations, orchestrate domain + ports
adapter/input/http/   REST controllers, DTOs, security and error-handling config
adapter/output/jpa/   Spring Data JPA repositories/entities implementing the output ports
```

Business rules (entry balancing, sufficient-funds check) live on the `Ledger` domain object, which is
constructed from a `LedgerCommand` plus the current per-account balances and decides `ACCEPTED` vs
`DECLINED` itself. Invalid input (e.g. a null idempotency key) throws `IllegalArgumentException` from
the domain constructors; a rule violation (unbalanced entries, insufficient funds) is not an error, it
simply produces a `DECLINED` ledger.

## Ledger rules

- A transaction is a `LedgerCommand` with one or more entries, each `DEBIT` or `CREDIT` against an
  account.
- **DEBIT increases** an account's balance, **CREDIT decreases** it.
- A transaction with a single entry is always considered balanced.
- A transaction with more than one entry must have `sum(DEBIT) == sum(CREDIT)` to be balanced.
- A transaction is `ACCEPTED` only if it is balanced **and** it would not drive any account's balance
  negative; otherwise it is `DECLINED`. Declined transactions are still persisted and do not count
  towards an account's balance.
- Requests are idempotent: replaying the same `idempotencyKey` returns the existing ledger instead of
  creating a new one.

## API

All endpoints are under `/api/v1/ledger` and require a Keycloak-issued bearer token.

| Method | Path                          | Role required     | Description                          |
|--------|-------------------------------|--------------------|--------------------------------------|
| POST   | `/api/v1/ledger`               | `LEDGER_WRITER`     | Create a ledger transaction          |
| GET    | `/api/v1/ledger/{id}`          | `LEDGER_READER`     | Get a ledger transaction by id       |
| GET    | `/api/v1/ledger/balance/{accountId}` | `LEDGER_READER` | Get an account's current balance     |

OpenAPI/Swagger UI is available at `/swagger-ui.html` when the app is running.

## Running locally

The app reads `SPRING_PROFILES_ACTIVE` with no default, so a profile must be provided, e.g. `local`,
which loads `application-local.yaml` (Postgres on `localhost:5432` and a Keycloak realm at
`http://localhost:8082/realms/feing-keycloak`). Both need to be running separately; there is no
docker-compose file in this repo yet.

```
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

## Testing

```
./mvnw test
```

- `FeingKeycloakResourceApplicationTests` is a context-load smoke test.
- `LedgerControllerIT` boots the full application context against an in-memory H2 database and drives
  all three endpoints through `MockMvc`, covering: authentication/authorization (401/403), request
  validation (400), not-found (404), accepted vs. declined transactions (unbalanced entries and
  insufficient funds), idempotency-driven balance calculation, and the balance endpoint.

Tests run against `src/test/resources/application.yaml`, which overrides the datasource to an H2
in-memory database, disables Flyway (schema is generated from the JPA entities instead), and points the
JWT resource server at a dummy JWK set URI so the security filter chain can be built without a real
Keycloak instance. Test requests authenticate via Spring Security's `jwt()` `MockMvc` post-processor
with explicit granted authorities, bypassing real token verification.
