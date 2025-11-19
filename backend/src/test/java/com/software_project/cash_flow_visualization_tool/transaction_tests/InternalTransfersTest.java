package com.software_project.cash_flow_visualization_tool.transaction_tests;

import com.software_project.cash_flow_visualization_tool.CashFlowVisualizationToolApplication;
import com.software_project.cash_flow_visualization_tool.customer.Customer;
import com.software_project.cash_flow_visualization_tool.customer.CustomerRepository;
import com.software_project.cash_flow_visualization_tool.transaction.Transaction;
import com.software_project.cash_flow_visualization_tool.transaction.TransactionRepository;
import com.software_project.cash_flow_visualization_tool.transaction.TransactionService;
import com.software_project.cash_flow_visualization_tool.transaction.dto.account.AccountAggregateDTO;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")  // This will use application-test.yml
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
@ContextConfiguration(classes = CashFlowVisualizationToolApplication.class)
public class InternalTransfersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setUp() {
        Customer customer1 = new Customer("1", "My Company", "Corp");
        Customer customer2 = new Customer("8", "SME Company 3", "SME");

        customerRepository.saveAll(List.of(customer1, customer2));

        Transaction transaction1 = new Transaction(
                88L, 70L, customer1,
                "EE121212121212121212", "EE121240534536346436", "8", "SME Company 3",
                "GER", "SEB", "EST", "EESEB123", "Legal",
                LocalDate.of(2024, 3, 1), LocalTime.of(9, 15),
                BigDecimal.valueOf(550.00), BigDecimal.valueOf(550.00), "EUR",
                'D', 'I', "Internet Bank", Short.valueOf("2"),
                "Payment to SME Company 3", false, null, false, null, false
        );

        Transaction transaction2 = new Transaction(
                92L, 72L, customer1,
                "EE321282973364859699", "EE121240534536346436", "8", "SME Company 3",
                "GER", "SEB", "EST", "EESEB123", "Legal",
                LocalDate.of(2024, 3, 4), LocalTime.of(16, 45),
                BigDecimal.valueOf(1200.00), BigDecimal.valueOf(1200.00), "EUR",
                'D', 'I', "Internet Bank", Short.valueOf("2"),
                "Invoice payment to SME Company 3", false, null, false, null, false
        );

        Transaction transaction3 = new Transaction(
                102L, 77L, customer1,
                "EE321282973364859699", "EE121240534536346436", "8", "SME Company 3",
                "GER", "SEB", "EST", "EESEB123", "Legal",
                LocalDate.of(2024, 4, 1), LocalTime.of(9, 0),
                BigDecimal.valueOf(750.00), BigDecimal.valueOf(750.00), "EUR",
                'D', 'I', "Internet Bank", Short.valueOf("2"),
                "Monthly Subscription Fee to SME Company 3", false, null, false, null, false
        );

