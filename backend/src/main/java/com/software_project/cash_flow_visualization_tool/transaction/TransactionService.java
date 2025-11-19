package com.software_project.cash_flow_visualization_tool.transaction;

import com.software_project.cash_flow_visualization_tool.customer.CustomerRepository;
import com.software_project.cash_flow_visualization_tool.transaction.dto.*;

import com.software_project.cash_flow_visualization_tool.transaction.dto.account.*;

import com.software_project.cash_flow_visualization_tool.transaction.dto.account.AccountAggregateDTO;
import com.software_project.cash_flow_visualization_tool.transaction.dto.account.AccountLinkDTO;
import com.software_project.cash_flow_visualization_tool.transaction.dto.account.AccountNodeDTO;
import com.software_project.cash_flow_visualization_tool.transaction.dto.account.OwnerNodeDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    public TransactionService(TransactionRepository transactionRepository, CustomerRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
    }


    public List<TableDTO> getInternalTransactionSummaries(List<String> customerIds, LocalDate startDate, LocalDate endDate) {
        Set<InternalTransactionSummaryDTO> summaries = transactionRepository.findInternalTransactionSummaries(customerIds, startDate, endDate);

        return summaries.stream().map(summary -> {
            TableDTO tableDTO = new TableDTO();
            tableDTO.setSource(summary.getCustomerIban());
            tableDTO.setTarget(summary.getCounterpartyIban());
            tableDTO.setSourceName(summary.getCustomerName());
            tableDTO.setTargetName(summary.getCounterpartyName());
            tableDTO.setCurrency(summary.getCurrency());

            // Populate amounts map
            Map<String, BigDecimal> amounts = new HashMap<>();
            amounts.put("totalIncoming", summary.getTotalIncoming() != null ? summary.getTotalIncoming() : BigDecimal.ZERO);
            amounts.put("totalOutgoing", summary.getTotalOutgoing() != null ? summary.getTotalOutgoing() : BigDecimal.ZERO);
            amounts.put("netFlow", summary.getNetFlow() != null ? summary.getNetFlow() : BigDecimal.ZERO);
            tableDTO.setAmounts(amounts);

            // Populate average amounts map
            Map<String, BigDecimal> avgAmounts = new HashMap<>();
            avgAmounts.put("averageIncoming", summary.getAverageIncoming() != null ? summary.getAverageIncoming() : BigDecimal.ZERO);
            avgAmounts.put("averageOutgoing", summary.getAverageOutgoing() != null ? summary.getAverageOutgoing() : BigDecimal.ZERO);
            avgAmounts.put("averageNetFlow", summary.getAverageNetFlow() != null ? summary.getAverageNetFlow() : BigDecimal.ZERO);
            tableDTO.setAvgAmounts(avgAmounts);

            // Set transaction count and date range
            tableDTO.setCount((summary.getIncomingCount() != null ? summary.getIncomingCount() : 0) +
                    (summary.getOutgoingCount() != null ? summary.getOutgoingCount() : 0));

            // Set earliest and latest dates based on available incoming and outgoing dates
            tableDTO.setEarliestDate(summary.getEarliestIncomingDate() != null ? summary.getEarliestIncomingDate() : summary.getEarliestOutgoingDate());
            tableDTO.setLatestDate(summary.getLatestIncomingDate() != null ? summary.getLatestIncomingDate() : summary.getLatestOutgoingDate());

            return tableDTO;
        }).collect(Collectors.toList());
    }


    public AccountAggregateDTO getInternalTransfersBetweenCompanies(List<String> customerIds, LocalDate startDate, LocalDate endDate) {

        List<Object[]> results = transactionRepository.findInternalTransfersBetweenCompanies(customerIds, startDate, endDate);   // all transactions of internal transfers
        Set<InternalTransactionSummaryDTO> res = transactionRepository.findInternalTransactionSummaries(customerIds,startDate,endDate); // not used but the idea was to if needed you can include the individual transactions between every account
        Map<String, AccountNodeDTO> accountNodesMap = new HashMap<>(); // accounts
        Map<String, OwnerNodeDTO> ownerNodesMap = new HashMap<>();          // account owners
        Map<String,AccountLinkDTO> companyAndAccountLinksMap = new HashMap<>(); // liks between accounts and owners

        Map<String, AccountLinkDTO> linkMap = new HashMap<>(); // liks between accounts (transactions info)
        Map<String, AccountNetFlowLinkDTO> netFlowMap = new HashMap<>(); // netflow links between accounts
        Map<String,TableDTO> edgeTable = new HashMap<>();

        for (Object[] result : results) {
            String customerId = (String) result[0];          // id
            String customerName = (String) result[1];       // name
            String customerType = (String) result[2];       // type
            String customerIban = (String) result[3];       //iban
            String targetIban = (String) result[4];         //counterparty iban
            BigDecimal amount = (BigDecimal) result[5];     // transaction amount
            String currency = (String) result[6];           // currency used in transaction
            String country = (String) result[7];            // not used because Internal transactions always are SEB customers
            String counterpartyId = (String) result[8];     // counterparty ID
            String counterpartyName = (String) result[9];   // counterparty name
            String counterpartyBankCountry = (String) result[10];   //counterparty bank country
            LocalDate dateT = LocalDate.parse(result[11].toString());   // date
            String ownerCountry = "EST";        // internal always SEB customer

            //create Map based on customer ids, to create account owners
            // the key is customer id
            ownerNodesMap.computeIfAbsent(customerId, id -> new OwnerNodeDTO(customerId, customerName, ownerCountry));
            //map based on customer ibans, to create accounts
            //the key is iban
            accountNodesMap.computeIfAbsent(customerIban, iban ->
                    new AccountNodeDTO(customerId, customerName, customerType, customerIban, customerName, ownerCountry, currency)
            );


            // create link between account and account owner
            // key is customerId+customerIban+currency or  counterpartyId+targetIban+currency
            companyAndAccountLinksMap.computeIfAbsent(customerId+customerIban+currency,id ->new AccountLinkDTO(customerId, customerIban, null, null, currency,0,null,null,null,null,null,null,null,null,null,null,null));
            companyAndAccountLinksMap.computeIfAbsent(counterpartyId+targetIban+currency,id ->new AccountLinkDTO(counterpartyId, targetIban, null, null, currency,0,null,null,null,null,null,null,null,null,null,null,null));

            //create Map based on counterparty ids, to create account owners
            // the key is counterparty id
            ownerNodesMap.computeIfAbsent(counterpartyId, id ->
                    new OwnerNodeDTO(counterpartyId, counterpartyName, counterpartyBankCountry)
            );

            //map based on counterparty ibans, to create accounts
            //the key is counterparty iban
            accountNodesMap.computeIfAbsent(targetIban, iban ->
                    new AccountNodeDTO(counterpartyId, counterpartyName, "Unknown", targetIban, counterpartyName, counterpartyBankCountry, currency)
            );

            // netflow links
            String netFlowKey = (customerIban.compareTo(targetIban) < 0) ? customerIban + "-" + targetIban : targetIban + "-" + customerIban;
            AccountNetFlowLinkDTO netFlowLink = netFlowMap.computeIfAbsent(netFlowKey, key -> new AccountNetFlowLinkDTO(customerIban, targetIban, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, currency,0,null,null,null,null,null,null,null,new ArrayList<>(),null,null,null));

            // update netflowlinks as needed
            //depends on what transactions is
            if (customerIban.equals(netFlowLink.getSourceAccountId())) {
                netFlowLink.setFlowAmountToTarget(netFlowLink.getFlowAmountToTarget().add(amount));
            } else {
                netFlowLink.setFlowAmountToSource(netFlowLink.getFlowAmountToSource().add(amount));
            }
            netFlowLink.updateFlowAmounts(amount);
            netFlowLink.updateNetFlow();
            netFlowLink.updateCount(); // Increment transaction count for net flow link
            netFlowLink.updateAverage();
            netFlowLink.updateAmounts();
            netFlowLink.setEarliestDate(getEarliestDate(dateT,netFlowLink.getEarliestDate()));
            netFlowLink.setLatestDate(getLatestDate(dateT,netFlowLink.getLatestDate()));
            netFlowLink.updateMedian();

            //update links as needed
            String linkKey = customerIban + "-" + targetIban + "-" + currency;
            AccountLinkDTO link = linkMap.computeIfAbsent(linkKey, key -> new AccountLinkDTO(customerIban, targetIban, "Internal Transfer", BigDecimal.ZERO, currency,0,null,null,null,null,null,null,null,new ArrayList<>(),null,null,null));
            link.setAmount(link.getAmount().add(amount));
            link.updateFlowAmounts(amount);
            link.updateCount(); // Increment transaction count for transaction link
            link.updateAverage();
            link.updateAmounts();
            link.updateMedian();
            link.setEarliestDate(getEarliestDate(dateT,link.getEarliestDate()));
            link.setLatestDate(getLatestDate(dateT,link.getLatestDate()));
        }

        List<AccountLinkDTO> accountLinks = new ArrayList<>(linkMap.values());
        List<AccountNetFlowLinkDTO> netFlowLinks = new ArrayList<>(netFlowMap.values());

        return new AccountAggregateDTO(
                new ArrayList<>(ownerNodesMap.values()),   // ownerNodes
                new ArrayList<>(companyAndAccountLinksMap.values()),                    // companyAndAccountLink
                new ArrayList<>(accountNodesMap.values()), // accountNodes
                accountLinks,                              // accountLinks
                netFlowLinks                                // netflow links

        );
    }

    



    public ExternalNodesLinksDTO getExternalTransfers(List<String> customerIds, LocalDate startDate, LocalDate endDate, Integer topN, String viewType) {
        //VALIDATE INPUT
        customerIds = validateCustomerIds(customerIds);
        Pair<LocalDate, LocalDate> validatedDates = validateStartDateAndEndDate(startDate, endDate);
        startDate = validatedDates.getLeft();
        endDate = validatedDates.getRight();
        topN = validateTopN(topN);

        //QUERY THE DATABASE
        //find top n debit and credit counterparties names transacting with selected SEB customers (customerIds) in the given date range
        Pair<List<CounterpartyNamesDTO>, List<CounterpartyNamesDTO>> topCounterpartiesNames = findTopCounterpartiesNames(customerIds, startDate, endDate, topN, viewType);
        List<String> topDebitNames = topCounterpartiesNames.getLeft().stream().map(CounterpartyNamesDTO::getCounterpartyName).collect(Collectors.toList());
        List<String> topCreditNames = topCounterpartiesNames.getRight().stream().map(CounterpartyNamesDTO::getCounterpartyName).collect(Collectors.toList());


        //gets companies aggregated debit, credit, netflow transactions, count, avg, median, earliest and latest dates
        Set<TransactionSummaryDTO> transactions = findAggregatedTransactionsWithTopCompanyCounterparties(customerIds, topDebitNames, topCreditNames, startDate, endDate, viewType);


        //gets private debit, credit, netflow transactions, count, earliest and latest dates
        Set<TransactionSummaryDTO> privateTransactions = findAggregatedTransactionsWithTopPrivateCounterparties(customerIds, startDate, endDate, viewType);

        //put companies and private transactions together
        transactions.addAll(privateTransactions);

        //CREATING NODES AND LINKS
        Map<String, ExternalNodeDTO> nodesMap = new HashMap<>();
        Set<LinkDTO> groupLinks = new HashSet<>();
        List<LinkDTO> debitLinks = new ArrayList<>();
        List<LinkDTO> creditLinks = new ArrayList<>();
        List<LinkDTO> netflowLinks = new ArrayList<>();
        List<LinkDTO> netflowTable = new ArrayList<>();
        Set<CounterpartyKeyDTO> topDebitCounterparties = new HashSet<>();
        Set<CounterpartyKeyDTO> topCreditCounterparties = new HashSet<>();

        // Step 1: Add Group node, Private node, Counterparty nodes, and SEB Customer company Account nodes
        addNodesAndGroupLinks(nodesMap, groupLinks, transactions, topDebitCounterparties, topCreditCounterparties, topDebitNames, topCreditNames);


        // Step 2: Add Debit, Credit, and Net Flow links
        addDebitCreditAndNetFlowLinks(debitLinks, creditLinks, netflowLinks, netflowTable, nodesMap, transactions);


        Set<ExternalNodeDTO> nodes = new HashSet<>(nodesMap.values());
        // Sort by currency, then by amount
        debitLinks.sort(Comparator
                .comparing(LinkDTO::getCurrency)
                .thenComparing(link -> link.getAmounts().get("original"), Comparator.reverseOrder())
        );
        creditLinks.sort(Comparator
                .comparing(LinkDTO::getCurrency)
                .thenComparing(link -> link.getAmounts().get("original"), Comparator.reverseOrder())
        );
        netflowTable.sort(Comparator
                .comparing(LinkDTO::getCurrency)
                .thenComparing(link -> link.getAmounts().get("original"), Comparator.reverseOrder())
        );

        return new ExternalNodesLinksDTO(nodes, groupLinks, debitLinks, creditLinks, netflowLinks, netflowTable, topDebitCounterparties, topCreditCounterparties);
    }


    //MAIN FUNCTIONS
    private void addNodesAndGroupLinks (Map<String, ExternalNodeDTO> nodesMap, Set<LinkDTO> groupLinks, Set<TransactionSummaryDTO> transactions, Set<CounterpartyKeyDTO> topDebitCounterparties, Set<CounterpartyKeyDTO> topCreditCounterparties, List<String> topDebitNames, List<String> topCreditNames) {
        // Step 1: Initialize Group node

        // Add one "My Group" node
        ExternalNodeDTO groupNode = new ExternalNodeDTO();
        groupNode.setId(0);  // ID 0 for the group node
        groupNode.setName("MainCo");
        groupNode.setCountry("EST");
        groupNode.setType("group");
        groupNode.setIban(null);
        groupNode.setCurrency(null);
        nodesMap.put("MainCo", groupNode);  // Key for the group node

        // Step 2: Add Counterparty Nodes (type: company)

        int nodeIdCounter = 1;  // initialize node id counter and start from 1 to avoid conflicts with group node ID

        for (TransactionSummaryDTO dto : transactions) {

            // Step 2: Add Counterparty Company Nodes (type: company)
            String counterpartyKey = dto.getCounterpartyName(); //Private or some company name: My Company, Hitachi, etc
            if (!counterpartyKey.equals("Private")) {
                String counterpartyCompanyNodeKey = generateCounterpartyCompanyNodeKey(dto); //if counterparty_bank_country is null, it will be just counterparty name otherwise counterparty name + country
                ExternalNodeDTO counterpartyNode = new ExternalNodeDTO();
                counterpartyNode.setId(nodeIdCounter++);
                counterpartyNode.setName(dto.getCounterpartyName());
                counterpartyNode.setCountry(dto.getCounterpartyBankCountry());
                counterpartyNode.setType("company");
                nodesMap.put(counterpartyCompanyNodeKey, counterpartyNode);
            }

            // Step 3: Add  Counterparty Private Nodes for each country (type: private)
            if (counterpartyKey.equals("Private")) {
                String counterpartyPrivateKey = generateCounterpartyPrivateNodeKey(dto);  //becomes Private or Private-EST, Private-FIN, Private-LTU
                if (!nodesMap.containsKey(counterpartyPrivateKey)) {
                    ExternalNodeDTO privateNode = new ExternalNodeDTO();
                    privateNode.setId(nodeIdCounter++);
                    privateNode.setName("Private");
                    privateNode.setCountry(dto.getCounterpartyBankCountry());
                    privateNode.setType("private");
                    nodesMap.put(counterpartyPrivateKey, privateNode);
                }
            }

            // Step 4: Add SEB Customer company Account Nodes (type: account) based on iban and currency
            String myCompaniesGroupAccountKey = generateNodeKey(dto.getCustomerName(), dto.getCustomerIban(), dto.getCurrency());
            if (!nodesMap.containsKey(myCompaniesGroupAccountKey)) {
                ExternalNodeDTO accountNode = new ExternalNodeDTO();
                accountNode.setId(nodeIdCounter++);
                accountNode.setName(dto.getCustomerName());
                accountNode.setCountry("EST");  // All SEB Estonia's customers' accounts are in Estonia
                accountNode.setType("account");
                accountNode.setIban(dto.getCustomerIban());
                accountNode.setCurrency(dto.getCurrency());
                nodesMap.put(myCompaniesGroupAccountKey, accountNode);

                // Link the new account node to the "My Group" node
                LinkDTO groupLink = new LinkDTO();
                groupLink.setSource("0");  // Group node ID
                groupLink.setTarget(String.valueOf(accountNode.getId()));  // Account node ID
                groupLink.setCurrency(null);
                groupLink.setLabel("Group Link");
                groupLinks.add(groupLink);
            }

            if(topDebitNames.contains(dto.getCounterpartyName()) && !dto.getCounterpartyName().equals("Private")) {
                CounterpartyKeyDTO key = new CounterpartyKeyDTO(dto.getCounterpartyName(), dto.getCounterpartyBankCountry(), 'D', dto.getCounterpartyName() + "-" + dto.getCounterpartyBankCountry());
                topDebitCounterparties.add(key);
            }
            if(topCreditNames.contains(dto.getCounterpartyName()) && !dto.getCounterpartyName().equals("Private")) {
                CounterpartyKeyDTO key = new CounterpartyKeyDTO(dto.getCounterpartyName(), dto.getCounterpartyBankCountry(), 'C', dto.getCounterpartyName() + "-" + dto.getCounterpartyBankCountry());
                topCreditCounterparties.add(key);
            }
        }
    }

    private void addDebitCreditAndNetFlowLinks (List<LinkDTO> debitLinks, List<LinkDTO> creditLinks, List<LinkDTO> netflowLinks, List<LinkDTO> netflowTable, Map<String, ExternalNodeDTO> nodesMap, Set<TransactionSummaryDTO> transactions) {

        //Create transaction links between counterparty nodes + private node and My Group accounts
        for (TransactionSummaryDTO dto : transactions) {
            // Re-Generate unique keys to get the correct source and target nodes from the map for adding links
            String counterpartyKey = dto.getCounterpartyName().equals("Private") ? generateCounterpartyPrivateNodeKey(dto) : generateCounterpartyCompanyNodeKey(dto);
            String myCompanyAccountKey = generateNodeKey(dto.getCustomerName(), dto.getCustomerIban(), dto.getCurrency());

            ExternalNodeDTO sourceNode;
            ExternalNodeDTO targetNode;

            if (dto.getTotalOutgoing() != null && dto.getTotalOutgoing().compareTo(BigDecimal.ZERO) > 0) {  // Debit transaction
                sourceNode = nodesMap.get(myCompanyAccountKey);
                targetNode = nodesMap.get(counterpartyKey);
                String sebAccount = sourceNode.getIban();
                BigDecimal totalOutgoingAmount = dto.getTotalOutgoing().setScale(2, RoundingMode.HALF_UP);
                BigDecimal avgOutgoingAmount = (dto.getAverageOutgoing() != null ? dto.getAverageOutgoing().setScale(2, RoundingMode.HALF_UP) : totalOutgoingAmount);
                LinkDTO debitLink = createLink(sourceNode, targetNode, sebAccount,null, totalOutgoingAmount, dto.getOutgoingCount(), avgOutgoingAmount, dto.getMedianOutgoing(), dto.getEarliestOutgoingDate(), dto.getLatestOutgoingDate(), dto.getCurrency());
                debitLinks.add(debitLink);
            }
            if (dto.getTotalIncoming() != null && dto.getTotalIncoming().compareTo(BigDecimal.ZERO) > 0) {  // Credit transaction
                sourceNode = nodesMap.get(counterpartyKey);
                targetNode = nodesMap.get(myCompanyAccountKey);
                String sebAccount = targetNode.getIban();
                BigDecimal totalIncomingAmount = dto.getTotalIncoming().setScale(2, RoundingMode.HALF_UP);
                BigDecimal avgIncomingAmount = (dto.getAverageIncoming() != null ? dto.getAverageIncoming().setScale(2, RoundingMode.HALF_UP) : totalIncomingAmount);
                LinkDTO creditLink = createLink(sourceNode, targetNode, sebAccount, null,totalIncomingAmount, dto.getIncomingCount(), avgIncomingAmount, dto.getMedianIncoming(), dto.getEarliestIncomingDate(), dto.getLatestIncomingDate(), dto.getCurrency());
                creditLinks.add(creditLink);
            }
            if (dto.getNetFlow() != null && dto.getNetFlow().compareTo(BigDecimal.ZERO) >= 0) {  // Non-negative net flow
                sourceNode = nodesMap.get(counterpartyKey);
                targetNode = nodesMap.get(myCompanyAccountKey);
                String sebAccount = targetNode.getIban();
                int count = dto.getIncomingCount() + dto.getOutgoingCount();
                LocalDate earliestDate = getEarliestDate(dto.getEarliestIncomingDate(), dto.getEarliestOutgoingDate());
                LocalDate latestDate = getLatestDate(dto.getLatestIncomingDate(), dto.getLatestOutgoingDate());
                BigDecimal netFlow = dto.getNetFlow().setScale(2, RoundingMode.HALF_UP);
                BigDecimal avgNetFlow = (dto.getAverageNetFlow() != null ? dto.getAverageNetFlow().setScale(2, RoundingMode.HALF_UP) : netFlow); //if avgNetFlow is null, set it to netFlow, it means there is only one transaction
                LinkDTO creditLink = createLink(sourceNode, targetNode, sebAccount, null,netFlow, count, avgNetFlow, null, earliestDate, latestDate, dto.getCurrency());
                netflowLinks.add(creditLink);
                LinkDTO tableLink = createLink(targetNode, sourceNode, sebAccount, null,netFlow, count, avgNetFlow, null, earliestDate, latestDate, dto.getCurrency());
                netflowTable.add(tableLink);
            }
            if (dto.getNetFlow() != null && dto.getNetFlow().compareTo(BigDecimal.ZERO) < 0) {  // Negative net flow
                sourceNode = nodesMap.get(myCompanyAccountKey);
                targetNode = nodesMap.get(counterpartyKey);
                String sebAccount = sourceNode.getIban();
                int count = dto.getIncomingCount() + dto.getOutgoingCount();
                LocalDate earliestDate = getEarliestDate(dto.getEarliestIncomingDate(), dto.getEarliestOutgoingDate());
                LocalDate latestDate = getLatestDate(dto.getLatestIncomingDate(), dto.getLatestOutgoingDate());
                BigDecimal netFlow = dto.getNetFlow().setScale(2, RoundingMode.HALF_UP);
                BigDecimal avgNetFlow = (dto.getAverageNetFlow() != null ? dto.getAverageNetFlow().setScale(2, RoundingMode.HALF_UP) : netFlow); //if avgNetFlow is null, set it to netFlow, it means there is only one transaction
                LinkDTO creditLink = createLink(sourceNode, targetNode, sebAccount, null,netFlow.abs(), count, avgNetFlow.abs(), null, earliestDate, latestDate, dto.getCurrency()); //abs() to get the absolute value for links between nodes
                netflowLinks.add(creditLink);
                LinkDTO tableLink = createLink( sourceNode, targetNode, sebAccount,null ,netFlow, count, avgNetFlow, null, earliestDate, latestDate, dto.getCurrency()); //no abs() for table
                netflowTable.add(tableLink);
            }

        }

    }


    private Pair<List<CounterpartyNamesDTO>, List<CounterpartyNamesDTO>> findTopCounterpartiesNames(List<String> customerIds, LocalDate startDate, LocalDate endDate, Integer topN, String viewType) {
        List<CounterpartyNamesDTO> topCounterpartiesNames;

        if (viewType.equals("company")) topCounterpartiesNames = transactionRepository.findAllCounterpartyNamesWithTransactionType(customerIds, startDate, endDate, topN);
        else if (viewType.equals("country")) topCounterpartiesNames = transactionRepository.findCountriesCounterpartyNamesWithTransactionType(customerIds, startDate, endDate, topN);
        else throw new IllegalArgumentException("Unexpected view type: " + viewType);

        if (topCounterpartiesNames.isEmpty()) {
            log.warn("No transactions found for the given customer IDs {} in the given date range {} - {}", customerIds, startDate, endDate);
        }
        List<CounterpartyNamesDTO> topDebitCounterparties = new ArrayList<>();
        List<CounterpartyNamesDTO> topCreditCounterparties = new ArrayList<>();

        for (CounterpartyNamesDTO dto : topCounterpartiesNames) {
            if (dto.getTransactionType() == 'D') {
                topDebitCounterparties.add(dto);
            } else if (dto.getTransactionType() == 'C') {
                topCreditCounterparties.add(dto);
            } else {
                throw new IllegalArgumentException("Unexpected transaction type found: " + dto.getTransactionType());
            }
        }
        log.info("Top debit counterparties names from database: {}", topDebitCounterparties.stream().map(CounterpartyNamesDTO::getCounterpartyName).collect(Collectors.toList()));
        log.info("Top credit counterparties names from database: {}", topCreditCounterparties.stream().map(CounterpartyNamesDTO::getCounterpartyName).collect(Collectors.toList()));
        return Pair.of(topDebitCounterparties, topCreditCounterparties);
    }

    private Set<TransactionSummaryDTO> findAggregatedTransactionsWithTopCompanyCounterparties(List<String> customerIds, List<String> topDebitNames, List<String> topCreditNames, LocalDate startDate, LocalDate endDate, String viewType) {
        Set<TransactionSummaryDTO> transactions;

        if (viewType.equals("company")) transactions = transactionRepository.findCompaniesTransactionSummaries(customerIds, topDebitNames, topCreditNames, startDate, endDate);
        else if (viewType.equals("country")) transactions = transactionRepository.findCompaniesCountriesTransactionSummaries(customerIds, topDebitNames, topCreditNames, startDate, endDate);
        else throw new IllegalArgumentException("Unexpected view type: " + viewType);

        if (transactions.isEmpty()) {
            log.warn("No companies transactions found for the given customer IDs {} in the given date range {} - {} for the top {} debit counterparties and top {} credit counterparties ",
                    customerIds, startDate, endDate, topDebitNames.size(), topCreditNames.size());
        }
        log.info("Found {} companies transaction summaries for the given customer IDs {} in the given date range {} - {} for the top {} debit counterparties and top {} credit counterparties ",
                transactions.size(), customerIds, startDate, endDate, topDebitNames.size(), topCreditNames.size());
        return transactions;
    }

    private Set<TransactionSummaryDTO> findAggregatedTransactionsWithTopPrivateCounterparties(List<String> customerIds, LocalDate startDate, LocalDate endDate, String viewType) {
        Set<TransactionSummaryDTO> privateTransactions;

        if (viewType.equals("company")) privateTransactions = transactionRepository.findPrivateTransactionSummaries(customerIds, startDate, endDate);
        else if (viewType.equals("country")) privateTransactions = transactionRepository.findPrivateCountriesTransactionSummaries(customerIds, startDate, endDate);
        else throw new IllegalArgumentException("Unexpected view type: " + viewType);

        if (privateTransactions.isEmpty()) {
            log.warn("No private transactions found for the given customer IDs {} in the given date range {} - {}", customerIds, startDate, endDate);
        }
        log.info("Found {} private transaction summaries for the given customer IDs {} in the given date range {} - {}", privateTransactions.size(), customerIds, startDate, endDate);
        return privateTransactions;
    }


    //HELPER FUNCTIONS
    // Helper function to generate unique node keys (SEB Customer company accounts-currency)
    private String generateNodeKey(String name, String iban, String currency) {
        // Ensure name is not null
        String safeName = (name != null ? name.trim() : "");
        String safeIban = (iban != null ? iban.trim() : "");
        String safeCurrency = (currency != null ? currency.trim() : "");
        return safeName + "-" + safeIban + "-" + safeCurrency; //My Company-EE123456789-USD
    }

    // Helper function to generate unique Private node keys
    private String generateCounterpartyPrivateNodeKey(TransactionSummaryDTO dto) {
        if(dto.getCounterpartyBankCountry() == null) return "Private";
        else {
            String safeCountry = dto.getCounterpartyBankCountry().trim();
            return "Private-" + safeCountry; //Private-EST, Private-FIN, Private-LTU
        }
    }

    private String generateCounterpartyCompanyNodeKey(TransactionSummaryDTO dto) {
        if(dto.getCounterpartyBankCountry() == null) return dto.getCounterpartyName().trim(); //can be Private or some company name: My Company, Hitachi, etc
        else {
            return dto.getCounterpartyName().trim() + "-" + dto.getCounterpartyBankCountry().trim(); //Private-EST, Private-FIN, Private-LTU
        }
    }

    // Helper function to create link objects
    private LinkDTO createLink(ExternalNodeDTO sourceNode, ExternalNodeDTO targetNode, String sebAccount,String counterPartyAccount, BigDecimal amount, Integer count, BigDecimal avgAmount, BigDecimal median, LocalDate earliest, LocalDate latest, String currency) {
        LinkDTO transactionLink = new LinkDTO();
        transactionLink.setSource(sourceNode.getId().toString());
        transactionLink.setTarget(targetNode.getId().toString());
        transactionLink.setSourceName(sourceNode.getName());
        transactionLink.setTargetName(targetNode.getName());
        transactionLink.setSebCustomerAccount(sebAccount);
        transactionLink.setCounterpartyAccount(counterPartyAccount);
        transactionLink.setLabel("Transaction from " + sourceNode.getName() + " to " + targetNode.getName());
        transactionLink.setAmounts(convertAmount(amount)); //sets map with original amount, thousands, millions
        transactionLink.setAvgAmounts(convertAmount(avgAmount));
        transactionLink.setMedianAmounts(median != null ? convertAmount(median) : null);
        transactionLink.setCurrency(currency);
        transactionLink.setCount(count);
        transactionLink.setEarliestDate(earliest);
        transactionLink.setLatestDate(latest);
        return transactionLink;
    }
    // Helper functions to get the earliest and latest dates
    private LocalDate getEarliestDate(LocalDate date1, LocalDate date2) {
        if (date1 == null && date2 == null) {
            return null;
        } else if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        } else {
            return date1.isBefore(date2) ? date1 : date2;
        }
    }

    private LocalDate getLatestDate(LocalDate date1, LocalDate date2) {
        if (date1 == null && date2 == null) {
            return null;
        } else if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        } else {
            return date1.isAfter(date2) ? date1 : date2;
        }
    }

    private Map<String, BigDecimal> convertAmount(BigDecimal amount) {
        Map<String, BigDecimal> result = new HashMap<>();

        // Original amount
        result.put("original", amount);

        // Thousands (divide by 1,000)
        BigDecimal thousands = amount.divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);
        result.put("thousands", thousands);

        // Millions (divide by 1,000,000)
        BigDecimal millions = amount.divide(BigDecimal.valueOf(1000000), 4, RoundingMode.HALF_UP);
        result.put("millions", millions);

        return result;
    }


    //VALIDATION FUNCTIONS

    private List<String> validateCustomerIds(List<String> customerIds) {
        if (customerIds == null || customerIds.isEmpty()) {
            customerIds = customerRepository.findAllCompaniesIds();
            if (customerIds.isEmpty()) {
                log.info("No companies found in the database");
                throw new IllegalArgumentException("No companies found in the database");
            }
            log.info("No customer IDs provided. Using all companies: {}", customerIds);
            return customerIds;
        } else {
            log.info("Customer IDs provided: {}", customerIds);
            return customerIds;
        }
    }

    private Pair<LocalDate, LocalDate> validateStartDateAndEndDate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            startDate = LocalDate.of(1970, 1, 1);
            endDate = LocalDate.now();
            log.info("No start and end dates provided. Using default start date: {} and end date: {}", startDate, endDate);
        } else if (startDate == null) {
            startDate = LocalDate.of(1970, 1, 1);
            log.info("No start date provided. Using default start date: {} and provided end date: {}", startDate, endDate);
        } else if (endDate == null) {
            endDate = LocalDate.now();
            log.info("No end date provided. Using provided start date: {} and default end date: {}", startDate, endDate);
        } else if (endDate.isBefore(startDate)) {
            log.info("End date: {} cannot be before start date: {}", endDate, startDate);
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        return Pair.of(startDate, endDate);
    }

    private Integer validateTopN(Integer topN) {
        if (topN == null) {
            topN = 5;
            log.info("No topN provided. Using default topN: {}", topN);
            return topN;
        }
        else if (topN <= 0) {
            log.info("TopN cannot be less than or equal to 0: {}", topN);
            throw new IllegalArgumentException("TopN cannot be less than or equal to 0");
        }
        else {
            log.info("TopN provided: {}", topN);
            return topN;
        }
    }




    public ExternalNodesLinksWithBankDTO getExternalTransfersWithCounterpartyAcc(List<String> counterpartyKeys, List<String> customerIds, LocalDate startDate, LocalDate endDate) {

        // counterpartyKeys are name-country strings: McDonalds-EST, BurgerKing-FIN
        Pair<List<String>, List<String>> counterpartyNamesAndCountries = splitNamesAndCountries(counterpartyKeys);
        List<String> counterpartyNames = counterpartyNamesAndCountries.getLeft();
        List<String> counterpartyCountries = counterpartyNamesAndCountries.getRight();

        Set<TransactionSummaryForBankStuffDTO> data = transactionRepository.findCompaniesTransactionSummariesWithCounterpartyAccounts(counterpartyNames,customerIds,startDate,endDate,counterpartyCountries);
        data = filterData(data, counterpartyKeys); //get only data that is requested by counterpartyKeys counterpartyNames and counterpartyCountries for ex. McDonalds-EST, BurgerKing-FIN, but not McDonalds-FIN if it is not in counterpartyKeys, but may be in the data

        Map<String, ExternalNodeDTO> nodesMap = new HashMap<>();
        Map<String, LinkDTO> bankLinks = new HashMap<>();
        Set<LinkDTO> groupLinks = new HashSet<>();
        List<LinkDTO> debitLinks = new ArrayList<>();
        List<LinkDTO> creditLinks = new ArrayList<>();
        List<LinkDTO> netflowLinks = new ArrayList<>();
        List<LinkDTO> netflowTable = new ArrayList<>();

        // Step 1: Add Group node, Private node, Counterparty nodes, and SEB Customer company Account nodes
        addNodesAndGroupLinksMOD(nodesMap, groupLinks, data, bankLinks);


        // Step 2: Add Debit, Credit, and Net Flow links
        addDebitCreditAndNetFlowLinksForBank(debitLinks, creditLinks, netflowLinks, netflowTable, nodesMap, data);


        Set<ExternalNodeDTO> nodes = new HashSet<>(nodesMap.values());
        System.out.println(bankLinks.size());
        Set<LinkDTO> bankLinksSet = new HashSet<>(bankLinks.values());
        // Sort by currency, then by amount
        debitLinks.sort(Comparator
                .comparing(LinkDTO::getCurrency)
                .thenComparing(link -> link.getAmounts().get("original"), Comparator.reverseOrder())
        );
        creditLinks.sort(Comparator
                .comparing(LinkDTO::getCurrency)
                .thenComparing(link -> link.getAmounts().get("original"), Comparator.reverseOrder())
        );
        netflowTable.sort(Comparator
                .comparing(LinkDTO::getCurrency)
                .thenComparing(link -> link.getAmounts().get("original"), Comparator.reverseOrder())
        );

        return new ExternalNodesLinksWithBankDTO(nodes, groupLinks, bankLinksSet ,debitLinks, creditLinks, netflowLinks, netflowTable);

    }


    private void addNodesAndGroupLinksMOD (Map<String, ExternalNodeDTO> nodesMap, Set<LinkDTO> groupLinks, Set<TransactionSummaryForBankStuffDTO> transactions, Map<String,LinkDTO> bankLinks) {

        //Map<String,LinkDTO> bankAndAccLinks = new HashMap<>();

        // Step 1: Initialize Group node

        // Add one "My Group" node
        ExternalNodeDTO groupNode = new ExternalNodeDTO();
        groupNode.setId(0);  // ID 0 for the group node
        groupNode.setName("MainCo");
        groupNode.setCountry("EST");
        groupNode.setType("group");
        groupNode.setIban(null);
        groupNode.setCurrency(null);
        nodesMap.put("MainCo", groupNode);  // Key for the group node

        // Step 2: Add Counterparty Nodes (type: company)

        int nodeIdCounter = 1;  // initialize node id counter and start from 1 to avoid conflicts with group node ID

        for (TransactionSummaryForBankStuffDTO dto : transactions) {
            // Step 2: Add  Counterparty Bank Nodes

            String counterpartyPrivateKey = generateCounterpartyCompanyBankNodeKey(dto);  //becomes Swedbank-EST, SEB Bank-FIN
            if (!nodesMap.containsKey(counterpartyPrivateKey)) {
                ExternalNodeDTO privateNode = new ExternalNodeDTO();
                privateNode.setId(nodeIdCounter++);
                privateNode.setName(dto.getCounterpartyBankName());
                privateNode.setCountry(dto.getCounterpartyBankCountry());
                privateNode.setType("Bank");

                nodesMap.put(counterpartyPrivateKey, privateNode);


            }
            // Step 3: Add Counterparty Company Account Nodes (type: company)
            String counterpartyKey = dto.getCounterpartyName(); //Private or some company name: My Company, Hitachi, etc

            String counterpartyCompanyNodeKey = generateCounterpartyCompanyNodeKeyAccountAndBankCountry(dto); //if counterparty_bank_country is null, it will be just counterparty name otherwise counterparty name + country
            if (!nodesMap.containsKey(counterpartyCompanyNodeKey)) {
                ExternalNodeDTO counterpartyNode = new ExternalNodeDTO();
                counterpartyNode.setId(nodeIdCounter++);
                counterpartyNode.setName(dto.getCounterpartyName());
                counterpartyNode.setCountry(dto.getCounterpartyBankCountry());
                counterpartyNode.setType("company");
                counterpartyNode.setIban(dto.getCounterpartyIban());
                nodesMap.put(counterpartyCompanyNodeKey, counterpartyNode);
            }

            //Make sure bank and counterparty account has a link
            String counterpartyAccountAndBankLinkKey = generateCounterpartyCompanyBankLinkKey(dto); //Swedbank-EE1252646688, LHV Pank-FI256789456
            System.out.println(counterpartyAccountAndBankLinkKey);
            if (!bankLinks.containsKey(counterpartyAccountAndBankLinkKey)){
                LinkDTO bankLink = new LinkDTO();
                bankLink.setSource(String.valueOf(nodesMap.get(counterpartyPrivateKey).getId()));
                bankLink.setTarget(String.valueOf(nodesMap.get(counterpartyCompanyNodeKey).getId()));
                bankLink.setSourceName(dto.getCounterpartyBankName());
                bankLink.setTargetName(dto.getCounterpartyName());
                bankLink.setLabel("Bank link");
                bankLinks.put(counterpartyAccountAndBankLinkKey,bankLink);
            }
            System.out.println(bankLinks.get(counterpartyAccountAndBankLinkKey));




            // Step 4: Add SEB Customer company Account Nodes (type: account) based on iban and currency
            String myCompaniesGroupAccountKey = generateNodeKey(dto.getCustomerName(), dto.getCustomerIban(), dto.getCurrency());
            if (!nodesMap.containsKey(myCompaniesGroupAccountKey)) {
                ExternalNodeDTO accountNode = new ExternalNodeDTO();
                accountNode.setId(nodeIdCounter++);
                accountNode.setName(dto.getCustomerName());
                accountNode.setCountry("EST");  // All SEB Estonia's customers' accounts are in Estonia
                accountNode.setType("account");
                accountNode.setIban(dto.getCustomerIban());
                accountNode.setCurrency(dto.getCurrency());
                nodesMap.put(myCompaniesGroupAccountKey, accountNode);

                // Link the new account node to the "My Group" node
                LinkDTO groupLink = new LinkDTO();
                groupLink.setSource("0");  // Group node ID
                groupLink.setTarget(String.valueOf(accountNode.getId()));  // Account node ID
                groupLink.setCurrency(null);
                groupLink.setLabel("Group Link");
                groupLinks.add(groupLink);
            }
        }
    }
    private String generateCounterpartyCompanyNodeKeyAccountAndBankCountry(TransactionSummaryForBankStuffDTO dto) {
        if(dto.getCounterpartyBankCountry() == null) return dto.getCounterpartyIban().trim();
        else {
            return dto.getCounterpartyIban().trim() + "-" + dto.getCounterpartyBankCountry().trim(); //EE1353474574-EST, FI4575667969-FIN
        }
    }

    private String generateCounterpartyCompanyBankNodeKey(TransactionSummaryForBankStuffDTO dto) {
        if(dto.getCounterpartyBankCountry() == null) return dto.getCounterpartyBankName().trim(); //Swedbank, SEB, etc
        else {
            return dto.getCounterpartyBankName().trim() + "-" + dto.getCounterpartyBankCountry().trim(); //Swedbank-EST, Swedbank-FIN
        }
    }

    private String generateCounterpartyCompanyBankLinkKey(TransactionSummaryForBankStuffDTO dto) {
        if(dto.getCounterpartyBankCountry() == null) return dto.getCounterpartyName().trim(); //can be Private or some company name: My Company, Hitachi, etc
        else {
            return dto.getCounterpartyBankName().trim() + "-" + dto.getCounterpartyIban().trim(); //Swedbank-EE1252646688, LHV Pank-FI256789456
        }
    }
    private void addDebitCreditAndNetFlowLinksForBank (List<LinkDTO> debitLinks, List<LinkDTO> creditLinks, List<LinkDTO> netflowLinks, List<LinkDTO> netflowTable, Map<String, ExternalNodeDTO> nodesMap, Set<TransactionSummaryForBankStuffDTO> transactions) {

        //Create transaction links between counterparty nodes + private node and My Group accounts
        for (TransactionSummaryForBankStuffDTO dto : transactions) {
            // Re-Generate unique keys to get the correct source and target nodes from the map for adding links
            String counterpartyKey =  generateCounterpartyCompanyNodeKeyAccountAndBankCountry(dto); // EE1353474574-EST
            String myCompanyAccountKey = generateNodeKey(dto.getCustomerName(), dto.getCustomerIban(), dto.getCurrency()); //My Company-EE123456789-USD

            ExternalNodeDTO sourceNode;
            ExternalNodeDTO targetNode;

            if (dto.getTotalOutgoing() != null && dto.getTotalOutgoing().compareTo(BigDecimal.ZERO) > 0) {  // Debit transaction
                sourceNode = nodesMap.get(myCompanyAccountKey);
                targetNode = nodesMap.get(counterpartyKey);
                String sebAccount = sourceNode.getIban();
                String counterpartyAccount = targetNode.getIban();
                BigDecimal totalOutgoingAmount = dto.getTotalOutgoing().setScale(2, RoundingMode.HALF_UP);
                BigDecimal avgOutgoingAmount = (dto.getAverageOutgoing() != null ? dto.getAverageOutgoing().setScale(2, RoundingMode.HALF_UP) : totalOutgoingAmount);
                LinkDTO debitLink = createLink(sourceNode, targetNode, sebAccount, counterpartyAccount ,totalOutgoingAmount, dto.getOutgoingCount(), avgOutgoingAmount, dto.getMedianOutgoing(), dto.getEarliestOutgoingDate(), dto.getLatestOutgoingDate(), dto.getCurrency());
                debitLinks.add(debitLink);
            }
            if (dto.getTotalIncoming() != null && dto.getTotalIncoming().compareTo(BigDecimal.ZERO) > 0) {  // Credit transaction
                sourceNode = nodesMap.get(counterpartyKey);
                targetNode = nodesMap.get(myCompanyAccountKey);
                String sebAccount = targetNode.getIban();
                String counterpartyAccount = sourceNode.getIban();
                BigDecimal totalIncomingAmount = dto.getTotalIncoming().setScale(2, RoundingMode.HALF_UP);
                BigDecimal avgIncomingAmount = (dto.getAverageIncoming() != null ? dto.getAverageIncoming().setScale(2, RoundingMode.HALF_UP) : totalIncomingAmount);
                LinkDTO creditLink = createLink(sourceNode, targetNode, sebAccount,counterpartyAccount, totalIncomingAmount, dto.getIncomingCount(), avgIncomingAmount, dto.getMedianIncoming(), dto.getEarliestIncomingDate(), dto.getLatestIncomingDate(), dto.getCurrency());
                creditLinks.add(creditLink);
            }
            if (dto.getNetFlow() != null && dto.getNetFlow().compareTo(BigDecimal.ZERO) >= 0) {  // Non-negative net flow
                sourceNode = nodesMap.get(counterpartyKey);
                targetNode = nodesMap.get(myCompanyAccountKey);
                String sebAccount = targetNode.getIban();
                String counterpartyAccount = sourceNode.getIban();
                int count = dto.getIncomingCount() + dto.getOutgoingCount();
                LocalDate earliestDate = getEarliestDate(dto.getEarliestIncomingDate(), dto.getEarliestOutgoingDate());
                LocalDate latestDate = getLatestDate(dto.getLatestIncomingDate(), dto.getLatestOutgoingDate());
                BigDecimal netFlow = dto.getNetFlow().setScale(2, RoundingMode.HALF_UP);
                BigDecimal avgNetFlow = (dto.getAverageNetFlow() != null ? dto.getAverageNetFlow().setScale(2, RoundingMode.HALF_UP) : netFlow); //if avgNetFlow is null, set it to netFlow, it means there is only one transaction
                LinkDTO creditLink = createLink(sourceNode, targetNode, sebAccount, counterpartyAccount,netFlow, count, avgNetFlow, null, earliestDate, latestDate, dto.getCurrency());
                netflowLinks.add(creditLink);
                LinkDTO tableLink = createLink(targetNode, sourceNode, sebAccount,counterpartyAccount ,netFlow, count, avgNetFlow, null, earliestDate, latestDate, dto.getCurrency());
                netflowTable.add(tableLink);
            }
            if (dto.getNetFlow() != null && dto.getNetFlow().compareTo(BigDecimal.ZERO) < 0) {  // Negative net flow
                sourceNode = nodesMap.get(myCompanyAccountKey);
                targetNode = nodesMap.get(counterpartyKey);
                String sebAccount = sourceNode.getIban();
                String counterpartyAccount = targetNode.getIban();
                int count = dto.getIncomingCount() + dto.getOutgoingCount();
                LocalDate earliestDate = getEarliestDate(dto.getEarliestIncomingDate(), dto.getEarliestOutgoingDate());
                LocalDate latestDate = getLatestDate(dto.getLatestIncomingDate(), dto.getLatestOutgoingDate());
                BigDecimal netFlow = dto.getNetFlow().setScale(2, RoundingMode.HALF_UP);
                BigDecimal avgNetFlow = (dto.getAverageNetFlow() != null ? dto.getAverageNetFlow().setScale(2, RoundingMode.HALF_UP) : netFlow); //if avgNetFlow is null, set it to netFlow, it means there is only one transaction
                LinkDTO creditLink = createLink(sourceNode, targetNode, sebAccount,counterpartyAccount ,netFlow.abs(), count, avgNetFlow.abs(), null, earliestDate, latestDate, dto.getCurrency()); //abs() to get the absolute value for links between nodes
                netflowLinks.add(creditLink);
                LinkDTO tableLink = createLink( sourceNode, targetNode, sebAccount,counterpartyAccount ,netFlow, count, avgNetFlow, null, earliestDate, latestDate, dto.getCurrency()); //no abs() for table
                netflowTable.add(tableLink);
            }

        }

    }
    private Pair<List<String>, List<String>> splitNamesAndCountries(List<String> counterpartyKeys) {
        List<String> counterpartyNames = new ArrayList<>();
        List<String> counterpartyCountries = new ArrayList<>();
        for (String key : counterpartyKeys) {
            String[] parts = key.split("-");
            counterpartyNames.add(parts[0]);
            counterpartyCountries.add(parts[1]);
        }
        return Pair.of(counterpartyNames, counterpartyCountries);
    }

    private Set<TransactionSummaryForBankStuffDTO> filterData(Set<TransactionSummaryForBankStuffDTO> data, List<String> counterpartyKeys) {
        Set<TransactionSummaryForBankStuffDTO> filteredData = new HashSet<>();
        for (TransactionSummaryForBankStuffDTO dto : data) {
            if (dto.getCounterpartyName() != null && dto.getCounterpartyBankCountry() != null) {
                String key = dto.getCounterpartyName() + "-" + dto.getCounterpartyBankCountry();
                if (counterpartyKeys.contains(key)) {
                    filteredData.add(dto);
                }
            }
        }
        return filteredData;
    }




}

//list of nodes with types: "customer_account", "counterparty_account", "group", "bank".
// Customer accounts are {currency-account-country} nodes, counterparty nodes are {account-country} nodes,
// meaning multiple currencies can enter into single counterparty account node,
// but in case of customers each different currency in the same account creates separate node with that account.


