import random
import csv
import pandas as pd
from datetime import datetime, timedelta

# List of Estonian first names
est_first_names = [
    "Kristjan", "Mart", "Jaan", "Markus", "Andres", 
    "Peeter", "Arvo", "Rein", "Toomas", "Ragnar",
    "Kaarel", "Taavi", "Uku", "Madis", "Meelis", "Priit",
    "Kertu", "Maarika", "Liis", "Mari", "Kristiina", 
    "Anneli", "Kadri", "Heli", "Eve", "Silvia",
    "Piret", "Triin", "Anu", "Grete", "Leila" , "Hedi" 
]

# List of Estonian last names
est_last_names = [
    "Tamm", "Saar", "Kask", "Mägi", "Vaher", 
    "Laas", "Oja", "Kuusk", "Põld", "Veski",
    "Raud", "Tiik", "Põder", "Kivi", "Lepp",
    "Kõiv", "Nurme", "Talvik", "Pärn", "Välja"
]

eu_first_names = [
    # Northern Europe
    "Olaf", "Erik", "Björn", "Ingrid", "Astrid", "Sofia",  # Scandinavian
    "Lars", "Kari", "Johan", "Greta", "Mikkel", "Siri",    # Nordic

    # Western Europe
    "Pierre", "Jacques", "Marie", "Claire", "Julien", "Sophie",  # French
    "Liam", "Oliver", "Emily", "Charlotte", "Noah", "Grace",    # British
    "Max", "Anna", "Paul", "Johanna", "Klaus", "Greta",         # German
    "Thomas", "Elsa", "Lucas", "Amelie", "Leon", "Lena",        # Swiss

    # Southern Europe
    "Luca", "Matteo", "Giulia", "Sofia", "Marco", "Alessia",    # Italian
    "Antonio", "Jose", "Maria", "Carmen", "Miguel", "Elena",    # Spanish
    "Alexandros", "Nikolaos", "Maria", "Eleni", "Ioannis", "Sophia",  # Greek
    "Tiago", "João", "Ana", "Beatriz", "Pedro", "Marta",        # Portuguese

    # Eastern Europe
    "Ivan", "Anastasia", "Dmitri", "Olga", "Vladimir", "Maria", # Russian
    "Lukasz", "Anna", "Michal", "Katarzyna", "Piotr", "Agnieszka",  # Polish
    "Milos", "Petra", "Jana", "Jakub", "Ondrej", "Martina",     # Czech/Slovak
    "Bogdan", "Ioana", "Andrei", "Alexandra", "Mihai", "Elena", # Romanian

    # Other European Regions
    "Einar", "Ragna", "Arne", "Freya",                          # Icelandic
    "Csaba", "Janos", "Erzsébet", "Katalin", "Laszlo", "Eva",   # Hungarian
    "Bojan", "Dragana", "Milan", "Milena",                      # Balkan
]

eu_last_names = [
    # Northern Europe
    "Andersson", "Lindström", "Nielsen", "Hansen", "Johansen",  # Scandinavian
    "Kristensen", "Mikkelsen", "Sorensen", "Olofsson", "Berg",  # Nordic

    # Western Europe
    "Martin", "Bernard", "Dubois", "Durand", "Moreau",          # French
    "Smith", "Taylor", "Brown", "Wilson", "Jones",              # British
    "Müller", "Schmidt", "Schneider", "Fischer", "Weber",       # German
    "Bachmann", "Keller", "Frei", "Schmid", "Meier",            # Swiss

    # Southern Europe
    "Rossi", "Esposito", "Bianchi", "Romano", "Ferrari",        # Italian
    "Garcia", "Fernandez", "Lopez", "Martinez", "Sanchez",      # Spanish
    "Papadopoulos", "Georgiou", "Nikolaidis", "Kostas",         # Greek
    "Silva", "Santos", "Ferreira", "Pereira", "Carvalho",       # Portuguese

    # Eastern Europe
    "Ivanov", "Petrov", "Sokolov", "Kuznetsov", "Popov",        # Russian
    "Kowalski", "Nowak", "Wisniewski", "Zielinski", "Krawczyk", # Polish
    "Svoboda", "Novak", "Dvorak", "Krejci", "Kratochvil",       # Czech/Slovak
    "Popescu", "Ionescu", "Georgescu", "Marinescu", "Tudor",    # Romanian

    # Other European Regions
    "Gudmundsson", "Egilsson", "Sigurdsson", "Sveinsson",       # Icelandic
    "Nagy", "Kovács", "Tóth", "Szabó", "Varga",                 # Hungarian
    "Petrovic", "Markovic", "Jovanovic", "Nikolic",             # Balkan
]


estonian_banks = [
    ["Swedbank", "HABAEE2X"],
    ["LHV Pank", "LHVBEE22"],
    ["Coop Pank", "EHISEE22"],
    ["Luminor Bank AS", "NDEAEE2X"],
    ["Inbank AS", "INBAEE2X"],
    ["Bigbank AS", "BIGKEE2B"],
    ["TBB pank AS", "TABUEE22"]
]

