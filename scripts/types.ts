
export type CountryCode = 'EST' | 'SWE' | 'GER' | 'POL' | 'NOR' | 'GBR';

export type CustomerType = 'Private' | 'Corp' | 'SME';


export interface Bank {
    bankName: string;
    bankCode: string;
    branchCode: string;
    branchName: string;
    country: CountryCode; // Should be the iso_3166 alpha-3 code
    address: string;
    bicCode: string;
}

export interface Customer {
    customer_id: string;
    participant_name: string;
    psn_code: string;
    customer_type: CustomerType;
    postal_code: number;
    country: CountryCode;
    account: string;
    bank: Bank;
}

export interface Transfer {
    transaction_id: number; // Unique Transaction Line Number (Row Number, fee transaction is also a row)
    jh_jh: string; // Journal Number - the unique identifier assigned to the transaction when recorded in the transaction journal
    customer_id: string; // A unique identifier assigned to the customer involved in the transaction (within one bank)
    date: string; // The exact date when the transaction was successfully executed (format: day/month/yyyy)
    time: string; // The exact time when the transaction was successfully executed (format: hh:mm:ss)
    amount_orig: string; // The monetary value of the transaction in original currency, showing how much money was transferred
    amount_eur: string; // The monetary value of the transaction in EUR currency, showing how much money was transferred
    currency: string; // The original currency in which the transaction was conducted (3-letter currency code, e.g., EUR, USD, SEK)
    dc: string; // Specifies whether the transaction was a Debit (D) or Credit (C)
    source_account: string; // The account number (IBAN) from which the money was sent or withdrawn
    destination_account?: string; // The account number (IBAN) to which the money was sent (only if counterparty is also a X Bank customer)
    transaction_scope: string; // Whether the transaction was Intrabank (I), Domestic (D), or Foreign (F)
    channel_type: string; // Platform through which the transaction was initiated (e.g., Mobile App, POS, Branch)
    channel_code: number; // Channel code of the platform through which the transaction was initiated
    description: string; // Description of the transaction (e.g., POS Purchase - Grocery Store)
    fee_f: number; // Whether the transaction is a fee or not (1 - fee transaction, 0 - not)
    fee_type: string; // Type of fee (e.g., SHA, OUR, BEN)
    participant_name: string; // Name of the Business customer or full name of the private customer sending or receiving the funds
    psn_code: string; // Personal/Registry Code of the customer
    customer_type: string; // Customer Segment: Private, Corporation, or Small and Medium-sized Enterprise (SME)
    postal_code: number; // Postal code of the customer's location
    country: string; // Country of residence of the customer
    customer_f: number; // Counterparty customer X Bank flag (1 - belongs to X Bank, 0 - not)
    c_customer_id?: string; // Unique identifier assigned to the counterparty customer (only if counterparty is also X Bank customer)
    c_participant_name?: string; // Counterparty customer name (only if counterparty is also X Bank customer)
    c_participant_country?: string; // Counterparty customer country (only if counterparty is also X Bank customer)
    c_participant_address?: string; // Counterparty customer address (only if counterparty is also X Bank customer)
    c_bank_name?: string; // Counterparty customer bank name (only if counterparty is also X Bank customer)
    c_bank_country?: string; // Counterparty customer bank country (only if counterparty is also X Bank customer)
    c_bank_country_high_risk?: number; // Counterparty customer bank country high risk flag (1 - high risk, 0 - not)
    c_participant_bank_bic_code?: string; // Counterparty customer bank BIC code (only if counterparty is also X Bank customer)
    c_participant_regnum?: string; // Counterparty customer registration number (only if counterparty is also X Bank customer)
    c_participant_f?: number; // Counterparty customer X Bank flag (1 - belongs to X Bank, 0 - not)
    same_participant_group_id_f: number; // Same customer group ID flag (1 - both belong to the same group, 0 - not)
    private_corp_sme?: string; // Whether the counterparty is Private, Corporation, or SME
    is_rvrs_f: number; // Whether the transaction is reversing an earlier transaction (1 - reversing, 0 - not)
    is_rvrs_orig_jh: string; // Journal number of the transaction that is being reversed
    jh_bra: number; // Journal Branch Code - the branch where the transaction got processed
    jh_bra_nm: string; // Journal Branch Name - the name of the branch where the transaction was processed
    jh_who: string; // Journal Employee Code - unique identifier for the employee who processed the transaction
    jh_who_name: string; // Journal Employee Name - name of the employee who processed the transaction
    jh_tp: number; // Operation Code
    jh_tp_nm: string; // Operation Code Name
}

