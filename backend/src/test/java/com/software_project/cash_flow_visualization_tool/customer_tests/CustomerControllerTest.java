package com.software_project.cash_flow_visualization_tool.customer_tests;

import com.software_project.cash_flow_visualization_tool.customer.Customer;
import com.software_project.cash_flow_visualization_tool.customer.CustomerController;
import com.software_project.cash_flow_visualization_tool.customer.CustomerNotFoundException;
import com.software_project.cash_flow_visualization_tool.customer.CustomerRepository;
import com.software_project.cash_flow_visualization_tool.transaction.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")  // This will use application-test.yml
@Transactional  // Rolls back changes after each test
public class CustomerControllerTest {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerController customerController;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TransactionRepository transactionRepository;


    @Test
    public void testGetAllCompanies() {
        // Create and save customers in the H2 database
        Customer customer1 = new Customer("1", "Company 1", "SME");
        Customer customer2 = new Customer("2", "Company 2", "Corp");
        Customer customer3 = new Customer("3", "Private Person 1", "Private");
        Customer customer4 = new Customer("4", "Company 3", "SME");
        Customer customer5 = new Customer("5", "Private Person 2", "Private");

        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);
        customerRepository.save(customer4);
        customerRepository.save(customer5);

        // Fetch all companies
        List<Customer> customers = customerController.getAllCompanies();

        // Verify results
        assertEquals(3, customers.size());
        assertTrue(customers.stream().noneMatch(c -> c.getCustomerType().equals("Private")));

        customerRepository.deleteAll();  // Clear any existing companies
    }

    @Test
    public void testGetAllCompanies_CompaniesNotFoundException() {
        // When the database is empty, calling the controller should throw CustomerNotFoundException
        Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
            customerController.getAllCompanies();  // This will call the repository, which queries the empty H2 database
        });

        assertEquals("No companies found", exception.getMessage());  // Ensure the correct exception message is returned
    }

    @Test
    public void testGetAllCompanies_ResponseStatusOk() throws Exception {
        Customer customer1 = new Customer("1", "Company 1", "SME");
        customerRepository.save(customer1);
        // Act & Assert: Test that the correct path returns a 200 OK status
        mockMvc.perform(get("/api/customers/companies"))
                .andExpect(status().isOk());  // Expect 200 OK

        customerRepository.deleteAll();  // Clear any existing companies
    }

    @Test
    public void testGetAllCompanies_NotFound() throws Exception {
        // Act & Assert: Test that an empty response returns 404 Not Found
        mockMvc.perform(get("/api/customers/companies"))
                .andExpect(status().isNotFound());  // Expect 404 Not Found
    }

    @Test
    public void testGetAllCompanies_ContentType() throws Exception {
        Customer customer1 = new Customer("1", "Company 1", "SME");
        customerRepository.save(customer1);
        mockMvc.perform(get("/api/customers/companies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));  // Check content type is JSON

        customerRepository.deleteAll();  // Clear any existing companies
    }

    @Test
    public void testGetAllCompanies_ResponseBody() throws Exception {
        // Arrange: Create some customers in the database
        customerRepository.save(new Customer("1", "Company 1", "SME"));
        customerRepository.save(new Customer("2", "Company 2", "Corp"));

        // Act & Assert: Check that the response contains the expected customers
        mockMvc.perform(get("/api/customers/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value("1"))
                .andExpect(jsonPath("$[1].customerId").value("2"))
                .andExpect(jsonPath("$[0].customerName").value("Company 1"))
                .andExpect(jsonPath("$[1].customerName").value("Company 2"));
    }

}