foreign_banks = {
    "EST": [
    ["Swedbank", "HABAEE2X", "EE", "EST", "EUR"], # Estonia
    ["LHV Pank", "LHVBEE22", "EE", "EST", "EUR"],
    ["Coop Pank", "EHISEE22", "EE", "EST", "EUR"],
    ["Luminor Bank AS", "NDEAEE2X", "EE", "EST", "EUR"],
    ["Inbank AS", "INBAEE2X", "EE", "EST", "EUR"],
    ["Bigbank AS", "BIGKEE2B", "EE", "EST", "EUR"],
    ["TBB pank AS", "TABUEE22", "EE", "EST", "EUR"],
    ],

    "GER": [
    ["Deutsche Bank", "DEUTDEFF", "DE", "GER", "EUR"],  # Germany
    ["Commerzbank", "COBADEFF", "DE", "GER", "EUR"],    # Germany
    ["UBS Europe SE", "UBSWDEFF", "DE", "GER", "EUR"],  # Germany
    ["Credit Suisse AG", "CRESDEFF", "DE", "GER", "EUR"], # Germany
    ["Standard Chartered Bank AG", "SCBLDEFX", "DE", "GER", "EUR"], # Germany
    ["Lloyds Bank GmbH", "LOYDDEFF", "DE", "GER", "EUR"], # Germany
    ["Goldman Sachs Europe SE", "GSBEDEFF", "DE", "GER", "EUR"], # Germany
    ["J.P. Morgan AG", "CHASDEFX", "DE", "GER", "EUR"], # Germany
    ["Morgan Stanley Europe SE", "MSDLDEFF", "DE", "GER", "EUR"], # Germany
    ],

    "FRA": [
    ["BNP Paribas", "BNPAFRPP", "FR", "FRA", "EUR"],    # France
    ["Crédit Agricole", "AGRIFRPP", "FR", "FRA", "EUR"], # France
    ["Société Générale", "SOGEFRPP", "FR", "FRA", "EUR"], # France
    ["HSBC France", "CCFRFRPP", "FR", "FRA", "EUR"],    # France
    ],
    
    "NLD": [
    ["ING Bank", "INGBNL2A", "NL", "NLD", "EUR"],       # Netherlands
    ["ABN AMRO", "ABNANL2A", "NL", "NLD", "EUR"],       # Netherlands
    ["Rabobank", "RABONL2U", "NL", "NLD", "EUR"],       # Netherlands
    ["ABN AMRO Bank N.V.", "ABNANL2A", "NL", "NLD", "EUR"], # Netherlands
    ],

    "ESP": [
    ["Banco Santander", "BSCHESMM", "ES", "ESP", "EUR"],# Spain
    ["CaixaBank", "CAIXESBB", "ES", "ESP", "EUR"],      # Spain
    ["Banco Bilbao S.A.", "BBVAESMM", "ES", "ESP", "EUR"], # Spain
    ],

    "AUT": [
    ["Raiffeisen Bank", "RZBAATWW", "AT", "AUT", "EUR"], # Austria
    ["Erste Group Bank", "GIBAATWG", "AT", "AUT", "EUR"],# Austria
    ],

    "SWE": [
    ["Swedbank", "SWEDSESS", "SE", "SWE", "SEK"],       # Sweden
    ["SEB Bank", "ESSESESS", "SE", "SWE", "SEK"],       # Sweden
    ["Länsförsäkringar Bank", "EFTASESS", "SE", "SWE", "SEK"], # Sweden
    ["SEB Sweden", "ESSESESS", "SE", "SWE", "SEK"], # Sweden
    ["Handelsbanken", "HANDSESS", "SE", "SWE", "SEK"],  # Sweden
    ],

    "DNK": [
    ["Danske Bank", "DABADKKK", "DK", "DNK", "DKK"],    # Denmark
    ["Nykredit Bank", "NYKBDKKK", "DK", "DNK", "DKK"],  # Denmark
    ["OTP Bank", "OTPVHUHB", "HU", "HUN", "HUF"],       # Hungary
    ],

    "POL": [
    ["PKO Bank Polski", "BPKOPLPW", "PL", "POL", "PLN"],# Poland
    ["Bank Pekao", "PKOPPLPW", "PL", "POL", "PLN"],     # Poland
    ["ING Bank Śląski", "INGBPLPW", "PL", "POL", "PLN"],# Poland
    ["mBank", "BREXPLPW", "PL", "POL", "PLN"],          # Poland
    ["Bank Millennium", "BIGBPLPW", "PL", "POL", "PLN"],# Poland
    ],

    "ITA": [
    ["Intesa Sanpaolo", "BCITITMM", "IT", "ITA", "EUR"],# Italy
    ["Banco BPM", "BPMIITM2", "IT", "ITA", "EUR"],      # Italy
    ["UniCredit Bank", "UNCRITMM", "IT", "ITA", "EUR"], # Italy
    ],

    "IRL": [
    ["Bank of Ireland", "BOFIIE2D", "IE", "IRL", "EUR"],# Ireland
    ["Allied Irish Banks", "AIBKIE2D", "IE", "IRL", "EUR"], # Ireland
    ["Permanent TSB", "IPBSIE2D", "IE", "IRL", "EUR"],  # Ireland
    ["Barclays Bank Ireland PLC", "BARCIE2D", "IE", "IRL", "EUR"], # Ireland
    ["Citibank Europe PLC", "CITIIE2X", "IE", "IRL", "EUR"], # Ireland
    ["Bank of America Europe DAC", "BOFAIE3X", "IE", "IRL", "EUR"], # Ireland
    ["KBC Bank Ireland", "KBCIIE2D", "IE", "IRL", "EUR"], # Ireland
    ],

    "GRC": [
    ["Alpha Bank", "CRBAGRAA", "GR", "GRC", "EUR"],     # Greece
    ["Eurobank", "EFGPGRAA", "GR", "GRC", "EUR"],       # Greece
    ["National Bank of Greece", "ETHNGRAA", "GR", "GRC", "EUR"], # Greece
    ],

    "PRT": [
    ["Caixa Geral de Depósitos", "CGDIPTPL", "PT", "PRT", "EUR"], # Portugal
    ["Banco Comercial Português", "BCOMPTPL", "PT", "PRT", "EUR"], # Portugal
    ["Banco BPI", "BBPIPTPL", "PT", "PRT", "EUR"],      # Portugal
    ],

    "FIN": [
    ["Nordea Bank", "NDEAFIHH", "FI", "FIN", "EUR"],    # Finland
    ["OP Financial Group", "OKOYFIHH", "FI", "FIN", "EUR"], # Finland
    ["S-Bank", "SBANFIHH", "FI", "FIN", "EUR"],         # Finland
    ],

    "NOR": [
    ["DNB Bank", "DNBANOKK", "NO", "NOR", "NOK"],       # Norway
    ["SpareBank 1", "SPARNO22", "NO", "NOR", "NOK"],    # Norway
    ],

    "BEL": [
    ["BNP Paribas Fortis", "GEBABEBB", "BE", "BEL", "EUR"], # Belgium
    ["Belfius Bank", "GKCCBEBB", "BE", "BEL", "EUR"],   # Belgium
    ["KBC Bank", "KREDBEBB", "BE", "BEL", "EUR"],       # Belgium
    ["ING Belgium", "BBRUBEBB", "BE", "BEL", "EUR"],    # Belgium
    ],

    "LVA": [
    ["Citadele Banka", "PARXLV22", "LV", "LVA", "EUR"], # Latvia
    ["Swedbank Latvia", "HABALV22", "LV", "LVA", "EUR"],
    ["SEB Bank Latvia", "UNLALV2X", "LV", "LVA", "EUR"],
    ["Luminor Bank Latvia", "RIKOLV2X", "LV", "LVA", "EUR"],
    ["PrivatBank", "PRTTLV22", "LV", "LVA", "EUR"],
    ["BlueOrange Bank", "BLBALT22", "LV", "LVA", "EUR"],
    ["Revolut Bank Latvia", "REVOLV22", "LV", "LVA", "EUR"],
    ],

    "LTU": [
    ["Swedbank Lithuania", "HABALT22", "LT", "LTU", "EUR"], # Lithuania
    ["SEB Bank Lithuania", "CBVILT2X", "LT", "LTU", "EUR"],
    ["Luminor Bank Lithuania", "AGBLLT2X", "LT", "LTU", "EUR"],
    ["Šiaulių Bankas", "CBSBLT26", "LT", "LTU", "EUR"],
    ["Medicinos Bankas", "MDBALT22", "LT", "LTU", "EUR"],
    ["Revolut Bank Lithuania", "REVOLT21", "LT", "LTU", "EUR"],
    ["Citadele Bankas Lithuania", "PARXLT22", "LT", "LTU", "EUR"],
    ],

    # Japan Banks
    "JPN": [
    ["Mitsubishi UFJ Financial Group", "BOTKJPJT", "JP", "JPN", "JPY"], # Japan
    ["Sumitomo Mitsui Banking Corporation", "SMBCJPJT", "JP", "JPN", "JPY"],
    ["Mizuho Financial Group", "MHCBJPJT", "JP", "JPN", "JPY"],
    ["Japan Post Bank", "JPPSJPJ1", "JP", "JPN", "JPY"],
    ],

    # USA Banks
    "USA": [
    ["JPMorgan Chase", "CHASUS33", "US", "USA", "USD"],  # USA
    ["Bank of America", "BOFAUS3N", "US", "USA", "USD"],
    ["Wells Fargo", "WFBIUS6S", "US", "USA", "USD"],
    ["Citibank", "CITIUS33", "US", "USA", "USD"],
    ["Goldman Sachs", "GSCMUS33", "US", "USA", "USD"],
    ],

    # Canada Banks
    "CAN" : [
    ["Royal Bank of Canada", "ROYCCAT2", "CA", "CAN", "CAD"], # Canada
    ["Toronto-Dominion Bank", "TDOMCATTTOR", "CA", "CAN", "CAD"],
    ["Scotiabank", "NOSCCATTCOL", "CA", "CAN", "CAD"],
    ["Bank of Montreal", "BOFMCAT2", "CA", "CAN", "CAD"],
    ["Canadian Imperial Bank of Commerce", "CIBCCATT", "CA", "CAN", "CAD"]
    ]
}

