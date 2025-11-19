package com.software_project.cash_flow_visualization_tool.customer;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@AllArgsConstructor
//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("api/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    @GetMapping("/companies")
    public List<Customer> getAllCompanies() {
        List<Customer> companies = customerRepository.findAllCompanies();
        if (companies.isEmpty()) {
            throw new CustomerNotFoundException("No companies found");
        }
        return companies;
    }
}
