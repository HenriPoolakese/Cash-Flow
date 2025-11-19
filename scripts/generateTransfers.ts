import inquirer from 'inquirer';
import {writeFileSync} from 'fs';
import {json2csv} from 'json-2-csv';
import {Bank, CountryCode, Customer, CustomerType, Transfer} from './types';
import {
    getRandomDate,
    getRandomFromList,
    getRandomInt,
    getRandomInt10,
    getRandomIntWithLength,
    getRandomNumericString,
    getRandomTime,
    getWeightedRandomFromList
} from "./util/randomUtil";
import {convertToEuro, getWeightedRandomCurrency, getWeightedRandomCurrencyByCountry} from "./util/currencyUtil";
import {getParticipantData} from "./util/nameUtils";
import {getBankByCountry, getSEBLocalBranch} from "./util/bankUtil";
import {
    getChannelType,
    getOperationCode,
    getTransactionDescription,
    isFeeTransaction,
    translateDescriptionToEstonian
} from "./util/transactionUtil";


const channelTypes = ['Mobile App', 'Internet Bank', 'POS', 'Branch'];


const countryCodeMap: { [key: string]: string } = {
    EST: 'EE',
    POL: 'PL',
    SWE: 'SE',
    GER: 'DE',
    FRA: 'FR',
    GBR: 'GB',
    NLD: 'NL',
    NOR: 'NO'
};

// Function to convert 3-letter country code to 2-letter country code
function convertToTwoLetterCountryCode(threeLetterCode: CountryCode): string {
    const twoLetterCode = countryCodeMap[threeLetterCode.toUpperCase()];
    if (!twoLetterCode) {
        throw new Error(`Unsupported 3-letter country code: ${threeLetterCode}`);
    }
    return twoLetterCode;
}

// IBAN generator function that accepts a BankData object
function generateIBAN(bankData: Bank): string {

    // Map of country codes to IBAN lengths
    const ibanLengths: { [key: string]: number } = {
        EE: 20,  // Estonia
        PL: 28,  // Poland
        SE: 24,  // Sweden
        DE: 22,  // Germany
        FR: 27,  // France
        GB: 22,  // United Kingdom
        NL: 18,  // Netherlands
        NO: 15   // Norway
    };

    const twoLetterCountryCode = convertToTwoLetterCountryCode(bankData.country);  // Convert to 2-letter code
    const length = ibanLengths[twoLetterCountryCode];
    if (!length) {
        throw new Error(`Unsupported country code: ${twoLetterCountryCode}`);
    }

    const checkDigits = getRandomNumericString(2);  // Random 2 check digits

    // Calculate the remaining length for the account number
    const remainingLength = length - (2 + bankData.bankCode.length + bankData.branchCode.length + checkDigits.length + twoLetterCountryCode.length);
    const accountNumber = getRandomNumericString(remainingLength);  // Account number to fill remaining space

    return `${twoLetterCountryCode}${checkDigits}${bankData.bankCode}${bankData.branchCode}${accountNumber}`;
}

function generatePsnCode(countryCode: string, customerType: string): string {
    if (countryCode === 'EST') {
        if (customerType === 'Private') {
            return generateEstonianPersonalCode();  // Generate personal code for individuals
        } else {
            return generateEstonianRegistryCode();  // Generate registry code for corporations/SMEs
        }
    }
    return getRandomNumericString(11);  // Fallback for other countries (use generic 11-digit code)
}

// Function to generate Estonian personal code (Isikukood)
function generateEstonianPersonalCode(): string {
    const centuryAndGender = getRandomInt(3, 6);  // Randomly pick a value for century and gender (1-6)

    // Generate a valid birth date (YYMMDD)
    const year = getRandomInt(50, 99);  // Pick a year (1950-1999 for example)
    const month = getRandomInt(1, 12).toString().padStart(2, '0');  // Month as 2 digits
    const day = getRandomInt(1, 28).toString().padStart(2, '0');  // Day as 2 digits (simplified, 1-28)

    const birthdate = `${year}${month}${day}`;
    const sequence = getRandomInt(0, 999).toString().padStart(3, '0');  // Sequence number as 3 digits
    const controlDigit = getRandomInt(0, 9);  // Control digit as a single digit

    return `${centuryAndGender}${birthdate}${sequence}${controlDigit}`;
}

