// nameUtils.ts

// Define lists of private, corporate, and SME names specific to each country
import {CountryCode, CustomerType} from "../types";
import {getRandomFromList} from "./randomUtil";

const estonianCompanyPrefixes = [
    'Põhjamaa', 'EneTekk', 'Metsatööstus', 'Rannakivi', 'Innovaatika', 'Tehnoloogiad', 'Tarkvara',
    'Energiakeskus', 'Puidutööd', 'Ühisvara', 'Tartu', 'Põldude', 'Linnutee', 'Kaubandus'
];

const estonianCompanySuffixes = [
    'AS', 'OÜ', 'Grupp OÜ', 'Konsultatsioonid OÜ', 'Tehnika OÜ', 'Ehitustööd OÜ', 'Tööstus OÜ', 'Arendus OÜ'
];

const estonianSMEPrefixes = [
    'Kivimaja', 'Päiksepuu', 'Õnnelik', 'Veetehnika', 'Lilleaed', 'Ranna', 'Põllu', 'Küla', 'Toidutare', 'Metsakoda',
    'Värskendus', 'Kohvik', 'KoduKaubad', 'Väike-Talu', 'Puukoda', 'Saaremaa', 'Põhjatähe'
];

const estonianSMESuffixes = [
    'OÜ', 'Käsitööd OÜ', 'Teenused OÜ', 'Kaubad OÜ', 'Kohvik OÜ', 'Lilled OÜ', 'Konsultatsioonid OÜ', 'Pagarid OÜ', 'Kaupmehed OÜ'
];

const estonianFirstNames = [
    'Kaarel', 'Anna', 'Tiina', 'Laura', 'Martin', 'Helen', 'Karl', 'Markus', 'Liina', 'Siim',
    'Jaanus', 'Maarja', 'Eva', 'Madis', 'Mari-Anne', 'Peeter', 'Kristi', 'Külli', 'Jüri', 'Hanna',
    'Riina', 'Tanel', 'Marju', 'Rene', 'Eveli', 'Toomas', 'Eero', 'Ingrid', 'Aivar', 'Tiiu'
];

const estonianLastNames = [
    'Gustavson', 'Treier', 'Tooming', 'Järv', 'Kütt', 'Maasik', 'Kuusk', 'Tõnisson', 'Kask', 'Põder', 'Tamm',
    'Saar', 'Sepp', 'Rebane', 'Lepp', 'Raud', 'Mets', 'Vaher', 'Soo', 'Õunapuu', 'Rannik', 'Oja', 'Rõõm',
    'Karu', 'Veski', 'Pärn', 'Hunt', 'Ilves'
];

// Function to generate a fictional Estonian person name
export function generateFictionalEstonianPerson(): string {
    const firstName = getRandomFromList(estonianFirstNames);
    const lastName = getRandomFromList(estonianLastNames);
    return `${firstName} ${lastName}`;
}


// Function to generate a fictional Estonian corporation name
export function generateFictionalEstonianCorporation(): string {
    const prefix = getRandomFromList(estonianCompanyPrefixes);
    const suffix = getRandomFromList(estonianCompanySuffixes);
    return `${prefix} ${suffix}`;
}

// Function to generate a fictional Estonian SME name
export function generateFictionalEstonianSME(): string {
    const prefix = getRandomFromList(estonianSMEPrefixes);
    const suffix = getRandomFromList(estonianSMESuffixes);
    return `${prefix} ${suffix}`;
}

const nameDataByCountry: Record<CountryCode, { private: string[], corp: string[], sme: string[] }> = {
    EST: {
        private: Array.from({ length: 40 }, () => generateFictionalEstonianPerson()), // Generate 20 fictional person names
        corp: Array.from({ length: 40 }, () => generateFictionalEstonianCorporation()), // Generate 20 fictional corporation names
        sme: Array.from({ length: 40 }, () => generateFictionalEstonianSME()) // Generate 20 fictional SME names
    },
    SWE: {
        private: ['Johan Lindberg', 'Emma Svensson', 'Lars Karlsson', 'Sara Johansson', 'Oskar Nyström'],
        corp: ['Ericsson AB', 'Volvo AB', 'H&M Group', 'IKEA AB', 'Scania AB'],
        sme: ['Nordic Innovations AB', 'Svenska Smide AB', 'Skogsbruk AB', 'Småland Catering AB']
    },
    GER: {
        private: ['Hans Müller', 'Anna Schmidt', 'Peter Fischer', 'Ursula Becker', 'Michael Wagner'],
        corp: ['Deutsche Telekom AG', 'Siemens AG', 'Volkswagen AG', 'Allianz SE', 'BMW AG'],
        sme: ['Bäckerei Müller GmbH', 'Kleinbauern AG', 'Lichtblick IT GmbH', 'Technikservice KG']
    },
    POL: {
        private: ['Jan Kowalski', 'Maria Nowak', 'Piotr Zielinski', 'Anna Lewandowska', 'Tomasz Wojcik'],
        corp: ['PKO Bank Polski SA', 'PGE Polska Grupa Energetyczna SA', 'Orlen SA', 'LOT Polish Airlines', 'Polska Miedź SA'],
        sme: ['Złoty Transport Sp. z o.o.', 'Warsztat Nowak Sp. z o.o.', 'Polska Meble Sp. z o.o.', 'Świeże Owoce Sp. z o.o.']
    },
    NOR: {
        private: ['Ola Nordmann', 'Kari Nordmann', 'Lars Johansen', 'Ingrid Hansen', 'Erik Olsen'],
        corp: ['Equinor ASA', 'DNB ASA', 'Telenor ASA', 'Norsk Hydro ASA', 'Yara International ASA'],
        sme: ['Fjordbygg AS', 'Laksefisk AS', 'Nordic Tech AS', 'Oslo Catering AS']
    },
    GBR: {
        private: ['John Smith', 'Emily Brown', 'George Johnson', 'Charlotte Wilson', 'James Williams'],
        corp: ['BP Plc', 'HSBC Holdings Plc', 'GlaxoSmithKline Plc', 'Tesco Plc', 'Unilever Plc'],
        sme: ['Cambridge IT Ltd.', 'Westminster Consulting Ltd.', 'Thames River Logistics Ltd.', 'Brighton Builders Ltd.']
    }
};

// Function to generate participant names based on country code and customer type
export function getParticipantData(countryCode: CountryCode, customerType: CustomerType): string {
    const countryData = nameDataByCountry[countryCode];

    if (!countryData) {
        throw new Error(`Unsupported country code: ${countryCode}`);
    }

    let nameList: string[] = [];

    switch (customerType.toLowerCase()) {
        case 'private':
            nameList = countryData.private;
            break;
        case 'corp':
            nameList = countryData.corp;
            break;
        case 'sme':
            nameList = countryData.sme;
            break;
        default:
            throw new Error(`Unsupported customer type: ${customerType}`);
    }

    return getRandomFromList(nameList);
}