# Data for company name generation by country
customer_data = {
    "CAN": {  # Canada
        "prefixes": ["Maple", "North", "True", "Atlantic", "Great"],
        "core_names": ["Tech", "Energy", "Logistics", "Finance", "Enterprises"],
    },
    "USA": {  # United States
        "prefixes": ["American", "United", "Star", "Liberty", "West"],
        "core_names": ["Industries", "Holdings", "Innovations", "Solutions", "Corporation"],
    },
    "JPN": {  # Japan
        "prefixes": ["Sakura", "Nippon", "Fuji", "Kyoto", "Hikari"],
        "core_names": ["Tech", "Manufacturing", "Electronics", "Transport", "Finance"],
    },
    "LVA": {  # Latvia
        "prefixes": ["Riga", "Baltic", "Daugava", "Latvijas", "Zemgale"],
        "core_names": ["Transport", "Logistics", "Bank", "Development", "Energy"],
    },
    "LTU": {  # Lithuania
        "prefixes": ["Vilnius", "Baltic", "Kaunas", "Klaipėda", "Lietuvos"],
        "core_names": ["Trade", "Tech", "Shipping", "Holdings", "Bank"],
    },
    "BEL": {  # Belgium
        "prefixes": ["Brussels", "Flemish", "Wallonia", "Antwerp", "Belgian"],
        "core_names": ["Finance", "Logistics", "Tech", "Energy", "Holdings"],
    },
    "GER": {  # Germany
        "prefixes": ["Deutsche", "Bavarian", "Berlin", "Rheinland", "Euro"],
        "core_names": ["Bank", "Industries", "Logistics", "Solutions", "Manufacturing"],
    },
    "NLD": {  # Netherlands
        "prefixes": ["Dutch", "Rotterdam", "Amsterdam", "Hague", "Euro"],
        "core_names": ["Logistics", "Bank", "Tech", "Development", "Trade"],
    },
    "NOR": {  # Norway
        "prefixes": ["Nordic", "Oslo", "Fjord", "Scandic", "Arctic"],
        "core_names": ["Energy", "Shipping", "Bank", "Holdings", "Tech"],
    },
    "SWE": {  # Sweden
        "prefixes": ["Swedish", "Stockholm", "Scandic", "Nordic", "Viking"],
        "core_names": ["Bank", "Tech", "Energy", "Holdings", "Shipping"],
    },
    "FIN": {  # Finland
        "prefixes": ["Helsinki", "Finnish", "Nordic", "Arctic", "Lapland"],
        "core_names": ["Bank", "Shipping", "Tech", "Energy", "Holdings"],
    },
    "DNK": {  # Denmark
        "prefixes": ["Copenhagen", "Danish", "Scandic", "Nordic", "Viking"],
        "core_names": ["Logistics", "Bank", "Tech", "Holdings", "Energy"],
    },
    "POL": {  # Poland
        "prefixes": ["Warsaw", "Polish", "Baltic", "Mazovia", "Euro"],
        "core_names": ["Logistics", "Tech", "Energy", "Finance", "Bank"],
    },
    "FRA": {  # France
        "prefixes": ["Paris", "French", "Euro", "Riviera", "Bretagne"],
        "core_names": ["Finance", "Tech", "Bank", "Logistics", "Industries"],
    },
    "PRT": {  # Portugal
        "prefixes": ["Lisbon", "Atlantic", "Iberian", "Porto", "Portuguese"],
        "core_names": ["Tech", "Bank", "Shipping", "Energy", "Logistics"],
    }
}

vendor_data = {
    "CAN": {  # Canada
        "prefixes": ["Timberline", "Northern", "Pacific", "Frontier", "Mapleleaf"],
        "core_names": ["Supplies", "Construction", "Materials", "Industrial", "Distribution"],
    },
    "USA": {  # United States
        "prefixes": ["Pioneer", "Freedom", "Summit", "Keystone", "Silverline"],
        "core_names": ["Supplies", "Manufacturing", "Tools", "Equipment", "Distribution"],
    },
    "JPN": {  # Japan
        "prefixes": ["Hokkaido", "Shinobi", "Osaka", "Samurai", "Kobe"],
        "core_names": ["Supplies", "Machinery", "Automation", "Engineering", "Distribution"],
    },
    "LVA": {  # Latvia
        "prefixes": ["Daugava", "Riga", "Baltic", "Kurzeme", "Latgale"],
        "core_names": ["Supplies", "Logistics", "Machinery", "Engineering", "Distribution"],
    },
    "LTU": {  # Lithuania
        "prefixes": ["Nemunas", "Kaunas", "Vilnius", "Baltic", "Klaipėda"],
        "core_names": ["Supplies", "Machinery", "Engineering", "Automation", "Tools"],
    },
    "BEL": {  # Belgium
        "prefixes": ["Flemish", "Antwerp", "Brussels", "Wallonia", "Belgian"],
        "core_names": ["Supplies", "Materials", "Distribution", "Machinery", "Engineering"],
    },
    "GER": {  # Germany
        "prefixes": ["Rheinland", "Bavarian", "Euro", "Berlin", "Frankfurt"],
        "core_names": ["Supplies", "Machinery", "Tools", "Industrial", "Logistics"],
    },
    "NLD": {  # Netherlands
        "prefixes": ["Rotterdam", "Amsterdam", "Hague", "Dutch", "Euro"],
        "core_names": ["Supplies", "Distribution", "Machinery", "Logistics", "Engineering"],
    },
    "NOR": {  # Norway
        "prefixes": ["Arctic", "Oslo", "Nordic", "Fjord", "Bergen"],
        "core_names": ["Energy", "Supplies", "Shipping", "Engineering", "Industrial"],
    },
    "SWE": {  # Sweden
        "prefixes": ["Stockholm", "Swedish", "Viking", "Nordic", "Gothenburg"],
        "core_names": ["Supplies", "Shipping", "Engineering", "Machinery", "Industrial"],
    },
    "FIN": {  # Finland
        "prefixes": ["Helsinki", "Finnish", "Nordic", "Arctic", "Lapland"],
        "core_names": ["Supplies", "Shipping", "Machinery", "Industrial", "Engineering"],
    },
    "DNK": {  # Denmark
        "prefixes": ["Copenhagen", "Danish", "Nordic", "Viking", "Aarhus"],
        "core_names": ["Supplies", "Materials", "Machinery", "Shipping", "Industrial"],
    },
    "POL": {  # Poland
        "prefixes": ["Warsaw", "Baltic", "Polish", "Mazovia", "Euro"],
        "core_names": ["Supplies", "Materials", "Machinery", "Industrial", "Tools"],
    },
    "FRA": {  # France
        "prefixes": ["Parisian", "French", "Euro", "Normandy", "Riviera"],
        "core_names": ["Supplies", "Materials", "Machinery", "Logistics", "Industrial"],
    },
    "PRT": {  # Portugal
        "prefixes": ["Lisbon", "Porto", "Atlantic", "Iberian", "Portuguese"],
        "core_names": ["Supplies", "Materials", "Industrial", "Machinery", "Tools"],
    },
}

