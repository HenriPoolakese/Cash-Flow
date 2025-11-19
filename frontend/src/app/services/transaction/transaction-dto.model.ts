import {Counterparty} from "../../services/filter/counterparty-store.service";

// Internal transfer summaries
export interface InternalTransactionSummaryDTO {
  source: string; // Source account (IBAN)
  target: string; // Target account (IBAN)
  sourceName: string; // Name of the source entity
  targetName: string; // Name of the target entity
  label: string | null; // Optional label for the transaction
  amounts: AmountDetailsDTO; // Detailed amounts
  avgAmounts: AverageAmountDetailsDTO; // Averaged amounts
  currency: string; // Currency of the transaction
  count: number; // Number of transactions
  earliestDate: string; // Earliest transaction date (ISO 8601 format)
  latestDate: string; // Latest transaction date (ISO 8601 format)
}

export interface AmountDetailsDTO {
  totalOutgoing: number; // Total outgoing amount
  netFlow: number; // Net flow of the transaction
  totalIncoming: number; // Total incoming amount
}

export interface AverageAmountDetailsDTO {
  averageIncoming: number; // Average incoming amount
  averageNetFlow: number; // Average net flow
  averageOutgoing: number; // Average outgoing amount
}

export interface InternalTransfersDTO {
  ownerNodes: OwnerNode[];
  ownerAndAccountLink: OwnerAccountLink[];
  accountNodes: AccountNode[];
  accountLinks: AccountLink[];
  netFlowLinks: NetFlowLink[];
}

// Define each section of the structure

export interface OwnerNode {
  customer_id: string;
  customer_name: string;
  country: string;
}

export interface OwnerAccountLink {
  source: string;   // Customer ID of the owner
  target: string;   // IBAN of the account
  label: string | null;
  currency: string | null;
}

export interface AccountNode {
  customer_id: string;
  customer_name: string;
  customer_type: string;
  customer_iban: string;
  label: string;
  country: string;
  currency: string;
}

export interface AccountLink {
  source: string;   // IBAN of the source account
  target: string;   // IBAN of the target account
  label: string;
  amount: number;
  amountK: number;
  amountM: number;
  average: number;
  averageK: number;
  averageM: number;
  median: number;
  medianK: number;
  medianM: number;
  currency: string;
  transactionCount: number;
  earliestDate: string;
  latestDate: string;
}

export interface NetFlowLink {
  sourceAccountId: string;   // IBAN of the source account
  targetAccountId: string;   // IBAN of the target account
  flowAmountToSource: number;
  flowAmountToTarget: number;
  netFlow: number;
  amountK: number;
  amountM: number;
  average: number;
  averageK: number;
  averageM: number;
  median: number;
  medianK: number;
  medianM: number;
  currency: string;
}


// Define the main DTO for the external data structure
export interface ExternalTransfersDTO {
  nodes: ExternalNode[];
  groupLinks: GroupLink[];
  netflowLinks: TransactionLink[];
  debitLinks: TransactionLink[];
  creditLinks: TransactionLink[];
  netflowTable: TransactionLink[];
  topDebitCounterparties: Counterparty[];
  topCreditCounterparties: Counterparty[];
}

export interface CounterPartyAccountsDTO {
  nodes: ExternalNode[];
  groupLinks: GroupLink[];
  bankLinks: BankLink[];
  netflowLinks: TransactionLink[];
  debitLinks: TransactionLink[];
  creditLinks: TransactionLink[];
  netflowTable: TransactionLink[];
}

// Define each section of the structure

// Represents each node (company, account, bank or group)
export interface ExternalNode {
  id: number;
  name: string;
  country: string | null;
  type: 'company' | 'account' | 'group' | 'Bank';
  iban: string | null;
  currency: string | null;
}

// Represents group relationships between nodes
export interface GroupLink {
  source: string;   // ID of the source node, e.g., group ID
  target: string;   // ID of the target node
  label: string;
  currency: string;
}

export interface Amounts {
  original: number;
  millions: number;
  thousands: number;
};

// Represents debit or credit transaction links
export interface TransactionLink {
  source: string;   // ID of the source node
  target: string;   // ID of the target node
  label: string;
  currency: string;
  amounts: Amounts,
  avgAmounts: Amounts
  medianAmounts: Amounts
  sebCustomerAccount: string;
  count: number;
  earliestDate: string;
  latestDate: string;
}

export interface BankLink {
  source: string;
  target: string;
  sourceName: string;
  targetName: string;
  sebCustomerAccount: string;
  counterpartyAccount: string;
  label: string;
  amounts: Amounts;
  avgAmounts: Amounts;
  medianAmounts: Amounts;
  currency: string;
  count: number;
  earliestDate: string;
  latestDate: string;
}
