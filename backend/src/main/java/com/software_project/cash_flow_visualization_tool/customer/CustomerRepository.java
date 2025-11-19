package com.software_project.cash_flow_visualization_tool.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    // Custom query to get all customers where customerType is 'SME' or 'Corp'
    @Query("SELECT c FROM Customer c WHERE c.customerType IN ('SME', 'Corp')")
    List<Customer> findAllCompanies();

    @Query("SELECT c.customerId FROM Customer c WHERE c.customerType IN ('SME', 'Corp')")
    List<String> findAllCompaniesIds();

}

