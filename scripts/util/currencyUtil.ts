
// Function to get a weighted random currency
import {getWeightedRandomFromList} from "./randomUtil";

const currencyByCountry: { [key: string]: { currency: string, weight: number }[] } = {
    EST: [
        { currency: 'EUR', weight: 90 },   // Estonia primarily uses EUR
        { currency: 'USD', weight: 5 },
        { currency: 'GBP', weight: 5 }
    ],
    SWE: [
        { currency: 'SEK', weight: 80 },   // Sweden primarily uses SEK
        { currency: 'EUR', weight: 10 },
        { currency: 'USD', weight: 5 },
        { currency: 'GBP', weight: 5 }
    ],
    GER: [
        { currency: 'EUR', weight: 90 },   // Germany uses EUR
        { currency: 'USD', weight: 5 },
        { currency: 'GBP', weight: 5 }
    ],
    POL: [
        { currency: 'PLN', weight: 80 },   // Poland uses PLN
        { currency: 'EUR', weight: 10 },
        { currency: 'USD', weight: 5 },
        { currency: 'GBP', weight: 5 }
    ],
    NOR: [
        { currency: 'NOK', weight: 85 },   // Norway uses NOK
        { currency: 'EUR', weight: 10 },
        { currency: 'USD', weight: 5 }
    ],
    GBR: [
        { currency: 'GBP', weight: 90 },   // UK uses GBP
        { currency: 'EUR', weight: 5 },
        { currency: 'USD', weight: 5 }
    ]
};

export function getWeightedRandomCurrencyByCountry(country: string): string {
    const currencies = currencyByCountry[country] || [{ currency: 'EUR', weight: 100 }];  // Default to EUR if country not found
    return getWeightedRandomFromList(currencies.map(({ currency, weight }) => ({ item: currency, weight })));
}

export function getWeightedRandomCurrency(): string {
    const currencies = [
        { item: 'EUR', weight: 50 },   // Higher weight for EUR
        { item: 'USD', weight: 15 },
        { item: 'GBP', weight: 10 },
        { item: 'NOK', weight: 10 },
        { item: 'SEK', weight: 10 },
        { item: 'PLN', weight: 5 }
    ];

    return getWeightedRandomFromList(currencies);
}

export function convertToEuro(currency: string, amount: number): number {
    const exchangeRates: Record<string, number> = {
        EUR: 1,
        SEK: 11.533, // Example rate: 11.533 SEK = 1 EUR
        NOK: 11.007, // Example rate: 11.007 NOK = 1 EUR
        USD: 1.1155,  // Example rate: 1.1155 USD = 1 EUR
        GBP: 0.83428,  // Example rate: 0.83428 GBP = 1 EUR
        PLN: 4.2710   // Example rate: 4.2710 PLN = 1 EUR
    };

    const rate = exchangeRates[currency];
    if (!rate) {
        throw new Error(`Currency ${currency} is not supported.`);
    }

    return amount / rate;
}