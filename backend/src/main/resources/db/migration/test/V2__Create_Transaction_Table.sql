CREATE TABLE IF NOT EXISTS app_cashflow_test.transaction
(
    id                         BIGSERIAL PRIMARY KEY,
    transaction_id             BIGINT         NOT NULL,
    customer_id                VARCHAR(15)    NOT NULL REFERENCES app_cashflow_test.customer (customer_id),
    customer_iban              VARCHAR(34)    NOT NULL,
    counterparty_iban          VARCHAR(34)    NOT NULL,
    counterparty_id            VARCHAR(15),
    counterparty_name          VARCHAR(140)   NOT NULL,
    counterparty_country       VARCHAR(3),
    counterparty_bank_name     VARCHAR(20)    NOT NULL,
    counterparty_bank_country  VARCHAR(3)     NOT NULL,
    counterparty_bank_bic_code VARCHAR(20)    NOT NULL,
    counterparty_type          VARCHAR(20)    NOT NULL,
    date                       DATE           NOT NULL,
    time                       TIME           NOT NULL,
    amount_org                 NUMERIC(18, 2) NOT NULL,
    amount_eur                 NUMERIC(18, 2) NOT NULL,
    currency                   CHAR(3)        NOT NULL,
    dc                         CHAR(1)        NOT NULL,
    transaction_scope          CHAR(1)        NOT NULL,
    channel_type               VARCHAR(25)    NOT NULL,
    channel_code               SMALLINT       NOT NULL,
    description                VARCHAR(210)   NOT NULL,
    fee_f                      BOOLEAN        NOT NULL, -- 0 - no fee, 1 - fee
    fee_type                   VARCHAR(3),
    is_rvrs_f                  BOOLEAN        NOT NULL DEFAULT false,
    is_rvrs_orig_id            BIGINT, -- original transaction_id
    was_later_rvrs_f           BOOLEAN        NOT NULL DEFAULT false
);