// Function to generate Estonian registry code (8-digit)
function generateEstonianRegistryCode(): string {
    return getRandomIntWithLength(8).toString();  // Estonian registry codes are 8 digits long
}

function getWeightedRandomCountry(): CountryCode {
    const countries = [
        { item: 'EST', weight: 70 },   // Heavily weigh Estonia even more
        { item: 'SWE', weight: 8 },
        { item: 'GER', weight: 7 },
        { item: 'POL', weight: 5 },
        { item: 'NOR', weight: 5 },
        { item: 'GBR', weight: 5 }
    ];

    return getWeightedRandomFromList(countries) as CountryCode;
}

function generateTransfer(src: Customer, destCustomers: Customer[]): Transfer {
    const dc = getRandomFromList(['D', 'C']);

    // Select a destination customer
    let dest: Customer | null = null;


    // Determine possible destination customers based on dc and srcCustomerType
    if (dc === 'D' && src.customer_type === 'Private') {
        // Private customer spending money; may not always have a counterparty
        dest = getRandomFromList(destCustomers);
    } else if (dc === 'C' && src.customer_type === 'Private') {
        // Private customer receiving money; may have a counterparty
        dest = getRandomFromList(destCustomers);

    } else if ((src.customer_type === 'Corp' || src.customer_type === 'SME')) {
        // Business customers; likely to have a counterparty
        dest = getRandomFromList(destCustomers);
    }

    const description = getTransactionDescription(dc, src.customer_type, dest?.customer_type);

    // Check if the description indicates no counterparty

    // For transactions without a counterparty (e.g., ATM Withdrawal, Cash Deposit)
    let hasCounterparty = true;
    const noCounterpartyDescriptions = ['ATM Withdrawal', 'Cash Deposit', 'Tax Payment', 'Interest Income', 'Capital Injection'];
    if (noCounterpartyDescriptions.includes(description)) {
        hasCounterparty = false;
        dest = null;
    }

    const currency = getWeightedRandomCurrencyByCountry(dest?.country || src.country);
    const amountOrig = (Math.random() * 10000);

    const isCounterpartySameBank = dest && dest.bank.bankName === src.bank.bankName;
    const isSameCountry = dest && dest.bank.country === src.bank.country;

    // Determine transaction scope based on source and destination
    let transactionScope = 'I'; // Default to 'I' (Intrabank)

    if (hasCounterparty) {
        if (isCounterpartySameBank) {
            transactionScope = 'I'; // Intrabank transfer
        } else if (src.country === dest?.country) {
            transactionScope = 'D'; // Domestic transfer
        } else {
            transactionScope = 'F'; // Foreign transfer
        }
    } else {
        transactionScope = 'I'; // Transactions without a counterparty default to Intrabank
    }

    const bothPartiesEstonian = src.country === 'EST' && (!dest || dest.country === 'EST');


    return {
        transaction_id: getRandomInt10(),
        jh_jh: String(getRandomInt10()),
        customer_id: src.customer_id,
        date: getRandomDate(),
        time: getRandomTime(),
        amount_orig: amountOrig.toFixed(2),
        amount_eur: convertToEuro(currency, amountOrig).toFixed(2),
        currency,
        dc,
        source_account: src.account,
        destination_account: hasCounterparty && isCounterpartySameBank && dest ? dest.account : '',
        transaction_scope: transactionScope,
        channel_type: getChannelType(description),
        channel_code: getRandomInt(1, 99),
        description: bothPartiesEstonian ? translateDescriptionToEstonian(description) : description,
        fee_f: isFeeTransaction(description) ? 1 : 0,
        fee_type: isFeeTransaction(description)  ? getRandomFromList(['SHA', 'OUR', 'BEN']) : '',
        participant_name: src.participant_name,
        psn_code: src.psn_code,
        customer_type: src.customer_type,
        postal_code: src.postal_code,
        country: src.country,
        customer_f: isCounterpartySameBank ? 1 : 0,
        same_participant_group_id_f: isCounterpartySameBank ? 1 : 0,
        private_corp_sme: dest?.customer_type ?? '',
        is_rvrs_f: getRandomInt(0, 1),
        is_rvrs_orig_jh: getRandomIntWithLength(7).toString(),
        jh_bra: parseInt(src.bank.branchCode),
        jh_bra_nm: src.bank.branchName,
        jh_who: String(getRandomInt10()),
        jh_who_name: getParticipantData(src.country, src.customer_type),
        jh_tp: getOperationCode(description),
        jh_tp_nm: description,
        // Only include c_ fields if there is a counterparty
        ...(hasCounterparty && isCounterpartySameBank && dest && {
            c_customer_id: dest.customer_id,
            c_participant_name: dest.participant_name,
            c_participant_country: dest.country,
            c_participant_address: dest.bank.address,
            c_bank_name: dest.bank.bankName,
            c_bank_country: dest.bank.country,
            c_bank_country_high_risk: getRandomInt(0, 1),
            c_participant_bank_bic_code: dest.bank.bicCode, // Use BIC code from bank info
            c_participant_regnum: dest.psn_code,
            c_participant_f: isCounterpartySameBank && isSameCountry ? 1 : 0,
        }),
    };
}

