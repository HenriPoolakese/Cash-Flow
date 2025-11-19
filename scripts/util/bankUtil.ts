import {Bank, CountryCode} from "../types";
import {getRandomFromList, getRandomIntWithLength, getRandomNumericString} from "./randomUtil";

const banksByCountry: { [key: string]: Bank[] } = {
    SWE: [
        {
            bankName: 'Swedbank',
            country: 'SWE',
            address: 'Kungsgatan 18, Stockholm, Sweden',
            bankCode: '8000',
            branchCode: '8000', // Swedbank's clearing number
            branchName: 'Stockholm Central Branch',
            bicCode: 'SWEDSESS'
        },
        {
            bankName: 'SEB',
            country: 'SWE',
            address: 'Kungsträdgårdsgatan 8, Stockholm, Sweden',
            bankCode: '5000',
            branchCode: '5000', // SEB's clearing number
            branchName: 'Stockholm South Branch',
            bicCode: 'ESSESESS'
        }
    ],
    GER: [
        {
            bankName: 'Deutsche Bank',
            country: 'GER',
            address: 'Taunusanlage 12, Frankfurt, Germany',
            bankCode: '10070000', // Bankleitzahl (BLZ)
            branchCode: getRandomNumericString(4),
            branchName: 'Frankfurt Financial District Branch',
            bicCode: 'DEUTDEFF'
        },
        {
            bankName: 'Commerzbank',
            country: 'GER',
            address: 'Kaiserstraße 16, Frankfurt, Germany',
            bankCode: '10040000',
            branchCode: getRandomNumericString(4),
            branchName: 'Frankfurt Branch',
            bicCode: 'COBADEFF'
        }
    ],
    EST: [
        {
            bankName: 'Swedbank',
            country: 'EST',
            address: 'Liivalaia 8, Tallinn, Estonia',
            bankCode: '2200',
            branchCode: '2200',
            branchName: 'Tallinn Main Branch',
            bicCode: 'HABAEE2X'
        },
        {
            bankName: 'SEB',
            country: 'EST',
            address: 'Tornimäe 2, Tallinn, Estonia',
            bankCode: '3300',
            branchCode: '3300',
            branchName: 'Tallinn City Center Branch',
            bicCode: 'EEUHEE2X'
        },
        {
            bankName: 'LHV Pank',
            country: 'EST',
            address: 'Tartu mnt 2, Tallinn, Estonia',
            bankCode: '7700',
            branchCode:  '7700',
            branchName: 'Tallinn Main Branch',
            bicCode: 'LHVBEE22'
        },
        {
            bankName: 'Coop Pank',
            country: 'EST',
            address: 'Suur-Sõjamäe 1, Tallinn, Estonia',
            bankCode: '4200',
            branchCode: '4200',
            branchName: 'Tallinn Branch',
            bicCode: 'EKRDEE22'
        },
        {
            bankName: 'Luminor Bank',
            country: 'EST',
            address: 'Liivalaia 45, Tallinn, Estonia',
            bankCode: '9600',
            branchCode: '9600',
            branchName: 'Tallinn Branch',
            bicCode: 'RIKOEE22'
        }
    ],
    POL: [
        {
            bankName: 'PKO Bank Polski',
            country: 'POL',
            address: 'ul. Puławska 15, Warsaw, Poland',
            bankCode: '1020',
            branchCode: getRandomNumericString(4),
            branchName: 'Warsaw Main Branch',
            bicCode: 'BPKOPLPW'
        },
        {
            bankName: 'Bank Pekao',
            country: 'POL',
            address: 'Grzybowska 53/57, Warsaw, Poland',
            bankCode: '1240',
            branchCode: getRandomNumericString(4),
            branchName: 'Warsaw Branch',
            bicCode: 'PKOPPLPW'
        }
    ],
    NOR: [
        {
            bankName: 'DNB',
            country: 'NOR',
            address: 'Dronning Eufemias gate 30, Oslo, Norway',
            bankCode: '2000',
            branchCode: '2000', // DNB bank code
            branchName: 'Oslo Central Branch',
            bicCode: 'DNBANOKK'
        },
        {
            bankName: 'SpareBank 1',
            country: 'NOR',
            address: 'Storgata 3, Oslo, Norway',
            bankCode: '4000',
            branchCode: '4000', // SpareBank 1 code
            branchName: 'Oslo Branch',
            bicCode: 'SPARNO22'
        }
    ],
    GBR: [
        {
            bankName: 'Barclays',
            country: 'GBR',
            address: '1 Churchill Place, London, United Kingdom',
            bankCode: '203253',
            branchCode: getRandomNumericString(4),
            branchName: 'London Main Branch',
            bicCode: 'BARCGB22'
        },
        {
            bankName: 'HSBC',
            country: 'GBR',
            address: '8 Canada Square, London, United Kingdom',
            bankCode: '400515',
            branchCode: getRandomNumericString(4),
            branchName: 'London Central Branch',
            bicCode: 'MIDLGB22'
        }
    ]
};

export function getBankByCountry(country: CountryCode) {
    return getRandomFromList(banksByCountry[country]);
}

export function getSEBLocalBranch() {
    return  banksByCountry['EST'].find(bank => bank.bankName === 'SEB')!;
}