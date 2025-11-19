CREATE SCHEMA IF NOT EXISTS app_cashflow_test;

CREATE TABLE IF NOT EXISTS app_cashflow_test.customer
(
    customer_id            VARCHAR(15) PRIMARY KEY,
    customer_name          VARCHAR(140) NOT NULL,
    customer_type          VARCHAR(7)   NOT NULL
);