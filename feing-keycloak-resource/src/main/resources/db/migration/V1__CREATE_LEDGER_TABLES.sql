CREATE TABLE ledger
(
    id              UUID PRIMARY KEY,
    idempotency_key UUID        NOT NULL UNIQUE,
    description     VARCHAR(255),
    status          VARCHAR(50) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL
);

CREATE TABLE ledger_entry
(
    id         UUID PRIMARY KEY,
    ledger_id  UUID           NOT NULL,
    account_id UUID           NOT NULL,
    amount     NUMERIC(18, 6) NOT NULL,
    type       VARCHAR(50)    NOT NULL,
    created_at TIMESTAMPTZ    NOT NULL,

    CONSTRAINT fk_ledger_entry_ledger
        FOREIGN KEY (ledger_id)
            REFERENCES ledger (id)
);

CREATE INDEX idx_ledger_entry_account
    ON ledger_entry (account_id);

CREATE INDEX idx_ledger_entry_entry
    ON ledger_entry (entry_id);

CREATE INDEX idx_ledger_created_at
    ON ledger (created_at);

CREATE INDEX idx_ledger_entry_created_at
    ON ledger_entry (created_at);