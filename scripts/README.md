# Transfer Data Generator

This Node.js script generates randomized transfer data between companies in CSV format. The data structure is based on an Excel file with columns for transaction details such as `transaction_id`, `customer_type`, `amount`, `bank_name`, and many more. The user can specify how many rows to generate, and the script outputs the data into a CSV file.

## Prerequisites

To run the script, you will need:

- **Node.js** (version 20 or later)
- **npm** (Node Package Manager)

If you don't have Node.js installed, you can download it [here](https://nodejs.org/en/).

## Installation

1. Clone the repository or download the script files.

2. Open a terminal and navigate to the directory where you have saved the script files.

3. Run the following command to install the required dependencies:

   ```bash
   npm install
   ```
   This will install the following dependencies

## Usage

1. After installing the dependencies, run the script using the following command:

   ```bash
   npm start
   ```

2. You will be prompted to enter the following details:
    - **Number of rows**: Specify how many rows of data to generate (default is 10).
    - **Output file name**: Specify the name of the output CSV file (default is `transfers.csv`).

** NB! the output will be stored in output folder, which is also listed in .gitignore file**

3. The script will generate the specified number of rows of transfer data and save it to the CSV file you named.

## Example

### Running the script:

```bash
npm start
```

### Example prompts:

```
? How many rows of data do you want to generate? (100)
? What should be the name of the output CSV file? (transfers.csv)
```

### Example output (in `output/transfers.csv`):

```csv
transaction_id,jh_jh,customer_id,date,time,amount_orig,amount_eur,currency,dc,source_account,destination_account,transaction_scope,channel_type,channel_code,description,fee_f,fee_type,participant_name,psn_code,customer_type,postal_code,country,customer_f,c_customer_id,c_participant_name,c_participant_country,c_participant_address,c_bank_name,c_bank_country,c_bank_country_high_risk,c_participant_bank_bic_code,c_participant_regnum,c_participant_f,same_participant_group_id_f,private_corp_sme,is_rvrs_f,is_rvrs_orig_jh,jh_bra,jh_bra_nm,jh_who,jh_who_name,jh_tp,jh_tp_nm
8427346955,12345678901234567890,987654321234567,2023-10-01,12:45:15,5000.50,4600.30,USD,D,12345678901234567890,12345678901234567890,I,Mobile App,21,POS Purchase - Grocery Store,1,SHA,John Doe,0000000001,Private,94103,SWE,1,000000000000001,John Doe,SWE,Kungsgatan 18, Stockholm, Sweden,Swedbank,SWE,0,PNCCUS33,12345678901234567890,1,1,Private,0,1234567,Main Street Branch,9876543210,John Doe,12345678,Mobile Transfer
...
```