// Function to generate transfers between companies
function generateTransfers(rowCount: number, sourceCustomer: Customer, destCustomers: Customer[]): Transfer[] {
    const transfers: Transfer[] = [];

    for (let i = 0; i < rowCount; i++) {
        let transfer = generateTransfer(sourceCustomer, destCustomers);
        console.log(JSON.stringify(transfer, null, 2));
        transfers.push(transfer);
    }

    return transfers;
}

// Function to generate source customer data
function generateSourceCustomerData(answers: any): Customer {
    const bankData = getSEBLocalBranch();
    return {
        customer_id: String(getRandomIntWithLength(15)),
        participant_name: answers.participant_name,
        psn_code: answers.psn_code,
        customer_type: answers.customer_type,
        postal_code: getRandomIntWithLength(5),
        country: 'EST',
        account: generateIBAN(bankData),
        bank: bankData,
    };
}

// Function to generate destination customers
function generateDestinationCustomers(rowCount: number): Customer[] {
    const numDestCustomers = rowCount / 3;
    const destCustomers: Customer[] = [];

    for (let i = 0; i < numDestCustomers; i++) {
        const customer_type = getRandomFromList(['Private', 'Corp', 'SME']) as CustomerType;
        const country = getWeightedRandomCountry();
        const bankData = getBankByCountry(country);

        destCustomers.push({
            customer_id: String(getRandomIntWithLength(15)),
            participant_name: getParticipantData(country, customer_type),
            psn_code: generatePsnCode(country, customer_type),
            customer_type,
            postal_code: getRandomInt(10000, 99999),
            country,
            account: generateIBAN(bankData),
            bank: bankData,
        });
    }

    return destCustomers;
}


(async () => {
    // Step 1: Ask for user inputs
    const answers = await inquirer.prompt([
        {
            type: 'input',
            name: 'rows',
            message: 'How many rows of data do you want to generate?',
            default: '100',
            validate: input => {
                const valid = !isNaN(parseInt(input));
                return valid || 'Please enter a number';
            }
        },
        {
            type: 'input',
            name: 'outputFile',
            message: 'What should be the name of the output CSV file?',
            default: 'transfers.csv',
        },
        {
            type: 'list',
            name: 'customer_type',
            message: 'Select the customer type:',
            choices: ['Private', 'SME', 'Corp'],
            default: 'SME',
        },
        {
            type: 'input',
            name: 'psn_code',
            message: 'Enter the reg/personal code of the source customer:',
            default: (prevAnswers: any) => generatePsnCode('EST', prevAnswers.customer_type),
        },
        {
            type: 'input',
            name: 'participant_name',
            message: 'Enter the name of the source customer:',
            default: (prevAnswers: any) => getParticipantData('EST', prevAnswers.customer_type),
        },
    ]);

    // Generate source customer data

    const rowCount = parseInt(answers.rows);
    const sourceCustomer = generateSourceCustomerData(answers);
    const destCustomers = generateDestinationCustomers(rowCount);

    console.log('Generating transfers:');
    const transfers = generateTransfers(rowCount, sourceCustomer, destCustomers);

    // Step 3: Convert data to CSV
    const csv = await json2csv(transfers, { emptyFieldValue: '' });

    // Step 4: Write CSV to file
    writeFileSync('output/' + answers.outputFile, csv);

    console.log(`Generated ${rowCount} rows of transfer data and saved to ${answers.outputFile}`);
})();