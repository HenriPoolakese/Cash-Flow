// Function to get a transaction description based on dc and participant types
import {getRandomFromList, getRandomInt} from "./randomUtil";
import {CustomerType} from "../types";

export function getTransactionDescription(dc: string, srcCustomerType: CustomerType, destCustomerType?: CustomerType): string {
    if (dc === 'D') {
        // Debit transaction (money out from source customer)
        if (srcCustomerType === 'Private') {
            // Private customer spending money
            return getRandomFromList([
                'POS Purchase - Grocery Store',
                'Online Payment - Electronics',
                'ATM Withdrawal',
                'Direct Debit - Utility Bill',
                'Online Subscription Payment',
                'Loan Repayment',
                'Transfer to Friend/Family',
                'Rent Payment',
            ]);
        } else if (srcCustomerType === 'Corp' || srcCustomerType === 'SME') {
            // Business customer making payments
            if (destCustomerType === 'Private') {
                // Payments to individuals
                return getRandomFromList([
                    'Salary Payment',
                    'Contractor Payment',
                    'Employee Expense Reimbursement',
                ]);
            } else if (destCustomerType === 'Corp' || destCustomerType === 'SME') {
                // Payments to other businesses
                return getRandomFromList([
                    'Supplier Payment',
                    'Service Fee Payment',
                    'Invoice Payment',
                ]);
            } else {
                // No counterparty (e.g., tax or utility payments)
                return getRandomFromList([
                    'Tax Payment',
                    'Utility Bill Payment',
                    'Loan Repayment',
                ]);
            }
        }
    } else if (dc === 'C') {
        // Credit transaction (money into source customer)
        if (srcCustomerType === 'Private') {
            // Private customer receiving money
            if (destCustomerType === 'Corp' || destCustomerType === 'SME') {
                // Receiving from businesses
                return getRandomFromList([
                    'Salary Payment',
                    'Expense Reimbursement',
                    'Refund - Retail Purchase',
                ]);
            } else if (destCustomerType === 'Private') {
                // Receiving from individuals
                return getRandomFromList([
                    'Received Transfer',
                    'Gift Received',
                ]);
            } else {
                // No counterparty (e.g., cash deposit)
                return getRandomFromList([
                    'Cash Deposit',
                    'Interest Income',
                ]);
            }
        } else if (srcCustomerType === 'Corp' || srcCustomerType === 'SME') {
            // Business customer receiving money
            if (destCustomerType === 'Private') {
                // Receiving from individuals
                return getRandomFromList([
                    'Customer Payment',
                    'Sale Revenue',
                ]);
            } else if (destCustomerType === 'Corp' || destCustomerType === 'SME') {
                // Receiving from other businesses
                return getRandomFromList([
                    'Invoice Payment Received',
                    'Loan Received',
                    'Investment Income',
                ]);
            } else {
                // No counterparty
                return getRandomFromList([
                    'Capital Injection',
                    'Grant Received',
                    'Interest Income',
                ]);
            }
        }
    }

    // Default description if none of the above conditions are met
    return 'General Transaction';
}


const operationCodeMap: { [description: string]: number } = {
    'POS Purchase - Grocery Store': 1001,
    'Online Payment - Electronics': 1002,
    'ATM Withdrawal': 1003,
    'Direct Debit - Utility Bill': 1004,
    'Online Subscription Payment': 1005,
    'Loan Repayment': 1006,
    'Transfer to Friend/Family': 1007,
    'Rent Payment': 1008,
    'Supplier Payment': 2001,
    'Payroll Disbursement': 2002,
    'Tax Payment': 2003,
    'Utility Bill Payment': 2004,
    'Office Supplies Purchase': 2005,
    'Salary Payment': 3001,
    'Contractor Payment': 3002,
    'Employee Expense Reimbursement': 3003,
    'Customer Payment': 4001,
    'Sale Revenue': 4002,
    'Invoice Payment': 4003,
    'Invoice Payment Received': 4004,
    'Loan Received': 4005,
    'Investment Income': 4006,
    'Capital Injection': 5001,
    'Grant Received': 5002,
    'Interest Income': 5003,
    'Refund - Retail Purchase': 6001,
    'Received Transfer': 6002,
    'Gift Received': 6003,
};