# Function to generate a random Estonian name
def generate_private_name(first_names, last_names):
    first_name = random.choice(first_names)
    last_name = random.choice(last_names)
    return f"{first_name} {last_name}"

def generate_estonian_company_name(client_type):
  # Name components for customers
    customer_prefixes = [
        "Eesti", "Dorpat", "Tallinna", "Tartu", "Põhja", "Lõuna", "Harju", "Viru", 
        "Metsa", "Järve", "Võru", "Pärnu", "Edela", "Saaremaa", "Ida", "Lääne", 
        "Alutaguse", "Haanja", "Peipsi", "Soomaa", "Viljandi"
    ]
    customer_core_names = [
        "Energia", "Tehnika", "Varahaldus", "Keemia", "Transpordi", "Metall", 
        "Logistika", "Sadam", "Ehitus", "Tootmine", "Finants", "Pank", "Kinnisvara", 
        "Kaubandus", "Arendus", "Telekommunikatsioon", "Meditsiini", "Laevandus", 
        "Innovatsioon", "Autoteenindus", "Tarkvara"
    ]

    # Name components for vendors
    vendor_prefixes = [
        "Pärnu", "Saare", "Hiiumaa", "Narva", "Rakvere", "Viljandi", "Valga", 
        "Rapla", "Tamsalu", "Jõhvi", "Kuressaare", "Paide", "Kihnu", "Keila", 
        "Mustvee", "Haapsalu", "Elva", "Otepää", "Abja", "Kose"
    ]
    vendor_core_names = [
        "Puit", "Tööstus", "Kaevandused", "Hulgikaubandus", "Põllumajandus", 
        "Võrgud", "Kütus", "Elektroonika", "Külmhoone", "Masinad", "Traktorid", 
        "Mööbel", "Valgustus", "Ehitusmaterjalid", "Metallitöö", "Keemiakaubad", 
        "Toiduained", "Plastik", "Pakendid", "Energiatooted"
    ]


    # Choose the appropriate name components based on type
    if client_type == "customer":
        prefixes = customer_prefixes
        core_names = customer_core_names
    elif client_type == "vendor":
        prefixes = vendor_prefixes
        core_names = vendor_core_names
    else:
        raise ValueError("Invalid type specified. Use 'customer' or 'vendor'.")

    # Legal entity types
    entity_types = ["AS", "OÜ", "MTÜ", "TÜ", "UÜ"]

    # Combine parts to form a name
    prefix = random.choice(prefixes)
    core_name = random.choice(core_names)
    entity_type = random.choice(entity_types)

    # Construct the full name
    company_name = f"{prefix} {core_name} {entity_type}"

    return company_name


def generate_foreign_company_name(country_code=None, counterparty_type="customer"):
    if counterparty_type not in ["customer", "vendor"]:
        raise ValueError(f"Invalid counterparty type: {counterparty_type}. Must be 'customer' or 'vendor'.")

    # Select data source based on counterparty type
    data_source = customer_data if counterparty_type == "customer" else vendor_data

    # Handle random country code selection
    if country_code is None:
        country_code = random.choice(list(data_source.keys()))

    if country_code not in data_source:
        raise ValueError(f"Invalid country code: {country_code}")

    # Generate company name
    data = data_source[country_code]
    prefix = random.choice(data["prefixes"])
    core_name = random.choice(data["core_names"])

    company_name = f"{prefix} {core_name}"
    return [company_name, country_code]



# Random IBAN and data generators for other companies and private persons
def generate_random_iban(country_code="EE", length=20):
    return country_code + ''.join([str(random.randint(0, 9)) for _ in range(length - 2)])


    
def convert_to_euro(amount, currency_symbol, date, exchange_rates):
    """
    Convert an amount from a specified currency to euros (€) using the historical exchange rate
    from a preloaded exchange rates dictionary.
    
    Parameters:
    - amount (float): The amount in the original currency.
    - currency_symbol (str): The symbol of the currency (e.g., 'USD', 'GBP').
    - date (str): The date for which to find the exchange rate (format: 'YYYY-MM-DD').
    - exchange_rates (dict): Preloaded dictionary with exchange rates 
      (e.g., {date: {currency: rate}}).
    
    Returns:
    - float: The equivalent amount in euros (€), or None if the rate is not available.
    """
    try:
        # Ensure the date is in the dictionary
        if date not in exchange_rates:
            raise ValueError(f"No exchange rates found for date {date}.")
        
        # Ensure the currency is in the dictionary for the given date
        if currency_symbol not in exchange_rates[date]:
            raise ValueError(f"No exchange rate found for {currency_symbol} on {date}.")
        
        # Get the exchange rate for the given date and currency
        exchange_rate = exchange_rates[date][currency_symbol]
        
        # Convert the amount to euros
        euro_value = float(amount / exchange_rate)
        
        return round(euro_value, 2)
    except Exception as e:
        print(f"Error: {e}")
        return None




def create_private_seb_customers(amount):
    private_customers = []
    id = 100
    for _ in range(amount):
        customer_id = "000"+ str(id)
        private_customers.append(
            {"customer_id": customer_id,
             "customer_name": generate_private_name(est_first_names, est_last_names),
             "customer_type": "Private",
             "type": "Private",
             "country": "EST",
             "iban": generate_random_iban(),
             "bank_name" : "SEB Pank AS",
             "bic_code": "EEUHEE2X"})  
        id += 1  
    return private_customers


def create_private_est_counterparties(amount):
    private_customers = []
    for _ in range(amount):
        random_bank = random.choice(estonian_banks)
        private_customers.append(
            {"customer_id": None,
             "customer_name": generate_private_name(est_first_names, est_last_names),
             "type": "Private",
             "country": "EST",
             "iban": generate_random_iban(),
             "bank_name" : random_bank[0],
             "bic_code": random_bank[1]})  
    return private_customers

def create_private_foreign_counterparties(amount):
    private_customers = []
    for _ in range(amount):
        # Choose a random key (country code) from the dictionary
        country_code = random.choice(list(foreign_banks.keys()))
        # Choose a random bank from the list of banks for the selected country
        random_bank = random.choice(foreign_banks[country_code])
        private_customers.append(
            {"customer_id": None,
             "customer_name": generate_private_name(eu_first_names, eu_last_names),
             "type": "Private",
             "country": random_bank[3],
             "iban": generate_random_iban(random_bank[2]),
             "bank_name" : random_bank[0],
             "bic_code": random_bank[1],
             "bank_country": random_bank[3],
             "currency": random.choice([random_bank[4], "EUR"]) # native currency of country or EUR
             })  
    return private_customers

def create_company_est_counterparties(amount, client_type):
    companies = []
    i = 0

    for i in range(amount):
        name = generate_estonian_company_name(client_type)
        n = random.randint(1, 3) # some companies have 2 or 3 accounts
        for j in range(n):
            random_bank = random.choice(estonian_banks)
            companies.append({
                "customer_id": None,
                "customer_name": name,
                "type": "Legal",
                "country": "EST",
                "iban": generate_random_iban(),
                "bank_name": random_bank[0],
                "bic_code": random_bank[1]
            })
        i += 1
    return companies

