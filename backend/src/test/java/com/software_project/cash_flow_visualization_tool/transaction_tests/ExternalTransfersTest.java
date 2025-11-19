package com.software_project.cash_flow_visualization_tool.transaction_tests;

import com.software_project.cash_flow_visualization_tool.customer.Customer;
import com.software_project.cash_flow_visualization_tool.customer.CustomerController;
import com.software_project.cash_flow_visualization_tool.customer.CustomerRepository;
import com.software_project.cash_flow_visualization_tool.transaction.Transaction;
import com.software_project.cash_flow_visualization_tool.transaction.TransactionRepository;
import com.software_project.cash_flow_visualization_tool.transaction.dto.TransactionSummaryDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")  // This will use application-test.yml
//@Transactional
public class ExternalTransfersTest {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TransactionRepository transactionRepository;


    @BeforeEach
    public void setUp() {
        // Create and save customers before each test
        Customer customer1 = new Customer("1", "Company 1", "SME");
        Customer customer2 = new Customer("2", "Company 2", "Corp");
        Customer customer3 = new Customer("3", "Company 3", "Corp");
        Customer customer4 = new Customer("4", "Company 4", "Corp");

        customerRepository.saveAll(List.of(customer1, customer2, customer3, customer4));


        Transaction t1 = new Transaction(null, 62L, customer1, "EE777777777777777777", "EE214334757572345683", null, "Private Person 1", "EST", "Swedbank", "EST", "HABAEE2X", "Private", LocalDate.of(2024, 5, 14), LocalTime.of(14, 33, 15), BigDecimal.valueOf(150), BigDecimal.valueOf(150), "EUR", 'D', 'D', "Internet Bank", Short.valueOf("1"), "Payment to Private Person 1", false, null, false, null, false);
        Transaction t2 = new Transaction(null, 63L, customer2, "EE121240534536346436", "EE019275737475843233", null, "Prisma AS", "EST", "Swedbank", "EST", "HABAEE2X", "Legal", LocalDate.of(2024, 5, 15), LocalTime.of(11, 11, 12), BigDecimal.valueOf(400), BigDecimal.valueOf(400), "EUR", 'D', 'D', "Internet Bank", Short.valueOf("1"), "Payment to Prisma AS", false, null, false, null, false);
        Transaction t3 = new Transaction(null, 64L, customer2, "EE121240534536346436", "EE283630354260964635", null, "Prisma AS", "EST", "LHV", "EST", "EELHV321", "Legal", LocalDate.of(2024, 5, 16), LocalTime.of(7, 17, 15), BigDecimal.valueOf(50), BigDecimal.valueOf(50), "EUR", 'D', 'D', "Internet Bank", Short.valueOf("1"), "Payment to Prima AS", false, null, false, null, false);
        Transaction t4 = new Transaction(null, 65L, customer2, "EE121240534536346436", "EE019275737475843233", null, "Prisma AS", "EST", "Swedbank", "EST", "HABAEE2X", "Legal", LocalDate.of(2024, 5, 16), LocalTime.of(12, 31, 22), BigDecimal.valueOf(150), BigDecimal.valueOf(150), "EUR", 'C', 'D', "Internet Bank", Short.valueOf("1"), "Payment from Prisma AS", false, null, false, null, false);
        Transaction t5 = new Transaction(null, 66L, customer3, "EE124569453346799958", "NO93860111179477544", null, "Corporation 5", "NOR", "SEB Norway", "NOR", "NOSEB30", "Legal", LocalDate.of(2024, 5, 16), LocalTime.of(14, 15, 1), BigDecimal.valueOf(10000), BigDecimal.valueOf(10000), "NOK", 'D', 'F', "Internet Bank", Short.valueOf("1"), "Payment to Corporation 5", false, null, false, null, false);
        Transaction t6 = new Transaction(null, 67L, customer3, "EE124569453346799958", "NO93860111179477544", null, "Corporation 5", "NOR", "SEB Norway", "NOR", "NOSEB30", "Legal", LocalDate.of(2024, 5, 16), LocalTime.of(16, 37, 5), BigDecimal.valueOf(500), BigDecimal.valueOf(462), "USD", 'D', 'F', "Internet Bank", Short.valueOf("1"), "Payment to Corporation 5", false, null, false, null, false);
        Transaction t7 = new Transaction(null, 68L, customer3, "EE124569453346799958", "NO93860111179477544", null, "Corporation 5", "NOR", "SEB Norway", "NOR", "NOSEB30", "Legal", LocalDate.of(2024, 5, 17), LocalTime.of(14, 15, 1), BigDecimal.valueOf(899), BigDecimal.valueOf(899), "EUR", 'C', 'F', "Internet Bank", Short.valueOf("1"), "Payment from Corporation 5", false, null, false, null, false);
        Transaction t8 = new Transaction(null, 69L, customer1, "EE777777777777777777", "EE578685654588988777", null, "Private Person 2", "EST", "Swedbank", "EST", "HABAEE2X", "Private", LocalDate.of(2024, 5, 19), LocalTime.of(14, 33, 15), BigDecimal.valueOf(300), BigDecimal.valueOf(300), "EUR", 'C', 'D', "Internet Bank", Short.valueOf("1"), "Payment from Private Person 2", false, null, false, null, false);
        Transaction t9 = new Transaction(null, 70L, customer1, "EE777777777777777777", "EE976543557899544455", null, "Private Person 3", "EST", "Swedbank", "EST", "HABAEE2X", "Private", LocalDate.of(2024, 5, 20), LocalTime.of(10, 0, 0), BigDecimal.valueOf(25), BigDecimal.valueOf(25), "EUR", 'D', 'D', "Internet Bank", Short.valueOf("1"), "Payment to Private Person 3", false, null, false, null, false);
        Transaction t10 = new Transaction(null, 71L, customer1, "EE777777777777777777", "EE214334757572345683", null, "Private Person 1", "EST", "Swedbank", "EST", "HABAEE2X", "Private", LocalDate.of(2024, 5, 21), LocalTime.of(10, 0, 0), BigDecimal.valueOf(123), BigDecimal.valueOf(123), "EUR", 'C', 'D', "Internet Bank", Short.valueOf("1"), "Payment to Private Person 1", false, null, false, null, false);
        Transaction t11 = new Transaction(null, 72L, customer4, "EE456758476785847568", "EE667788543343678884", null, "Norway Fishing", "NOR", "Swedbank", "EST", "HABAEE2X", "Legal", LocalDate.of(2024, 6, 20), LocalTime.of(12, 16, 9), BigDecimal.valueOf(1300), BigDecimal.valueOf(1300), "EUR", 'D', 'F', "Internet Bank", Short.valueOf("1"), "Payment to Norway Fishing", false, null, false, null, false);
        Transaction t12 = new Transaction(null, 73L, customer4, "EE456758476785847568", "NO564283969350606030", null, "Norway Fishing", "NOR", "DNB Bank", "NOR", "DNBANOKKXXX", "Legal", LocalDate.of(2024, 6, 21), LocalTime.of(14, 20, 5), BigDecimal.valueOf(400), BigDecimal.valueOf(400), "EUR", 'D', 'F', "Internet Bank", Short.valueOf("1"), "Payment to Norway Fishing", false, null, false, null, false);
        Transaction t13 = new Transaction(null, 74L, customer4, "EE456758476785847568", "NO220568303023596683", null, "Norway Fishing", "NOR", "Sbanken", "NOR", "SBAKNOBB", "Legal", LocalDate.of(2024, 6, 25), LocalTime.of(9, 17, 55), BigDecimal.valueOf(3500), BigDecimal.valueOf(3500), "EUR", 'D', 'F', "Internet Bank", Short.valueOf("1"), "Payment to Norway Fishing", false, null, false, null, false);
        Transaction t14 = new Transaction(null, 75L, customer4, "EE456758476785847568", "NO220568303023596683", null, "Norway Fishing", "NOR", "Sbanken", "NOR", "SBAKNOBB", "Legal", LocalDate.of(2024, 6, 26), LocalTime.of(10, 11, 33), BigDecimal.valueOf(500), BigDecimal.valueOf(500), "EUR", 'C', 'F', "Internet Bank", Short.valueOf("1"), "Partial refund from Norway Fishing", false, null, false, null, false);
        Transaction t15 = new Transaction(null, 76L, customer4, "EE456758476785847568", "SE579695685869707577", null, "Norway Fishing", "NOR", "Handelsbanken", "SWE", "HANDSESSSHD", "Legal", LocalDate.of(2024, 6, 27), LocalTime.of(12, 22, 19), BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), "EUR", 'C', 'F', "Internet Bank", Short.valueOf("1"), "Payment to Norway Fishing", false, null, false, null, false);
        transactionRepository.saveAll(List.of(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15));

    }

    @AfterEach
    public void tearDown() {
        // Cleanup transactions explicitly (if needed)
        transactionRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    public void testContentType() throws Exception {

        // JSON payload
        String requestBody = """
                {
                    "customerIds": ["1", "2", "3", "4"],
                    "startDate": "2024-01-01",
                    "endDate": "2024-12-31",
                    "viewType": "company"
                }
                """;

        // Perform the POST request
        MvcResult result = mockMvc.perform(
                        post("/api/transactions/external-transfers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk()) // Assert status is 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Assert content type
                .andReturn();

    }

    @Test
    public void testFindPrivateTransactionSummaries() {
        List<String> customerIds = List.of("1", "2", "3");
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        Set<TransactionSummaryDTO> summaries = transactionRepository.findPrivateTransactionSummaries(customerIds, startDate, endDate);

        // Using BigDecimal constructor with String to preserve scale and avoid floating-point issues
        BigDecimal expectedTotalIncoming = new BigDecimal("423.00");
        BigDecimal expectedTotalOutgoing = new BigDecimal("175.00");
        BigDecimal expectedNetFlow = new BigDecimal("248.00");
        BigDecimal expectedAverageIncoming = new BigDecimal("211.50");
        BigDecimal expectedAverageOutgoing = new BigDecimal("87.50");
        BigDecimal expectedAverageNetFlow = new BigDecimal("124.00");

        // Validate results
        assertNotNull(summaries);
        assertEquals(1, summaries.size()); // Ensure grouping by customer_id, iban, and currency works

        // Retrieve the summary from the set
        TransactionSummaryDTO summary = summaries.iterator().next();

        // Verify customer and counterparty details
        assertEquals("1", summary.getCustomerId()); // Check if customer ID is "1"
        assertEquals("Company 1", summary.getCustomerName()); // Check if customer name is "Company 1"
        assertEquals("EE777777777777777777", summary.getCustomerIban()); // Check if customer IBAN matches
        assertEquals("Private", summary.getCounterpartyName()); // Check if counterparty name is "Private"
        assertEquals("EUR", summary.getCurrency()); // Check if currency is "EUR"

        // Verify total incoming amount
        assertEquals(new BigDecimal("423.00"), summary.getTotalIncoming()); // Total Incoming: 300 (C) + 123 (C)

        // Verify total outgoing amount
        assertEquals(new BigDecimal("175.00"), summary.getTotalOutgoing()); // Total Outgoing: 150 (D) + 25 (D)

        // Verify net flow (Total Incoming - Total Outgoing)
        assertEquals(new BigDecimal("248.00"), summary.getNetFlow()); // Net Flow: 423.00 - 175.00

        // Verify transaction counts
        assertEquals(2, summary.getIncomingCount()); // Incoming transactions count (Credit 'C')
        assertEquals(2, summary.getOutgoingCount()); // Outgoing transactions count (Debit 'D')

        // Verify average amounts
        assertEquals(new BigDecimal("211.50"), summary.getAverageIncoming()); // Average Incoming: (300 + 123) / 2
        assertEquals(new BigDecimal("87.50"), summary.getAverageOutgoing()); // Average Outgoing: (150 + 25) / 2
        assertEquals(new BigDecimal("124.00"), summary.getAverageNetFlow()); // Average Net Flow: 211.50 - 87.50

        // Verify earliest and latest dates for incoming transactions
        assertEquals(LocalDate.of(2024, 5, 19), summary.getEarliestIncomingDate()); // Earliest Incoming Date
        assertEquals(LocalDate.of(2024, 5, 21), summary.getLatestIncomingDate()); // Latest Incoming Date

        // Verify earliest and latest dates for outgoing transactions
        assertEquals(LocalDate.of(2024, 5, 14), summary.getEarliestOutgoingDate()); // Earliest Outgoing Date
        assertEquals(LocalDate.of(2024, 5, 20), summary.getLatestOutgoingDate()); // Latest Outgoing Date

    }

    @Test
    public void testFindCompaniesTransactionSummaries() {
        List<String> customerIds = List.of("1", "2", "3");
        List<String> topDebitNames = List.of("Prisma AS", "Corporation 5"); //companies counterparties
        List<String> topCreditNames = List.of("Prisma AS", "Corporation 5"); //companies counterparties
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        Set<TransactionSummaryDTO> summaries = transactionRepository.findCompaniesTransactionSummaries(customerIds, topDebitNames, topCreditNames, startDate, endDate);

        // Set up expected values for Customer 2, Counterparty "Prisma AS", Currency "EUR"
        BigDecimal expectedTotalIncoming = new BigDecimal("150.00");
        BigDecimal expectedTotalOutgoing = new BigDecimal("450.00");
        BigDecimal expectedNetFlow = new BigDecimal("-300.00");
        BigDecimal expectedAverageIncoming = new BigDecimal("150.00");
        BigDecimal expectedAverageOutgoing = new BigDecimal("225.00");
        BigDecimal expectedAverageNetFlow = new BigDecimal("-75.00");
        LocalDate expectedEarliestIncomingDate = LocalDate.of(2024, 5, 16);
        LocalDate expectedLatestIncomingDate = LocalDate.of(2024, 5, 16);
        LocalDate expectedEarliestOutgoingDate = LocalDate.of(2024, 5, 15);
        LocalDate expectedLatestOutgoingDate = LocalDate.of(2024, 5, 16);

        int expectedIncomingCount = 1;
        int expectedOutgoingCount = 2;

// Retrieve the summary for Customer 2
        TransactionSummaryDTO summary = null;
        for (TransactionSummaryDTO s : summaries) {
            if ("2".equals(s.getCustomerId())
                    && "Prisma AS".equals(s.getCounterpartyName())
                    && "EUR".equals(s.getCurrency())) {
                summary = s;
                break;
            }
        }
        assertNotNull(summary, "Summary for customer 2, Prisma AS, EUR not found");

// Perform assertions
        assertEquals("2", summary.getCustomerId()); // Customer ID
        assertEquals("Company 2", summary.getCustomerName()); // Customer Name
        assertEquals("EE121240534536346436", summary.getCustomerIban()); // Customer IBAN
        assertEquals("Prisma AS", summary.getCounterpartyName()); // Counterparty Name
        assertEquals("EUR", summary.getCurrency()); // Currency

// Verify amounts
        assertEquals(expectedTotalIncoming, summary.getTotalIncoming()); // Total Incoming
        assertEquals(expectedTotalOutgoing, summary.getTotalOutgoing()); // Total Outgoing
        assertEquals(expectedNetFlow, summary.getNetFlow()); // Net Flow

// Verify transaction counts
        assertEquals(expectedIncomingCount, summary.getIncomingCount()); // Incoming Count
        assertEquals(expectedOutgoingCount, summary.getOutgoingCount()); // Outgoing Count

// Verify average amounts
        assertEquals(expectedAverageIncoming, summary.getAverageIncoming()); // Average Incoming
        assertEquals(expectedAverageOutgoing, summary.getAverageOutgoing()); // Average Outgoing
        assertEquals(expectedAverageNetFlow, summary.getAverageNetFlow()); // Average Net Flow

// Verify dates for incoming transactions
        assertEquals(expectedEarliestIncomingDate, summary.getEarliestIncomingDate()); // Earliest Incoming Date
        assertEquals(expectedLatestIncomingDate, summary.getLatestIncomingDate()); // Latest Incoming Date

// Verify dates for outgoing transactions
        assertEquals(expectedEarliestOutgoingDate, summary.getEarliestOutgoingDate()); // Earliest Outgoing Date
        assertEquals(expectedLatestOutgoingDate, summary.getLatestOutgoingDate()); // Latest Outgoing Date

//----------------------------------------------------------------------------------------------------------------------

        // Set up expected values for Customer 3, Counterparty "Corporation 5", Currency "EUR"
        BigDecimal expectedTotalIncoming3 = new BigDecimal("899.00");
        BigDecimal expectedTotalOutgoing3 = new BigDecimal("0.00");
        BigDecimal expectedNetFlow3 = new BigDecimal("899.00");
        BigDecimal expectedAverageIncoming3 = new BigDecimal("899.00");
        BigDecimal expectedAverageOutgoing3 = new BigDecimal("0.00");
        BigDecimal expectedAverageNetFlow3 = new BigDecimal("899.00");
        LocalDate expectedEarliestIncomingDate3 = LocalDate.of(2024, 5, 17);
        LocalDate expectedLatestIncomingDate3 = LocalDate.of(2024, 5, 17);
        LocalDate expectedEarliestOutgoingDate3 = null;
        LocalDate expectedLatestOutgoingDate3 = null;


        int expectedIncomingCount3 = 1;
        int expectedOutgoingCount3 = 0;

// Retrieve the summary for Customer 3, EUR
        summary = null;
        for (TransactionSummaryDTO s : summaries) {
            if ("3".equals(s.getCustomerId())
                    && "Corporation 5".equals(s.getCounterpartyName())
                    && "EUR".equals(s.getCurrency())) {
                summary = s;
                break;
            }
        }
        assertNotNull(summary, "Summary for customer 3, Corporation 5, EUR not found");

// Perform assertions
        assertEquals("3", summary.getCustomerId()); // Customer ID
        assertEquals("Company 3", summary.getCustomerName()); // Customer Name
        assertEquals("EE124569453346799958", summary.getCustomerIban()); // Customer IBAN
        assertEquals("Corporation 5", summary.getCounterpartyName()); // Counterparty Name
        assertEquals("EUR", summary.getCurrency()); // Currency

// Verify amounts
        assertEquals(expectedTotalIncoming3, summary.getTotalIncoming()); // Total Incoming
        assertEquals(expectedTotalOutgoing3, summary.getTotalOutgoing()); // Total Outgoing
        assertEquals(expectedNetFlow3, summary.getNetFlow()); // Net Flow

// Verify transaction counts
        assertEquals(expectedIncomingCount3, summary.getIncomingCount()); // Incoming Count
        assertEquals(expectedOutgoingCount3, summary.getOutgoingCount()); // Outgoing Count

// Verify average amounts
        assertEquals(expectedAverageIncoming3, summary.getAverageIncoming()); // Average Incoming
        assertEquals(expectedAverageOutgoing3, summary.getAverageOutgoing()); // Average Outgoing
        assertEquals(expectedAverageNetFlow3, summary.getAverageNetFlow()); // Average Net Flow

// Verify dates for incoming transactions
        assertEquals(expectedEarliestIncomingDate3, summary.getEarliestIncomingDate()); // Earliest Incoming Date
        assertEquals(expectedLatestIncomingDate3, summary.getLatestIncomingDate()); // Latest Incoming Date

// Verify dates for outgoing transactions
        assertEquals(expectedEarliestOutgoingDate3, summary.getEarliestOutgoingDate()); // Earliest Outgoing Date
        assertEquals(expectedLatestOutgoingDate3, summary.getLatestOutgoingDate()); // Latest Outgoing Date



        //-------------------------------------------------------------------------------------------------------------



        // Set up expected values for Customer 3, Counterparty "Corporation 5", Currency "NOK"
        BigDecimal expectedTotalIncoming35NOK = new BigDecimal("0.00");
        BigDecimal expectedTotalOutgoing35NOK = new BigDecimal("10000.00");
        BigDecimal expectedNetFlow35NOK = new BigDecimal("-10000.00");
        BigDecimal expectedAverageIncoming35NOK = new BigDecimal("0.00");
        BigDecimal expectedAverageOutgoing35NOK = new BigDecimal("10000.00");
        BigDecimal expectedAverageNetFlow35NOK = new BigDecimal("-10000.00");
        LocalDate expectedEarliestIncomingDate35NOK = null;
        LocalDate expectedLatestIncomingDate35NOK = null;
        LocalDate expectedEarliestOutgoingDate35NOK = LocalDate.of(2024, 5, 16);
        LocalDate expectedLatestOutgoingDate35NOK = LocalDate.of(2024, 5, 16);

        int expectedIncomingCount35NOK = 0;
        int expectedOutgoingCount35NOK = 1;

// Retrieve the summary for Customer 3, NOK
        summary = null;
        for (TransactionSummaryDTO s : summaries) {
            if ("3".equals(s.getCustomerId())
                    && "Corporation 5".equals(s.getCounterpartyName())
                    && "NOK".equals(s.getCurrency())) {
                summary = s;
                break;
            }
        }
        assertNotNull(summary, "Summary for customer 3, Corporation 5, NOK not found");

// Perform assertions
        assertEquals("3", summary.getCustomerId()); // Customer ID
        assertEquals("Company 3", summary.getCustomerName()); // Customer Name
        assertEquals("EE124569453346799958", summary.getCustomerIban()); // Customer IBAN
        assertEquals("Corporation 5", summary.getCounterpartyName()); // Counterparty Name
        assertEquals("NOK", summary.getCurrency()); // Currency

// Verify amounts
        assertEquals(expectedTotalIncoming35NOK, summary.getTotalIncoming()); // Total Incoming
        assertEquals(expectedTotalOutgoing35NOK, summary.getTotalOutgoing()); // Total Outgoing
        assertEquals(expectedNetFlow35NOK, summary.getNetFlow()); // Net Flow

// Verify transaction counts
        assertEquals(expectedIncomingCount35NOK, summary.getIncomingCount()); // Incoming Count
        assertEquals(expectedOutgoingCount35NOK, summary.getOutgoingCount()); // Outgoing Count

// Verify average amounts
        assertEquals(expectedAverageIncoming35NOK, summary.getAverageIncoming()); // Average Incoming
        assertEquals(expectedAverageOutgoing35NOK, summary.getAverageOutgoing()); // Average Outgoing
        assertEquals(expectedAverageNetFlow35NOK, summary.getAverageNetFlow()); // Average Net Flow

// Verify dates for incoming transactions
        assertEquals(expectedEarliestIncomingDate35NOK, summary.getEarliestIncomingDate()); // Earliest Incoming Date
        assertEquals(expectedLatestIncomingDate35NOK, summary.getLatestIncomingDate()); // Latest Incoming Date

// Verify dates for outgoing transactions
        assertEquals(expectedEarliestOutgoingDate35NOK, summary.getEarliestOutgoingDate()); // Earliest Outgoing Date
        assertEquals(expectedLatestOutgoingDate35NOK, summary.getLatestOutgoingDate()); // Latest Outgoing Date


        //-------------------------------------------------------------------------------------------------------------


        // Set up expected values for Customer 3, Counterparty "Corporation 5", Currency "USD"
        BigDecimal expectedTotalIncoming35USD = new BigDecimal("0.00");
        BigDecimal expectedTotalOutgoing35USD = new BigDecimal("500.00");
        BigDecimal expectedNetFlow35USD = new BigDecimal("-500.00");
        BigDecimal expectedAverageIncoming35USD = new BigDecimal("0.00");
        BigDecimal expectedAverageOutgoing35USD = new BigDecimal("500.00");
        BigDecimal expectedAverageNetFlow35USD = new BigDecimal("-500.00");

        LocalDate expectedEarliestIncomingDate35USD = null;
        LocalDate expectedLatestIncomingDate35USD = null;
        LocalDate expectedEarliestOutgoingDate35USD = LocalDate.of(2024, 5, 16);
        LocalDate expectedLatestOutgoingDate35USD = LocalDate.of(2024, 5, 16);

        int expectedIncomingCount35USD = 0;
        int expectedOutgoingCount35USD = 1;

// Retrieve the summary for Customer 3, USD
        summary = null;
        for (TransactionSummaryDTO s : summaries) {
            if ("3".equals(s.getCustomerId())
                    && "Corporation 5".equals(s.getCounterpartyName())
                    && "USD".equals(s.getCurrency())) {
                summary = s;
                break;
            }
        }
        assertNotNull(summary, "Summary for customer 3, Corporation 5, USD not found");

// Perform assertions
        assertEquals("3", summary.getCustomerId()); // Customer ID
        assertEquals("Company 3", summary.getCustomerName()); // Customer Name
        assertEquals("EE124569453346799958", summary.getCustomerIban()); // Customer IBAN
        assertEquals("Corporation 5", summary.getCounterpartyName()); // Counterparty Name
        assertEquals("USD", summary.getCurrency()); // Currency

// Verify amounts
        assertEquals(expectedTotalIncoming35USD, summary.getTotalIncoming()); // Total Incoming
        assertEquals(expectedTotalOutgoing35USD, summary.getTotalOutgoing()); // Total Outgoing
        assertEquals(expectedNetFlow35USD, summary.getNetFlow()); // Net Flow

// Verify transaction counts
        assertEquals(expectedIncomingCount35USD, summary.getIncomingCount()); // Incoming Count
        assertEquals(expectedOutgoingCount35USD, summary.getOutgoingCount()); // Outgoing Count

// Verify average amounts
        assertEquals(expectedAverageIncoming35USD, summary.getAverageIncoming()); // Average Incoming
        assertEquals(expectedAverageOutgoing35USD, summary.getAverageOutgoing()); // Average Outgoing
        assertEquals(expectedAverageNetFlow35USD, summary.getAverageNetFlow()); // Average Net Flow

// Verify dates for incoming transactions
        assertEquals(expectedEarliestIncomingDate35USD ,summary.getEarliestIncomingDate()); // Earliest Incoming Date
        assertEquals(expectedLatestIncomingDate35USD, summary.getLatestIncomingDate()); // Latest Incoming Date

// Verify dates for outgoing transactions
        assertEquals(expectedEarliestOutgoingDate35USD, summary.getEarliestOutgoingDate()); // Earliest Outgoing Date
        assertEquals(expectedLatestOutgoingDate35USD, summary.getLatestOutgoingDate()); // Latest Outgoing Date


        }

    @Test
    public void testFindCompaniesTransactionSummaries2() {
        List<String> customerIds = List.of("4");
        List<String> topDebitNames = List.of("Norway Fishing"); //companies counterparties
        List<String> topCreditNames = List.of("Norway Fishing"); //companies counterparties
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        Set<TransactionSummaryDTO> summaries = transactionRepository.findCompaniesTransactionSummaries(customerIds, topDebitNames, topCreditNames, startDate, endDate);
        TransactionSummaryDTO summary = summaries.iterator().next();

        // Set up expected values for Customer 4, Counterparty "Norway Fishing"
        BigDecimal expectedTotalIncoming = new BigDecimal("1500.00");
        BigDecimal expectedTotalOutgoing = new BigDecimal("5200.00");
        BigDecimal expectedNetFlow = new BigDecimal("-3700.00");
        BigDecimal expectedAverageIncoming = new BigDecimal("750.00");
        BigDecimal expectedAverageOutgoing = new BigDecimal("1733.33");
        BigDecimal expectedAverageNetFlow = new BigDecimal("-983.33");
        BigDecimal expectedMedianIncoming = new BigDecimal("500.00");
        BigDecimal expectedMedianOutgoing = new BigDecimal("1300.00");
        LocalDate expectedEarliestIncomingDate = LocalDate.of(2024, 6, 26);
        LocalDate expectedLatestIncomingDate = LocalDate.of(2024, 6, 27);
        LocalDate expectedEarliestOutgoingDate = LocalDate.of(2024, 6, 20);
        LocalDate expectedLatestOutgoingDate = LocalDate.of(2024, 6, 25);

        int expectedIncomingCount = 2;
        int expectedOutgoingCount = 3;

        assertNotNull(summary, "Summary for customer 4, Norway Fishing, EUR not found");




// Perform assertions
// expect 5 transactions (t11, t12, t13, t14, t15) with Norway Fishing to produce 1 summary
        assertEquals(1, summaries.size());


        assertEquals("4", summary.getCustomerId()); // Customer ID
        assertEquals("Company 4", summary.getCustomerName()); // Customer Name
        assertEquals("EE456758476785847568", summary.getCustomerIban()); // Customer IBAN
        assertEquals("Norway Fishing", summary.getCounterpartyName()); // Counterparty Name
        assertEquals("EUR", summary.getCurrency()); // Currency

// Verify amounts
        assertEquals(expectedTotalIncoming, summary.getTotalIncoming()); // Total Incoming
        assertEquals(expectedTotalOutgoing, summary.getTotalOutgoing()); // Total Outgoing
        assertEquals(expectedNetFlow, summary.getNetFlow()); // Net Flow

// Verify transaction counts
        assertEquals(expectedIncomingCount, summary.getIncomingCount()); // Incoming Count
        assertEquals(expectedOutgoingCount, summary.getOutgoingCount()); // Outgoing Count

// Verify average amounts
        assertEquals(expectedAverageIncoming, summary.getAverageIncoming()); // Average Incoming
        assertEquals(expectedAverageOutgoing, summary.getAverageOutgoing()); // Average Outgoing
        assertEquals(expectedAverageNetFlow, summary.getAverageNetFlow()); // Average Net Flow

// Verify median amounts
        assertEquals(expectedMedianIncoming, summary.getMedianIncoming()); // Median Incoming
        assertEquals(expectedMedianOutgoing, summary.getMedianOutgoing()); // Median Outgoing

// Verify dates for incoming transactions
        assertEquals(expectedEarliestIncomingDate, summary.getEarliestIncomingDate()); // Earliest Incoming Date
        assertEquals(expectedLatestIncomingDate, summary.getLatestIncomingDate()); // Latest Incoming Date

// Verify dates for outgoing transactions
        assertEquals(expectedEarliestOutgoingDate, summary.getEarliestOutgoingDate()); // Earliest Outgoing Date
        assertEquals(expectedLatestOutgoingDate, summary.getLatestOutgoingDate());  // Latest Outgoing Date


    }

    @Test
    public void testFindCompaniesCountriesTransactionSummaries() {
        List<String> customerIds = List.of("4");
        List<String> topDebitNames = List.of("Norway Fishing"); //companies counterparties
        List<String> topCreditNames = List.of("Norway Fishing"); //companies counterparties
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        Set<TransactionSummaryDTO> summaries = transactionRepository.findCompaniesCountriesTransactionSummaries(customerIds, topDebitNames, topCreditNames, startDate, endDate);

        // Retrieve the summary for customer 4 - Norway Fishing (EST), EUR
        TransactionSummaryDTO summary = null;
        for (TransactionSummaryDTO s : summaries) {
            if ("EST".equals(s.getCounterpartyBankCountry())
                    && "Norway Fishing".equals(s.getCounterpartyName())
                    && "EUR".equals(s.getCurrency())) {
                summary = s;
                break;
            }
        }


        // Set up expected values for Customer 4, Counterparty "Norway Fishing" (EST)
        BigDecimal expectedTotalIncomingEST = new BigDecimal("0.00");
        BigDecimal expectedTotalOutgoingEST = new BigDecimal("1300.00");
        BigDecimal expectedNetFlowEST = new BigDecimal("-1300.00");
        BigDecimal expectedAverageIncomingEST = new BigDecimal("0.00");
        BigDecimal expectedAverageOutgoingEST = new BigDecimal("1300.00");
        BigDecimal expectedAverageNetFlowEST = new BigDecimal("-1300.00");
        BigDecimal expectedMedianIncomingEST = null;
        BigDecimal expectedMedianOutgoingEST = new BigDecimal("1300.00");
        LocalDate expectedEarliestIncomingDateEST = null;
        LocalDate expectedLatestIncomingDateEST = null;
        LocalDate expectedEarliestOutgoingDateEST = LocalDate.of(2024, 6, 20);
        LocalDate expectedLatestOutgoingDateEST = LocalDate.of(2024, 6, 20);

        int expectedIncomingCountEST = 0;
        int expectedOutgoingCountEST = 1;

// Perform assertions

        assertNotNull(summary, "Summary for customer 4, Norway Fishing (EST), EUR not found");

        assertEquals(3, summaries.size()); // Ensure counterparties are grouped by country

        assertEquals("4", summary.getCustomerId()); // Customer ID
        assertEquals("Company 4", summary.getCustomerName()); // Customer Name
        assertEquals("EE456758476785847568", summary.getCustomerIban()); // Customer IBAN
        assertEquals("Norway Fishing", summary.getCounterpartyName()); // Counterparty Name
        assertEquals("EST", summary.getCounterpartyBankCountry()); // Counterparty Bank Country
        assertEquals("EUR", summary.getCurrency()); // Currency

// Verify amounts
        assertEquals(expectedTotalIncomingEST, summary.getTotalIncoming()); // Total Incoming
        assertEquals(expectedTotalOutgoingEST, summary.getTotalOutgoing()); // Total Outgoing
        assertEquals(expectedNetFlowEST, summary.getNetFlow()); // Net Flow

// Verify transaction counts
        assertEquals(expectedIncomingCountEST, summary.getIncomingCount()); // Incoming Count
        assertEquals(expectedOutgoingCountEST, summary.getOutgoingCount()); // Outgoing Count

// Verify average amounts
        assertEquals(expectedAverageIncomingEST, summary.getAverageIncoming()); // Average Incoming
        assertEquals(expectedAverageOutgoingEST, summary.getAverageOutgoing()); // Average Outgoing
        assertEquals(expectedAverageNetFlowEST, summary.getAverageNetFlow()); // Average Net Flow

        // Verify median amounts
        assertEquals(expectedMedianIncomingEST, summary.getMedianIncoming()); // Median Incoming
        assertEquals(expectedMedianOutgoingEST, summary.getMedianOutgoing()); // Median Outgoing

// Verify dates for incoming transactions
        assertEquals(expectedEarliestIncomingDateEST, summary.getEarliestIncomingDate()); // Earliest Incoming Date
        assertEquals(expectedLatestIncomingDateEST, summary.getLatestIncomingDate()); // Latest Incoming Date

// Verify dates for outgoing transactions
        assertEquals(expectedEarliestOutgoingDateEST, summary.getEarliestOutgoingDate()); // Earliest Outgoing Date
        assertEquals(expectedLatestOutgoingDateEST, summary.getLatestOutgoingDate());  // Latest Outgoing Date



        //-------------------------------------------------------------------------------------------------------------

        // Retrieve the summary for customer 4 - Norway Fishing (NOR), EUR
        summary = null;
        for (TransactionSummaryDTO s : summaries) {
            if ("NOR".equals(s.getCounterpartyBankCountry())
                    && "Norway Fishing".equals(s.getCounterpartyName())
                    && "EUR".equals(s.getCurrency())) {
                summary = s;
                break;
            }
        }

        // Set up expected values for Customer 4, Counterparty "Norway Fishing" (NOR)
        BigDecimal expectedTotalIncomingNOR = new BigDecimal("500.00");
        BigDecimal expectedTotalOutgoingNOR = new BigDecimal("3900.00");
        BigDecimal expectedNetFlowNOR = new BigDecimal("-3400.00");
        BigDecimal expectedAverageIncomingNOR = new BigDecimal("500.00");
        BigDecimal expectedAverageOutgoingNOR = new BigDecimal("1950.00");
        BigDecimal expectedAverageNetFlowNOR = new BigDecimal("-1450.00");
        BigDecimal expectedMedianIncomingNOR = new BigDecimal("500.00");
        BigDecimal expectedMedianOutgoingNOR = new BigDecimal("400.00");
        LocalDate expectedEarliestIncomingDateNOR = LocalDate.of(2024, 6, 26);
        LocalDate expectedLatestIncomingDateNOR = LocalDate.of(2024, 6, 26);
        LocalDate expectedEarliestOutgoingDateNOR = LocalDate.of(2024, 6, 21);
        LocalDate expectedLatestOutgoingDateNOR = LocalDate.of(2024, 6, 25);

        int expectedIncomingCountNOR = 1;
        int expectedOutgoingCountNOR = 2;

        // Perform assertions
        assertNotNull(summary, "Summary for customer 4, Norway Fishing (NOR), EUR not found");

        assertEquals("4", summary.getCustomerId()); // Customer ID
        assertEquals("Company 4", summary.getCustomerName()); // Customer Name
        assertEquals("EE456758476785847568", summary.getCustomerIban()); // Customer IBAN
        assertEquals("Norway Fishing", summary.getCounterpartyName()); // Counterparty Name
        assertEquals("NOR", summary.getCounterpartyBankCountry()); // Counterparty Bank Country
        assertEquals("EUR", summary.getCurrency()); // Currency

// Verify amounts
        assertEquals(expectedTotalIncomingNOR, summary.getTotalIncoming()); // Total Incoming
        assertEquals(expectedTotalOutgoingNOR, summary.getTotalOutgoing()); // Total Outgoing
        assertEquals(expectedNetFlowNOR, summary.getNetFlow()); // Net Flow

// Verify transaction counts
        assertEquals(expectedIncomingCountNOR, summary.getIncomingCount()); // Incoming Count
        assertEquals(expectedOutgoingCountNOR, summary.getOutgoingCount()); // Outgoing Count

// Verify average amounts
        assertEquals(expectedAverageIncomingNOR, summary.getAverageIncoming()); // Average Incoming
        assertEquals(expectedAverageOutgoingNOR, summary.getAverageOutgoing()); // Average Outgoing
        assertEquals(expectedAverageNetFlowNOR, summary.getAverageNetFlow()); // Average Net Flow

        // Verify median amounts
        assertEquals(expectedMedianIncomingNOR, summary.getMedianIncoming()); // Median Incoming
        assertEquals(expectedMedianOutgoingNOR, summary.getMedianOutgoing()); // Median Outgoing

// Verify dates for incoming transactions
        assertEquals(expectedEarliestIncomingDateNOR, summary.getEarliestIncomingDate()); // Earliest Incoming Date
        assertEquals(expectedLatestIncomingDateNOR, summary.getLatestIncomingDate()); // Latest Incoming Date

// Verify dates for outgoing transactions
        assertEquals(expectedEarliestOutgoingDateNOR, summary.getEarliestOutgoingDate()); // Earliest Outgoing Date
        assertEquals(expectedLatestOutgoingDateNOR, summary.getLatestOutgoingDate());  // Latest Outgoing Date



        //-------------------------------------------------------------------------------------------------------------


        // Retrieve the summary for customer 4 - Norway Fishing (NOR), EUR
        summary = null;
        for (TransactionSummaryDTO s : summaries) {
            if ("SWE".equals(s.getCounterpartyBankCountry())
                    && "Norway Fishing".equals(s.getCounterpartyName())
                    && "EUR".equals(s.getCurrency())) {
                summary = s;
                break;
            }
        }

        // Set up expected values for Customer 4, Counterparty "Norway Fishing" (SWE)
        BigDecimal expectedTotalIncomingSWE = new BigDecimal("1000.00");
        BigDecimal expectedTotalOutgoingSWE = new BigDecimal("0.00");
        BigDecimal expectedNetFlowSWE = new BigDecimal("1000.00");
        BigDecimal expectedAverageIncomingSWE = new BigDecimal("1000.00");
        BigDecimal expectedAverageOutgoingSWE = new BigDecimal("0.00");
        BigDecimal expectedAverageNetFlowSWE = new BigDecimal("1000.00");
        BigDecimal expectedMedianIncomingSWE = new BigDecimal("1000.00");
        BigDecimal expectedMedianOutgoingSWE = null;
        LocalDate expectedEarliestIncomingDateSWE = LocalDate.of(2024, 6, 27);
        LocalDate expectedLatestIncomingDateSWE = LocalDate.of(2024, 6, 27);
        LocalDate expectedEarliestOutgoingDateSWE = null;
        LocalDate expectedLatestOutgoingDateSWE = null;

        int expectedIncomingCountSWE = 1;
        int expectedOutgoingCountSWE = 0;

        // Perform assertions

        assertNotNull(summary, "Summary for customer 4, Norway Fishing (SWE), EUR not found");

        assertEquals("4", summary.getCustomerId()); // Customer ID
        assertEquals("Company 4", summary.getCustomerName()); // Customer Name
        assertEquals("EE456758476785847568", summary.getCustomerIban()); // Customer IBAN
        assertEquals("Norway Fishing", summary.getCounterpartyName()); // Counterparty Name
        assertEquals("SWE", summary.getCounterpartyBankCountry()); // Counterparty Bank Country
        assertEquals("EUR", summary.getCurrency()); // Currency

// Verify amounts
        assertEquals(expectedTotalIncomingSWE, summary.getTotalIncoming()); // Total Incoming
        assertEquals(expectedTotalOutgoingSWE, summary.getTotalOutgoing()); // Total Outgoing
        assertEquals(expectedNetFlowSWE, summary.getNetFlow()); // Net Flow

// Verify transaction counts
        assertEquals(expectedIncomingCountSWE, summary.getIncomingCount()); // Incoming Count
        assertEquals(expectedOutgoingCountSWE, summary.getOutgoingCount()); // Outgoing Count

// Verify average amounts
        assertEquals(expectedAverageIncomingSWE, summary.getAverageIncoming()); // Average Incoming
        assertEquals(expectedAverageOutgoingSWE, summary.getAverageOutgoing()); // Average Outgoing
        assertEquals(expectedAverageNetFlowSWE, summary.getAverageNetFlow()); // Average Net Flow

        // Verify median amounts
        assertEquals(expectedMedianIncomingSWE, summary.getMedianIncoming()); // Median Incoming
        assertEquals(expectedMedianOutgoingSWE, summary.getMedianOutgoing()); // Median Outgoing

// Verify dates for incoming transactions
        assertEquals(expectedEarliestIncomingDateSWE, summary.getEarliestIncomingDate()); // Earliest Incoming Date
        assertEquals(expectedLatestIncomingDateSWE, summary.getLatestIncomingDate()); // Latest Incoming Date

// Verify dates for outgoing transactions
        assertEquals(expectedEarliestOutgoingDateSWE, summary.getEarliestOutgoingDate()); // Earliest Outgoing Date
        assertEquals(expectedLatestOutgoingDateSWE, summary.getLatestOutgoingDate());  // Latest Outgoing Date

    }





}