        transactionRepository.saveAll(List.of(transaction1, transaction2, transaction3));
    }

    @Test
    public void testRequestWithBody() throws Exception {
        String requestBody = "{\n" +
                "    \"customerIds\": [\"1\", \"8\", \"9\"],\n" +
                "    \"startDate\": \"2024-01-01\",\n" +
                "    \"endDate\": \"2024-10-27\"\n" +
                "}";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions/internal-transfers")  // Replace with actual endpoint
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println(responseBody);  // Optionally assert or inspect
    }

    @Test
    public void testAggregateNodesAndLinksStructure() {
        List<String> customerIds = List.of("1", "8");
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 27);

        AccountAggregateDTO aggregate = transactionService.getInternalTransfersBetweenCompanies(customerIds, startDate, endDate);

        // Verify owner nodes exist
        assertFalse(aggregate.getOwnerNodes().isEmpty(), "Owner nodes should not be empty");
        assertTrue(aggregate.getOwnerNodes().stream().anyMatch(node -> node.getCustomer_id().equals("1")), "Customer '1' should be present");
        assertTrue(aggregate.getOwnerNodes().stream().anyMatch(node -> node.getCustomer_id().equals("8")), "Customer '8' should be present");

        // Verify account nodes
        assertFalse(aggregate.getAccountNodes().isEmpty(), "Account nodes should not be empty");
        assertTrue(aggregate.getAccountNodes().stream().anyMatch(node -> node.getCustomer_id().equals("1")), "Account node for customer '1' should exist");
        assertTrue(aggregate.getAccountNodes().stream().anyMatch(node -> node.getCustomer_id().equals("8")), "Account node for customer '8' should exist");

        // Check presence of links
        assertFalse(aggregate.getAccountLinks().isEmpty(), "Account links should not be empty");
        assertFalse(aggregate.getNetFlowLinks().isEmpty(), "Net flow links should not be empty");
    }

    @Test
    public void testNodeOwner(){
        List<String> customerIds = List.of("1", "8");
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 27);

        AccountAggregateDTO aggregate = transactionService.getInternalTransfersBetweenCompanies(customerIds, startDate, endDate);

        assertTrue(aggregate.getOwnerNodes().get(0).getCustomer_id().equals("1"),"Owner should be 1");
        assertTrue(aggregate.getOwnerNodes().get(0).getCountry().equals("EST"),"Owner should be EST");
        assertTrue(aggregate.getOwnerNodes().get(0).getCustomer_name().equals("My Company"),"Owner should be My company");
    }

    /*@Test
    public void testAccountLink(){
        List<String> customerIds = List.of("1", "8");
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 27);

        AccountAggregateDTO aggregate = transactionService.getInternalTransfersBetweenCompanies(customerIds, startDate, endDate);
        assertTrue(aggregate.getAccountLinks().get(0).getSource().equals("EE245660161331926819"),"Should be EE121212121212121212");
        assertTrue(aggregate.getAccountLinks().get(0).getTarget().equals("EE893704004405320130"),"Should be EE893704004405320130");
        assertTrue(aggregate.getAccountLinks().get(0).getAmount().compareTo(BigDecimal.valueOf(1500.50)) == 0,"Should be 1500.50");
        assertTrue(aggregate.getAccountLinks().get(0).getCurrency().equals("EUR"),"Should be EUR");

    }*/

    /*@Test
    public void testNetFlowLink(){
        List<String> customerIds = List.of("1", "8");
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 27);

        AccountAggregateDTO aggregate = transactionService.getInternalTransfersBetweenCompanies(customerIds, startDate, endDate);

        assertTrue(aggregate.getNetFlowLinks().get(0).getSourceAccountId().equals("EE245660161331926819"),"Should be EE245660161331926819");
        assertTrue(aggregate.getNetFlowLinks().get(0).getTargetAccountId().equals("EE893704004405320130"),"Should be EE893704004405320130");
        assertTrue(aggregate.getNetFlowLinks().get(0).getNetFlow().compareTo(BigDecimal.valueOf(1500.50)) == 0,"Should be 1500.50");
        assertTrue(aggregate.getNetFlowLinks().get(0).getTransactionCount() == 1,"Should be 1");

    }*/

    @Test
    public void testAccountNode(){
        List<String> customerIds = List.of("1", "8");
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 27);

        AccountAggregateDTO aggregate = transactionService.getInternalTransfersBetweenCompanies(customerIds, startDate, endDate);

        assertTrue(aggregate.getAccountNodes().get(0).getCustomer_id().equals("1"),"Should be 1");
        assertTrue(aggregate.getAccountNodes().get(0).getCustomer_name().equals("My Company"),"Should be My Company");
        assertTrue(aggregate.getAccountNodes().get(0).getCustomer_iban().equals("EE321282973364859699"),"Should be EE321282973364859699");

    }

    @Test
    public void testOwnerAndAccountLink(){
        List<String> customerIds = List.of("1", "8");
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 27);

        AccountAggregateDTO aggregate = transactionService.getInternalTransfersBetweenCompanies(customerIds, startDate, endDate);

        assertTrue(aggregate.getOwnerAndAccountLink().get(0).getSource().equals("1"), "Should be 1");
        assertTrue(aggregate.getOwnerAndAccountLink().get(0).getTarget().equals("EE321282973364859699"), "Should be EE321282973364859699");
    }


}