def create_company_foreign_counterparties(amount, counterparty_type):
    companies = []
    i = 0

    for i in range(amount):
        foreign_name = generate_foreign_company_name(None, counterparty_type)
        name = foreign_name[0]
        country = foreign_name[1]
        n = random.randint(1, 3) # some companies have 2 or 3 accounts
        for j in range(n):
            random_bank = random.choice(foreign_banks[country])
            companies.append({
                "customer_id": None,
                "customer_name": name,
                "type": "Legal",
                "country": country,
                "iban": generate_random_iban(random_bank[2]),
                "bank_name": random_bank[0],
                "bic_code": random_bank[1],
                "bank_country": random_bank[3],
                "currency": random.choice([random_bank[4], "EUR"]) # native currency of country or EUR
            })
        i += 1
    return companies

def create_est_counterparties(privates, companies, client_type):
    privates = create_private_est_counterparties(privates)
    companies = create_company_est_counterparties(companies, client_type)
    return privates + companies

def create_foreign_counterparties(privates, companies, client_type):
    privates = create_private_foreign_counterparties(privates)
    companies = create_company_foreign_counterparties(companies, client_type)
    return privates + companies




def random_date(start_date, end_date):
    return start_date + timedelta(seconds=random.randint(0, int((end_date - start_date).total_seconds())))

def generate_transaction_date():
    start_date = datetime(2023, 1, 1)
    end_date = datetime(2024, 11, 29)
    return random_date(start_date, end_date)

def generate_random_amount():
    return round(random.uniform(10, 5000), 2)

def combine_customers(seb_companies, seb_private_customers):
    # Keys to retain
    keys_to_keep = {'customer_id', 'customer_name', 'customer_type'}
    
    # Extract only the desired keys from both lists
    combined_list = [
        {key: customer[key] for key in keys_to_keep} 
        for customer in seb_companies + seb_private_customers
    ]
    
    return combined_list


def load_exchange_rates(csv_file):
    """
    Load exchange rates from a CSV file into a Python dictionary.
    
    Parameters:
    - csv_file (str): Path to the CSV file containing exchange rates.
    
    Returns:
    - dict: A nested dictionary with dates as keys and another dictionary 
            for currency and rates (e.g., rates[date][currency] = rate).
    """
    rates = {}
    with open(csv_file, mode='r') as file:
        reader = csv.DictReader(file)
        for row in reader:
            date = row['date']
            currency = row['currency']
            rate = float(row['rate'])
            
            # Initialize date key if not present
            if date not in rates:
                rates[date] = {}
            
            # Add currency rate
            rates[date][currency] = rate
    return rates




seb_customers_companies = [
    {"customer_id": "000001", "customer_name": "Oval-O Eesti AS", "customer_type": "Corp", "type": "Legal", "country": "EST", "bank_country": "EST", "bic_code": "SEB Pank AS" },
    {"customer_id": "000002", "customer_name": "Dorpat Energia AS", "customer_type": "Corp", "type": "Legal", "country": "EST", "bank_country": "EST", "bic_code": "SEB Pank AS" },
    {"customer_id": "000003", "customer_name": "Dorpat Varahalduse AS", "customer_type": "Corp", "type": "Legal", "country": "EST", "bank_country": "EST", "bic_code": "SEB Pank AS" },
    {"customer_id": "000004", "customer_name": "Dorpat Keemiatööstuse OÜ", "customer_type": "SME", "type": "Legal", "country": "EST", "bank_country": "EST", "bic_code": "SEB Pank AS" },
    {"customer_id": "000005", "customer_name": "Dorpat Logostics AS", "customer_type": "Corp", "type": "Legal", "country": "EST", "bank_country": "EST", "bic_code": "SEB Pank AS" },
    {"customer_id": "000006", "customer_name": "Dorpat Metall AS", "customer_type": "Corp", "type": "Legal", "country": "EST", "bank_country": "EST", "bic_code": "SEB Pank AS" },
    {"customer_id": "000007", "customer_name": "Dorpat Sadam AS", "customer_type": "Corp", "type": "Legal", "country": "EST", "bank_country": "EST", "bic_code": "SEB Pank AS" },
    {"customer_id": "000008", "customer_name": "Dorpat Production AS", "customer_type": "Corp", "type": "Legal", "country": "EST", "bank_country": "EST", "bic_code": "SEB Pank AS" },
]



seb_customers_ibans_companies = {
    "000001" : ['EE160663826059060244', 'EE557761901065155099', 'EE054088717047987288', 'EE373213085775250243', 'EE654433734564483607', 'EE836313890753653648', 'EE687263933714215601', 'EE771064180292559054', 'EE649374512627473772', 'EE255566996214511539', 
                'EE990968931055337841', 'EE683416543365301422', 'EE213116537175714982', 'EE614697841650639241', 'EE945628553779811588', 'EE653225192803746226', 'EE793654358661759760', 'EE477847795749846293', 'EE801017321076375578', 'EE146437254627171163', 
                'EE193746434061360681', 'EE060079469385034101', 'EE098440576349745052', 'EE507646954951440060', 'EE412894526961423878', 'EE041491091066822178', 'EE776922651958789874', 'EE071850872081436073', 'EE734567898449132454', 'EE708286681502324997'],
    "000002" : ['EE093483585480230228', 'EE491521044714174032', 'EE783495535029406641', 'EE119770224000614816', 'EE314383066739310995', 'EE054340449464965464'],
    "000003" : ['EE927294374257148853', 'EE904860599697193875', 'EE429425159047336485'],
    "000004" : ['EE379164482602560053', 'EE789088110227039107', 'EE476022076535703331', 'EE528566839411159571'],
    "000005" : ['EE443083199278410044', 'EE137443368998543679', 'EE706048606507286359', 'EE189537633253621286', 'EE061553406835815481'],
    "000006" : ['EE811602699425035961', 'EE398822090264706758', 'EE938096562636954474'],
    "000007" : ['EE794022970180187924', 'EE381101096691827881'],
    "000008" : ['EE399132874757753344', 'EE746022907249839260', 'EE799058602512404414']
}