// Function to get operation code based on description
export function getOperationCode(description: string): number {
    return operationCodeMap[description] || getRandomInt(1000, 9999);
}


// Determine channelType based on the transaction description
export function getChannelType(description: string): string {
    const mapping: { [key: string]: string[] } = {
        'POS Purchase - Grocery Store': ['POS'],
        'Online Payment - Electronics': ['Internet Bank', 'Mobile App'],
        'ATM Withdrawal': ['ATM'],
        'Direct Debit - Utility Bill': ['Internet Bank'],
        'Loan Repayment': ['Internet Bank', 'Branch'],
        'Online Subscription Payment': ['Internet Bank', 'Mobile App'],
        'Transfer to Friend/Family': ['Mobile App', 'Internet Bank'],
        'Rent Payment': ['Internet Bank', 'Branch'],
        'Supplier Payment': ['Internet Bank', 'Branch'],
        'Tax Payment': ['Internet Bank', 'Branch'],
        'Utility Bill Payment': ['Internet Bank', 'Mobile App'],
        'Office Supplies Purchase': ['POS', 'Internet Bank'],
        'Salary Payment': ['Payroll System'],
        'Contractor Payment': ['Internet Bank', 'Branch'],
        'Employee Expense Reimbursement': ['Internet Bank', 'Branch'],
        'Customer Payment': ['POS', 'Internet Bank'],
        'Sale Revenue': ['POS', 'Internet Bank'],
        'Invoice Payment': ['Internet Bank', 'Branch'],
        'Invoice Payment Received': ['Internet Bank', 'Branch'],
        'Loan Received': ['Branch'],
        'Investment Income': ['Branch'],
        'Capital Injection': ['Branch'],
        'Grant Received': ['Branch'],
        'Interest Income': ['Branch'],
        'Refund - Retail Purchase': ['POS', 'Internet Bank'],
        'Received Transfer': ['Mobile App', 'Internet Bank'],
        'Gift Received': ['Mobile App', 'Internet Bank'],
    };

    const possibleChannels = mapping[description] || ['Internet Bank', 'Mobile App', 'POS', 'Branch'];
    return getRandomFromList(possibleChannels);
}

export const feeTransactionDescriptions = [
    'ATM Withdrawal',
    'International Transfer Fee',
    'Overdraft Fee',
    'Late Payment Fee',
    'Loan Processing Fee',
];

export function isFeeTransaction(description: string) {
    return feeTransactionDescriptions.includes(description);
}

export function translateDescriptionToEstonian(description: string): string {
    const descriptionTranslations: { [key: string]: string } = {
        'POS Purchase - Grocery Store': 'Kaardimakse - Toidupood',
        'Online Payment - Electronics': 'Veebimakse - Elektroonika',
        'ATM Withdrawal': 'Sularaha väljavõtmine',
        'Direct Debit - Utility Bill': 'Otsedebiteering - Kommunaalarve',
        'Loan Repayment': 'Laenu tagasimakse',
        'Online Subscription Payment': 'Veebitellimuse kuumakse',
        'Transfer to Friend/Family': 'Ülekanne',
        'Rent Payment': 'Üürimakse',
        'Supplier Payment': 'Makse tarnijale',
        'Tax Payment': 'Maksumakse',
        'Utility Bill Payment': 'Kommunaalmakse',
        'Office Supplies Purchase': 'Kontoritarvete ost',
        'Salary Payment': 'Palk',
        'Contractor Payment': 'Makse alltöövõtjale',
        'Employee Expense Reimbursement': 'Töötaja kulude hüvitamine',
        'Customer Payment': 'Kliendi makse',
        'Sale Revenue': 'Müügikäive',
        'Invoice Payment': 'Arve tasu',
        'Invoice Payment Received': 'Arve laekumine',
        'Loan Received': 'Saadud laen',
        'Investment Income': 'Investeerimistulu',
        'Capital Injection': 'Kapitali sissemakse',
        'Grant Received': 'Saadud toetus',
        'Interest Income': 'Intressitulu',
        'Refund - Retail Purchase': 'Tagasimakse - Jaekaubandus',
        'Received Transfer': 'Ülekanne',
        'Gift Received': 'Kingitus',
    };
    return descriptionTranslations[description] || description;
}