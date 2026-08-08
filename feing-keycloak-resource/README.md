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

## Keycloak client setup (`ledger-service`)

Tokens are authorized via a `JwtAuthenticationConverter` (see `SecurityConfig`) that reads
`resource_access.ledger-service.roles` off the JWT and maps each role to a
`ROLE_<role>` Spring authority. This means the `LEDGER_READER`/`LEDGER_WRITER` roles must be
defined as **client roles on a `ledger-service` client**, not as realm roles.

The `ledger-service` client itself is only used to hold those roles — it is not meant to issue
tokens directly to end users. Calling services authenticate as their own confidential clients and
get `ledger-service` roles assigned to their service account, so only other services (never
interactive users) can obtain a token authorized against this API.

Steps in the Keycloak admin console (realm `feing-keycloak`):

1. **Clients → Create client**
   - Client ID: `ledger-service`
   - Client authentication: **On** (confidential client, gets a secret)
   - Authentication flow: enable only **Service accounts roles**; disable **Standard flow**,
     **Direct access grants**, and **Implicit flow**. This is what prevents any interactive/user
     login through this client — it can only participate in machine-to-machine `client_credentials`
     exchanges.
2. **`ledger-service` client → Roles → Create role**
   - Create client roles `LEDGER_READER` and `LEDGER_WRITER`.
3. **For each calling service** (itself a confidential client with service accounts enabled):
   - `<calling-service>` client → **Service accounts roles** tab → **Assign role** → filter by
     client `ledger-service` → assign `LEDGER_READER` and/or `LEDGER_WRITER` as appropriate.
4. Calling services obtain a token via the client-credentials grant:
   ```
   POST http://localhost:8090/realms/feing-keycloak/protocol/openid-connect/token
   Content-Type: application/x-www-form-urlencoded

   grant_type=client_credentials&client_id=<calling-service>&client_secret=<secret>
   ```
   The resulting token's `resource_access.ledger-service.roles` claim carries whichever of
   `LEDGER_READER`/`LEDGER_WRITER` were assigned, which the converter turns into
   `ROLE_LEDGER_READER`/`ROLE_LEDGER_WRITER` authorities for the `@PreAuthorize` checks above.

## Running locally

The app reads `SPRING_PROFILES_ACTIVE` with no default, so a profile must be provided, e.g. `local`,
which loads `application-local.yaml` (Postgres on `localhost:5432` and a Keycloak realm at
`http://localhost:8090/realms/feing-keycloak`). Both need to be running separately; there is no
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