oval_group_counterparties = [
    {"customer_id": None, "customer_name": "Oval-O Latvia SIA", "type": "Legal", "country": "LVA", "iban": "LV346633493486938668838", "bank_name": "Swedbank", "bic_code": "HABALV22", "bank_country": "LVA", "currency": "EUR"},
    {"customer_id": None, "customer_name": "Oval-O Latvia SIA", "type": "Legal", "country": "LVA", "iban": "LV248903457983456728345", "bank_name": "Luminor Bank", "bic_code": "RIKOLV2X", "bank_country": "LVA", "currency": "EUR"},
    {"customer_id": None, "customer_name": "Oval-O Latvia SIA", "type": "Legal", "country": "LVA", "iban": "EE234432743298340234823", "bank_name": "Swedbank", "bic_code": "HABAEE2X", "bank_country": "EST", "currency": "EUR"},

    {"customer_id": None, "customer_name": "Oval-O Lietuva UAB", "type": "Legal", "country": "LTU", "iban": "LT986540317286316543216", "bank_name": "SEB Bankas", "bic_code": "CBVILT2X", "bank_country": "LTU", "currency": "EUR"},
    {"customer_id": None, "customer_name": "Oval-O Lietuva UAB", "type": "Legal", "country": "LTU", "iban": "LT499876543210987654321", "bank_name": "Luminor Bank", "bic_code": "AGBLLT2X", "bank_country": "LTU", "currency": "EUR"},
    {"customer_id": None, "customer_name": "Oval-O Lietuva UAB", "type": "Legal", "country": "LTU", "iban": "DE91123456789012345678", "bank_name": "Deutsche Bank", "bic_code": "DEUTDEFF", "bank_country": "GER", "currency": "EUR"},

    {"customer_id": None, "customer_name": "Oval-O Norge AS", "type": "Legal", "country": "NOR", "iban": "NO9386011117947", "bank_name": "DNB Bank", "bic_code": "DNBANOKK", "bank_country": "NOR", "currency": "NOK"},
    {"customer_id": None, "customer_name": "Oval-O Norge AS", "type": "Legal", "country": "NOR", "iban": "NO3512345678902", "bank_name": "Nordea Bank", "bic_code": "NDEANOKK", "bank_country": "NOR", "currency": "NOK"},
    {"customer_id": None, "customer_name": "Oval-O Norge AS", "type": "Legal", "country": "NOR", "iban": "SE1234567890123456789012", "bank_name": "SEB Bank", "bic_code": "ESSESESS", "bank_country": "SWE", "currency": "SEK"},

    {"customer_id": None, "customer_name": "Oval-O Sverige AB", "type": "Legal", "country": "SWE", "iban": "SE3550000000054910000003", "bank_name": "Swedbank", "bic_code": "SWEDSESS", "bank_country": "SWE", "currency": "SEK"},
    {"customer_id": None, "customer_name": "Oval-O Sverige AB", "type": "Legal", "country": "SWE", "iban": "SE2450001110011011121314", "bank_name": "Nordea Bank", "bic_code": "NDEASESS", "bank_country": "SWE", "currency": "SEK"},
    {"customer_id": None, "customer_name": "Oval-O Sverige AB", "type": "Legal", "country": "SWE", "iban": "DK9500001110012001020120", "bank_name": "Danske Bank", "bic_code": "DABADKKK", "bank_country": "DNK", "currency": "DKK"},

    {"customer_id": None, "customer_name": "Oval-O Danmark A/S", "type": "Legal", "country": "DNK", "iban": "DK5000400440116243", "bank_name": "Danske Bank", "bic_code": "DABADKKK", "bank_country": "DNK", "currency": "DKK"},
    {"customer_id": None, "customer_name": "Oval-O Danmark A/S", "type": "Legal", "country": "DNK", "iban": "DK1456781234005678123456", "bank_name": "Jyske Bank", "bic_code": "JYBADKKK", "bank_country": "DNK", "currency": "DKK"},
    {"customer_id": None, "customer_name": "Oval-O Danmark A/S", "type": "Legal", "country": "DNK", "iban": "NO1212345678901", "bank_name": "DNB Bank", "bic_code": "DNBANOKK", "bank_country": "NOR", "currency": "NOK"},

    {"customer_id": None, "customer_name": "Oval-O Polska Sp. z o.o.", "type": "Legal", "country": "POL", "iban": "PL61109010140000071219812874", "bank_name": "PKO Bank Polski", "bic_code": "BPKOPLPW", "bank_country": "POL", "currency": "PLN"},
    {"customer_id": None, "customer_name": "Oval-O Polska Sp. z o.o.", "type": "Legal", "country": "POL", "iban": "PL79123456789012345678901234", "bank_name": "mBank", "bic_code": "BREXPLPW", "bank_country": "POL", "currency": "PLN"},
    {"customer_id": None, "customer_name": "Oval-O Polska Sp. z o.o.", "type": "Legal", "country": "POL", "iban": "DE67123456789012345678", "bank_name": "Deutsche Bank", "bic_code": "DEUTDEFF", "bank_country": "GER", "currency": "EUR"},

    {"customer_id": None, "customer_name": "Oval-O Ireland Energy Group Ltd", "type": "Legal", "country": "IRL", "iban": "IE29AIBK93115212345678", "bank_name": "Allied Irish Banks", "bic_code": "AIBKIE2D", "bank_country": "IRL", "currency": "EUR"},
    {"customer_id": None, "customer_name": "Oval-O Ireland Energy Group Ltd", "type": "Legal", "country": "IRL", "iban": "IE12BOFI90120212345678", "bank_name": "Bank of Ireland", "bic_code": "BOFIIE2D", "bank_country": "IRL", "currency": "EUR"},
    {"customer_id": None, "customer_name": "Oval-O Ireland Energy Group Ltd", "type": "Legal", "country": "IRL", "iban": "GB29NWBK60161331926819", "bank_name": "NatWest Bank", "bic_code": "NWBKGB2L", "bank_country": "GBR", "currency": "GBP"},
]


channel = [{'code': 1, 'type': 'Mobile App'}, {'code': 2, 'type': 'Internet Bank'}, {'code': 3, 'type': 'POS'}, {'code': 4, 'type': 'Branch'}]
    
exchange_rates = load_exchange_rates('exchange_rates.csv')

seb_private_customers = create_private_seb_customers(500)

total_customers = combine_customers(seb_customers_companies, seb_private_customers)

estonian_counterparties_customers = create_est_counterparties(1000,500, "customer")
estonian_counterparties_vendors = create_est_counterparties(1000,500, "vendor")

foreign_counterparties_customers = create_foreign_counterparties(1000,500, "customer")
foreign_counterparties_vendors = create_foreign_counterparties(1000,500, "vendor")



# Function to generate a transaction row
def create_transaction_row(transaction_id, customer_id, customer_iban, counterparty_iban, counterparty_id, counterparty_name, 
                           counterparty_country, counterparty_bank_name, counterparty_bank_country, counterparty_bank_bic_code, 
                           counterparty_type, amount_org, amount_eur, currency, dc, transaction_scope, channel_type, channel_code,
                           description, fee_f, fee_type, is_rvrs_f, is_rvrs_orig_id, was_later_rvrs_f, transaction_time):
    return {
        'transaction_id': transaction_id,
        'customer_id': customer_id,
        'customer_iban': customer_iban,
        'counterparty_iban': counterparty_iban,
        'counterparty_id': counterparty_id,
        'counterparty_name': counterparty_name,
        'counterparty_country': counterparty_country,
        'counterparty_bank_name': counterparty_bank_name,
        'counterparty_bank_country': counterparty_bank_country,
        'counterparty_bank_bic_code': counterparty_bank_bic_code,
        'counterparty_type': counterparty_type,
        'date': transaction_time.date(),
        'time': transaction_time.time(),
        'amount_org': amount_org,
        'amount_eur': amount_eur,
        'currency': currency,
        'dc': dc,
        'transaction_scope': transaction_scope,
        'channel_type': channel_type,
        'channel_code': channel_code,
        'description': description,
        'fee_f': fee_f,
        'fee_type': fee_type,
        'is_rvrs_f': is_rvrs_f,
        'is_rvrs_orig_id': is_rvrs_orig_id,
        'was_later_rvrs_f': was_later_rvrs_f
    }

# Function to generate transactions between one SEB customer's own accounts
# arg1 num_transactions - intger number of how many transactions to generate. Each transaction comes as two rows, from the perspective of initiator and receiver
# arg2 transaction_id - integer number, start from 1 or continues some other numbering 
# arg3 seb_customer - if you want to create transactions between accounts of a specific customer enter that customer, if not leave it empty a random customer will be chosen
def generate_transactions_1(num_transactions, transaction_id, seb_customer=None):
    transactions = []
    
    for _ in range(num_transactions):
        # Transaction time is the same for debit/credit pairs
        transaction_time = generate_transaction_date()
        random_seb_customer = random.choice(seb_customers_companies) if seb_customer is None else seb_customer
        customer_id = random_seb_customer["customer_id"]
        customer_name = random_seb_customer["customer_name"]
        # Randomly pick two IBANs and store them in a separate list
        random_ibans = random.sample(seb_customers_ibans_companies[customer_id], 2)
        selected_channel = random.choice(channel)
        # Same amount for both transactions
        amount_org = generate_random_amount()
        amount_eur = amount_org
        # Type 1: My Company transacts with its own accounts within SEB
        for j in range(2):
            dc = 'D' if j == 0 else 'C'
            description = f"Payment within {customer_name} between SEB accounts"
            transactions.append(create_transaction_row(
                transaction_id, 
                customer_id, 
                random_ibans[j], 
                random_ibans[1-j], 
                customer_id, 
                customer_name, 
                "EST", 
                "SEB Pank AS",
                "EST", 
                "EEUHEE2X", 
                'Corp', 
                amount_org, 
                amount_eur, 
                'EUR', 
                dc, 
                'I', 
                selected_channel['type'], 
                selected_channel['code'],
                description, 
                False, 
                None, 
                False, 
                None, 
                False, 
                transaction_time))
            
        transaction_id += 1

    return transactions, transaction_id

# Generate random transactions for Type 2a: Group of SEB customers companies transacting with each other (two transaction rows)
# If certain SEB customer companies should have more transactions with each other, then I select only these companies and leave others out
# arg1 num_transactions - intger number of how many transactions to generate. Each transaction comes as two rows, from the perspective of initiator and receiver
# arg2 transaction_id - integer number, start from 1 or continues some other numbering 
# arg3 selected_seb_customers - selcted group of SEB customer companies (at least 2) out of all seb_customers_companies
def generate_transactions_seb_2a(num_transactions, id, selected_seb_customers): #seb_customers_companies[1:]
    transactions = []
    transaction_id = id
    for _ in range(num_transactions):
        # Pick two random customers from the predefined list
        two_random_customers = random.sample(selected_seb_customers, 2) # leave 1st company in the list out of selection
        customer1 = two_random_customers[0]
        customer2 = two_random_customers[1]
        customer1_id = customer1["customer_id"]
        customer2_id = customer2["customer_id"]
        customer1_iban = random.choice(seb_customers_ibans_companies[customer1_id])
        customer2_iban = random.choice(seb_customers_ibans_companies[customer2_id])
        customer1_name = customer1["customer_name"]
        customer2_name = customer2["customer_name"]
        customer2_country = customer2["country"] 
        customer1_type = customer1['type']
        customer2_type = customer2['type']
        selected_channel = random.choice(channel)

        # Transaction date/time
        transaction_time = generate_transaction_date()

        amount_org = random.uniform(100, 5000)  # Random transaction amount
        amount_eur = amount_org  # Since we assume EUR transactions

        # Loop to generate the debit and credit transactions
        for j in range(2):
            dc = 'D' if j == 0 else 'C'  # Debit in the first iteration, Credit in the second
            description = f"Payment to {customer2_name}" if j == 0 else f"Payment from {customer1_name}"

            transactions.append(create_transaction_row(
                transaction_id,
                customer1_id if j == 0 else customer2_id,  # My company in the first transaction
                customer1_iban if j == 0 else customer2_iban,
                customer2_iban if j == 0 else customer1_iban,
                customer2_id if j == 0 else customer1_id,
                customer2_name if j == 0 else customer1_name,
                customer2_country,
                'SEB Pank AS',
                'EST',
                "EEUHEE2X",
                customer2_type if j == 0 else customer1_type,
                amount_org,
                amount_eur,
                'EUR',
                dc,
                'I',  # Intrabank transfer for SEB Estonia clients
                selected_channel['type'],
                selected_channel['code'],
                description,
                False, 
                None, 
                False, 
                None, 
                False, 
                transaction_time
            ))

        transaction_id += 1

    return transactions, transaction_id



# Generate random transactions for Type 2b: Group of SEB customer companies transacting with SEB Private clients (also two transaction rows)
# If certain companies should have more transactions with Private persons from SEB then I select those company customers
# arg1 num_transactions - intger number of how many transactions to generate. Each transaction comes as two rows, from the perspective of initiator and receiver
# arg2 transaction_id - integer number, start from 1 or continues some other numbering 
# arg3 selected_seb_customers - selcted group of SEB customer companies out of all seb_customers_companies
# arg4 type - means credit "C" or debit "D". Use with "D" then Private preson is the one who initiates the transaction and Company receives it.
def generate_transactions_seb_2b(num_transactions, id, selected_seb_customers, type): #seb_customers_companies[1:]
    dc = 'C' if type is None or type == 'C' else 'D'
    transactions = []
    transaction_id = id
    for _ in range(num_transactions):
        # Pick one random seb customer company from list
        customer2 = random.choice(selected_seb_customers) if len(selected_seb_customers) > 1 else selected_seb_customers[0] #seb company customer
        customer1 = random.choice(seb_private_customers) #seb private customer
        customer1_id = customer1["customer_id"]
        customer2_id = customer2["customer_id"]
        customer2_iban = random.choice(seb_customers_ibans_companies[customer2_id])
        customer1_iban = customer1['iban']
        customer2_name = customer2["customer_name"]
        customer1_name = customer1["customer_name"]
        customer1_country = customer1["country"] 
        customer2_country = customer2["country"]
        customer2_type = customer2["type"]
        customer1_type = customer1['type']
        selected_channel = random.choice(channel)
        description1 = f"Payment from {customer1_name}"
        description2 = f"Payment to {customer2_name}"

        # Transaction date/time
        transaction_time = generate_transaction_date()

        amount_org = random.uniform(100, 5000)  # Random transaction amount
        amount_eur = amount_org  # Since we assume EUR transactions

        # Loop to generate the debit and credit transactions
        for j in range(2):
            description = description1 if dc == 'C' else description2

            transactions.append(create_transaction_row(
                transaction_id,
                customer1_id if dc == 'D' else customer2_id,  # My company in the first transaction
                customer1_iban if dc == 'D' else customer2_iban,
                customer2_iban if dc == 'D' else customer1_iban,
                customer2_id if dc == 'D' else customer1_id,
                customer2_name if dc == 'D' else customer1_name,
                customer2_country,
                'SEB Pank AS',
                'EST',
                "EEUHEE2X",
                customer2_type if dc == 'D' else customer1_type,
                amount_org,
                amount_eur,
                'EUR',
                dc,
                'I',  # Intrabank transfer for SEB Estonia clients
                selected_channel['type'],
                selected_channel['code'],
                description,
                False, 
                None, 
                False, 
                None, 
                False, 
                transaction_time
            ))
            dc = 'D' if dc == 'C' else 'C'

        transaction_id += 1

    return transactions, transaction_id





# Generate random transactions for Type 3: SEB Customer Company transacts with other Companies and private persons whose bank accounts are in Estonian banks outside SEB Pank AS
def generate_transactions_3(num_transactions, transaction_id, dc, selected_seb_customers, counterparties):
    transactions = []

    for _ in range(num_transactions):
        # Pick a random SEB Customer company from preselected seb customers list. Pre selected list, because you can choose for which customers you want to create these transactions.
        seb_customer = random.choice(selected_seb_customers)
        # Pick a random conterparty from vendors_est,the predefined list
        counterparty_est = random.choice(counterparties)
        transaction_time = generate_transaction_date()
        selected_channel = random.choice(channel)
        description_debit = f"Payment from {seb_customer['customer_name']} to {counterparty_est['customer_name']}"
        description_credit = f"Payment from {counterparty_est['customer_name']} to {seb_customer['customer_name']}"
        amount_org = random.uniform(10, 500) if counterparty_est["type"] == "Private" else random.uniform(10, 25000)  # Random transaction amount
        amount_eur = amount_org  # Since we assume EUR transactions
        
        transactions.append(create_transaction_row(
            transaction_id,
            seb_customer["customer_id"],
            random.choice(seb_customers_ibans_companies[seb_customer["customer_id"]]), #iban
            counterparty_est['iban'],
            None,  # counterparty id
            counterparty_est['customer_name'],
            counterparty_est['country'],
            counterparty_est['bank_name'],
            'EST',
            counterparty_est['bic_code'],
            counterparty_est['type'],
            amount_org,
            amount_eur,
            'EUR',
            dc, # Debit if dc = 'D' and Credit if dc = 'C'
            'D',  # Domestic transfer
            selected_channel['type'],
            selected_channel['code'],
            description_debit if dc == 'D' else description_credit,
            False,
            None,
            False, 
            None, 
            False, 
            transaction_time
        ))

        transaction_id += 1

    return transactions, transaction_id



# Generate random transactions for Type 4: 
# Selected SEB clients group pays to other companies and private persons whose bank accounts are outside Estonia 'D'
# or private persons and companies whose bank accounts are outside Estonia pay to selected SEB clients group  'C'
# arg1: integer, how many transactons to create
# arg2: integer transaction_id. Start from 1 or enter another number to continue from there
# arg3: 'D' for debit transactions, 'C' for credit transactions.
# arg4: list of selected SEB customers for who to create transactions
# arg5: list of dictionaries, for 'D' transactions choose vendors_foreign and for 'C' transactions choose customers_foreign
def generate_transactions_4(num_transactions, transaction_id, dc, selected_seb_customers, counterparties):
    transactions = []

    for _ in range(num_transactions):
        # Pick a random SEB Customer company from preselected seb customers list.
        seb_customer = random.choice(selected_seb_customers)
        # Pick a random conterparty from vendors_foreign,the predefined list
        counterparty_foreign = random.choice(counterparties)
        transaction_time = generate_transaction_date()
        selected_channel = random.choice(channel)
        description_debit = f"Payment from {seb_customer['customer_name']} to {counterparty_foreign['customer_name']}"
        description_credit = f"Payment from {counterparty_foreign['customer_name']} to {seb_customer['customer_name']}"
        currency = counterparty_foreign['currency']
        amount_org = random.uniform(10, 500) if counterparty_foreign["type"] == "Private" else random.uniform(10, 25000)  # Random transaction amount  # Random transaction amount
        amount_eur = amount_org if currency == 'EUR' else convert_to_euro(amount_org, currency, transaction_time.date().strftime('%Y-%m-%d'), exchange_rates)
        
        transactions.append(create_transaction_row(
            transaction_id,
            seb_customer["customer_id"],
            random.choice(seb_customers_ibans_companies[seb_customer["customer_id"]]), #iban,
            counterparty_foreign['iban'],
            None,  # counterparty id
            counterparty_foreign['customer_name'],
            counterparty_foreign['country'],
            counterparty_foreign['bank_name'],
            counterparty_foreign['bank_country'], # bank_country can be different from counterpaty country
            counterparty_foreign['bic_code'],
            counterparty_foreign['type'],
            amount_org,
            amount_eur,
            currency, # transaction currency
            dc, # Debit if dc = 'D' and Credit if dc = 'C'
            'F',  # Foreign transfer
            selected_channel['type'],
            selected_channel['code'],
            description_debit if dc == 'D' else description_credit,
            False,
            None,
            False, 
            None, 
            False,
            transaction_time
        ))

        transaction_id += 1

    return transactions, transaction_id


# Write transactions to CSV
def write_transactions_to_csv(filename, transactions):
    fieldnames = [
        'transaction_id', 'customer_id', 'customer_iban', 'counterparty_iban', 'counterparty_id', 
        'counterparty_name', 'counterparty_country', 'counterparty_bank_name', 'counterparty_bank_country', 
        'counterparty_bank_bic_code', 'counterparty_type', 'date', 'time', 
        'amount_org', 'amount_eur', 'currency', 'dc', 'transaction_scope', 'channel_type', 'channel_code', 
        'description', 'fee_f', 'fee_type', 'is_rvrs_f', 'is_rvrs_orig_id', 'was_later_rvrs_f'
    ]

    with open(filename, 'w', newline='', encoding='utf-8') as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()

        # Replace None with an empty string explicitly
        for transaction in transactions:
            sanitized_transaction = {}
            for key, value in transaction.items():
                if value is None:
                    sanitized_transaction[key] = "null"  # Replace None with "null"
                else:
                    sanitized_transaction[key] = value  # Keep the original value
            writer.writerow(sanitized_transaction)

def write_customers_to_csv(filename, customers):
    fieldnames = ['customer_id', 'customer_name', 'customer_type']

    with open(filename, 'w', newline='', encoding='utf-8') as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(customers)

# Main function to generate and save transactions
def main():
    total_transactions = []

    id = 1
    # Generate transactions of type 1
    transactions_1, id = generate_transactions_1(10000, id, seb_customers_companies[0])
    total_transactions += transactions_1

    # Generate transactions of type 1
    transactions_1, id = generate_transactions_1(5000, id)
    total_transactions += transactions_1


    # Generate transactions of type 2a
    transactions_seb2a, id = generate_transactions_seb_2a(10000, id, seb_customers_companies[1:])
    total_transactions += transactions_seb2a

    # Generate  transactions of type 2b
    transactions_seb_2b, id = generate_transactions_seb_2b(50000, id, [seb_customers_companies[0]], 'D')
    total_transactions += transactions_seb_2b
    # Generate  transactions of type 2b
    transactions_seb_2b, id = generate_transactions_seb_2b(1000, id, seb_customers_companies[1:], 'D')
    total_transactions += transactions_seb_2b

    # Generate  transactions of type 3d
    # generate_transactions_3(num_transactions, transaction_id, dc, selected_seb_customers, counterparties)
    transactions_3d, id = generate_transactions_3(10000, id, 'D', seb_customers_companies, estonian_counterparties_vendors)
    total_transactions += transactions_3d

    # Generate  transactions of type 3c
    transactions_3c, id = generate_transactions_3(10000, id, 'C', seb_customers_companies, estonian_counterparties_vendors)
    total_transactions += transactions_3c

    # Generate transactions of 4 Debit
    transactions_4d, id = generate_transactions_4(20000, id, 'D', seb_customers_companies, foreign_counterparties_vendors)
    total_transactions += transactions_4d

     # Generate transactions of 4 Debit
    transactions_4d, id = generate_transactions_4(20000, id, 'D', seb_customers_companies[1:], oval_group_counterparties)
    total_transactions += transactions_4d

    # Generate transactions of 4 Credit
    transactions_4c, id = generate_transactions_4(20000, id, 'C', seb_customers_companies, foreign_counterparties_customers)
    total_transactions += transactions_4c

       # Generate transactions of 4 Credit
    transactions_4c, id = generate_transactions_4(20000, id, 'C', seb_customers_companies[1:], oval_group_counterparties)
    total_transactions += transactions_4c


    #print(total_transactions)

    # Save all SEB customers to CSV
    write_customers_to_csv('customers.csv', total_customers)
  
    # Save all transactions to CSV
    write_transactions_to_csv('transactions.csv', total_transactions)




if __name__ == "__main__":
    main()