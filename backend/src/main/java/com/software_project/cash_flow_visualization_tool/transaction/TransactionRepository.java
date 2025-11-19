package com.software_project.cash_flow_visualization_tool.transaction;
import com.software_project.cash_flow_visualization_tool.transaction.dto.CounterpartyNamesDTO;
import com.software_project.cash_flow_visualization_tool.transaction.dto.TransactionSummaryDTO;
import com.software_project.cash_flow_visualization_tool.transaction.dto.TransactionSummaryForBankStuffDTO;
import com.software_project.cash_flow_visualization_tool.transaction.dto.account.InternalTransactionSummaryDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Method to find all internal transfers within the company accounts
    /*@Query("SELECT t.customerIban AS source, t.counterpartyIban AS target, t.currency AS currency, SUM(t.amountOrg) AS amount "
            + "FROM Transaction t "
            + "WHERE t.customer.customerId = :customerId "
            + "AND t.counterpartyId = :customerId "
            + "AND t.dc = 'D' "
            + "AND t.transactionId NOT IN (" // this here filters out the rows where transactions are reversed, the reversing and original transactions
            + "    SELECT tr.transactionId FROM Transaction tr WHERE tr.isRvrsF = true "
            + "    UNION "
            + "    SELECT tr.isRvrsOrigId FROM Transaction tr WHERE tr.isRvrsF = true"
            + ") " +
            "GROUP BY t.customerIban, t.counterpartyIban, t.currency")
    List<Object[]> findInternalTransfers(@Param("customerId") String customerId);*/

    @Query(value = "SELECT " +
            "t.customer_id AS customerId, " +
            "t.counterparty_id AS counterpartyId, " +
            "c.customer_name AS customerName, " +
            "t.customer_iban AS customerIban, " +
            "t.counterparty_name AS counterpartyName, " +
            "t.counterparty_iban AS counterPartyIban, " +
            "t.counterparty_bank_country AS counterPartyBankCountry, " +
            "c.customer_type as customerType, " +
            "t.currency AS currency, " +
            "SUM(CASE WHEN t.dc = 'C' THEN t.amount_org ELSE 0 END) AS totalIncoming, " +
            "SUM(CASE WHEN t.dc = 'D' THEN t.amount_org ELSE 0 END) AS totalOutgoing, " +
            "SUM(CASE WHEN t.dc = 'C' THEN t.amount_org ELSE 0 END) - " +
            "SUM(CASE WHEN t.dc = 'D' THEN t.amount_org ELSE 0 END) AS netFlow, " +
            "COUNT(CASE WHEN t.dc = 'C' THEN 1 END) AS incomingCount, " +
            "COUNT(CASE WHEN t.dc = 'D' THEN 1 END) AS outgoingCount, " +
            "AVG(CASE WHEN t.dc = 'C' THEN t.amount_org END) AS averageIncoming, " +
            "AVG(CASE WHEN t.dc = 'D' THEN t.amount_org END) AS averageOutgoing, " +
            "AVG(CASE WHEN t.dc = 'C' THEN t.amount_org END) - " +
            "AVG(CASE WHEN t.dc = 'D' THEN t.amount_org END) AS averageNetFlow, " +
            "MIN(CASE WHEN t.dc = 'C' THEN t.date END) AS earliestIncomingDate, " +
            "MAX(CASE WHEN t.dc = 'C' THEN t.date END) AS latestIncomingDate, " +
            "MIN(CASE WHEN t.dc = 'D' THEN t.date END) AS earliestOutgoingDate, " +
            "MAX(CASE WHEN t.dc = 'D' THEN t.date END) AS latestOutgoingDate " +
            "FROM transaction t " +
            "JOIN customer c ON t.customer_id = c.customer_id " +
            "WHERE t.customer_id IN :customerIds " +
            "AND t.counterparty_id IN :customerIds " +
            "AND t.date >= :startDate AND t.date <= :endDate " +
            "AND t.counterparty_type = 'Legal' " +
            "AND t.was_later_rvrs_f = false AND t.is_rvrs_f = false AND t.fee_f = false " +
            "GROUP BY t.customer_id, c.customer_name, t.customer_iban, t.counterparty_name, " +
            "t.counterparty_iban, t.counterparty_id, t.currency, t.counterparty_bank_country, c.customer_type " +
            "ORDER BY t.currency",
            nativeQuery = true)
    Set<InternalTransactionSummaryDTO> findInternalTransactionSummaries(
            @Param("customerIds") List<String> customerIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);



    @Query(value = "SELECT " +
            " t.customer_id AS customer_id, " +
            " c.customer_name AS customer_name, " +
            " c.customer_type AS customer_type, " +
            " t.customer_iban AS customer_iban, " +
            " t.counterparty_iban AS target, " +
            " SUM(t.amount_org) AS amount, " +
            " t.currency AS currency, " +
            " t.counterparty_country AS counterpartycountry, " +
            " t.counterparty_id AS counterparty_id, " +
            " t.counterparty_name AS counterparty_name, " +
            " t.counterparty_bank_country AS counterparty_bank_country, " +
            " t.date AS date " +
            "FROM transaction t " +
            "join customer c on t.customer_id = c.customer_id " +
            "WHERE c.customer_id IN :customerIds " +
            "AND t.counterparty_id IN :customerIds " +
            "AND t.date >= :startDate AND t.date <= :endDate " +
            "AND t.dc = 'D' " +
            "AND t.was_later_rvrs_f = false AND t.is_rvrs_f = false AND t.fee_f = FALSE " +
            "GROUP BY t.customer_id, t.customer_iban, t.counterparty_iban, " +
            "         t.currency, t.counterparty_country, t.customer_id, c.customer_name, " +
            "         c.customer_type, t.counterparty_id, t.counterparty_name, t.counterparty_bank_country,t.date", nativeQuery = true)
    List<Object[]> findInternalTransfersBetweenCompanies(
            @Param("customerIds") List<String> customerIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);



    // Find counterparties names that are not among the selected group customersIds, meaning they are external in respect to the selected customers.
    // This query is used to find out who are the top debit and credit counterparties for the selected SEB customers and they are listed based on the EUR amounts of the transactions.
    @Query(value = "WITH RankedTransactions AS ( " +
            "SELECT " +
            "t.counterparty_name AS counterpartyName, " +
            "t.dc AS transactionType, " +
            "SUM(t.amount_eur) AS totalAmount, " +
            "ROW_NUMBER() OVER (PARTITION BY t.dc ORDER BY SUM(t.amount_eur) DESC) AS rn " +
            "FROM " +
            "transaction t " +
            "WHERE t.customer_id IN :customerIds " +
            "AND t.counterparty_type = 'Legal' " +
            "AND (t.counterparty_id IS NULL OR t.counterparty_id NOT IN :customerIds) " +
            "AND t.date >= :startDate AND t.date <= :endDate " +
            "AND t.was_later_rvrs_f = false AND t.is_rvrs_f = false AND fee_f = false " +
            "GROUP BY " +
            "t.counterparty_name, " +
            "t.dc " +
            ") " +
            "SELECT " +
            "counterpartyName, " +
            "transactionType, " +
            "totalAmount " +
            "FROM RankedTransactions " +
            "WHERE rn <= :topN " +
            "ORDER BY transactionType, totalAmount DESC", nativeQuery = true)
    List<CounterpartyNamesDTO> findAllCounterpartyNamesWithTransactionType(
            @Param("customerIds") List<String> customerIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("topN") int topN);

    @Query(value = "WITH RankedTransactions AS ( " +
            "SELECT " +
            "  t.counterparty_name AS counterpartyName, " +
            "  t.counterparty_bank_country AS counterpartyBankCountry, " +
            "  t.dc AS transactionType, " +
            "  SUM(t.amount_eur) AS totalAmount, " +
            "  ROW_NUMBER() OVER (PARTITION BY t.dc ORDER BY SUM(t.amount_eur) DESC) AS rn " +
            "FROM transaction t " +
            "WHERE t.customer_id IN :customerIds " +
            "  AND t.counterparty_type = 'Legal' " +
            "  AND (t.counterparty_id IS NULL OR t.counterparty_id NOT IN :customerIds) " +
            "  AND t.date >= :startDate AND t.date <= :endDate " +
            "  AND t.was_later_rvrs_f = false AND t.is_rvrs_f = false AND fee_f = false " +
            "GROUP BY " +
            "  t.counterparty_name, " +
            "  t.counterparty_bank_country, " +
            "  t.dc " +
            ") " +
            "SELECT counterpartyName, counterpartyBankCountry, transactionType, totalAmount " +
            "FROM RankedTransactions " +
            "WHERE rn <= :topN " +
            "ORDER BY transactionType, totalAmount DESC", nativeQuery = true)
    List<CounterpartyNamesDTO> findCountriesCounterpartyNamesWithTransactionType(
            @Param("customerIds") List<String> customerIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("topN") int topN);


    @Query(value = "SELECT " +
            "t.customer_id AS customerId, " +
            "c.customer_name AS customerName, " +
            "t.customer_iban AS customerIban, " +
            "t.counterparty_name AS counterpartyName, " +
            "t.currency AS currency, " +
            "ROUND(SUM(CASE WHEN t.dc = 'C' THEN t.amount_org ELSE 0 END), 2) AS totalIncoming, " +
            "ROUND(SUM(CASE WHEN t.dc = 'D' THEN t.amount_org ELSE 0 END), 2) AS totalOutgoing, " +
            "ROUND(SUM(CASE WHEN t.dc = 'C' THEN t.amount_org ELSE 0 END) - " +
            "SUM(CASE WHEN t.dc = 'D' THEN t.amount_org ELSE 0 END), 2) AS netFlow, " +
            "COUNT(CASE WHEN t.dc = 'C' THEN 1 END) AS incomingCount, " +
            "COUNT(CASE WHEN t.dc = 'D' THEN 1 END) AS outgoingCount, " +
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'C' THEN t.amount_org END), 2), 0.00) AS averageIncoming, " +  //if value will be NULL, COALESCE will return 0.00
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'D' THEN t.amount_org END), 2), 0.00) AS averageOutgoing, " +
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'C' THEN t.amount_org END), 2), 0.00) - " +
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'D' THEN t.amount_org END), 2), 0.00) AS averageNetFlow, " +
            "PERCENTILE_DISC(0.5) WITHIN GROUP (ORDER BY CASE WHEN t.dc = 'C' THEN t.amount_org END) AS medianIncoming, " +
            "PERCENTILE_DISC(0.5) WITHIN GROUP (ORDER BY CASE WHEN t.dc = 'D' THEN t.amount_org END) AS medianOutgoing, " +
            "MIN(CASE WHEN t.dc = 'C' THEN t.date END) AS earliestIncomingDate, " +
            "MAX(CASE WHEN t.dc = 'C' THEN t.date END) AS latestIncomingDate, " +
            "MIN(CASE WHEN t.dc = 'D' THEN t.date END) AS earliestOutgoingDate, " +
            "MAX(CASE WHEN t.dc = 'D' THEN t.date END) AS latestOutgoingDate " +
            "FROM transaction t " +
            "JOIN customer c ON t.customer_id = c.customer_id " +
            "WHERE t.customer_id IN :customerIds " +
            "AND t.date >= :startDate AND t.date <= :endDate " +
            "AND t.counterparty_type = 'Legal' " +
            "AND (t.counterparty_id IS NULL OR t.counterparty_id NOT IN :customerIds) " +
            "AND ((t.dc = 'D' AND t.counterparty_name IN :topDebitCounterpartyNames) " +
            "     OR (t.dc = 'C' AND t.counterparty_name IN :topCreditCounterpartyNames)) " +
            "AND t.was_later_rvrs_f = false AND t.is_rvrs_f = false AND t.fee_f = false " +
            "GROUP BY t.customer_id, c.customer_name, t.customer_iban, t.counterparty_name, t.currency " +
            "ORDER BY t.currency",
            nativeQuery = true)
    Set<TransactionSummaryDTO> findCompaniesTransactionSummaries(
            @Param("customerIds") List<String> customerIds,
            @Param("topDebitCounterpartyNames") List<String> topDebitCounterpartyNames,
            @Param("topCreditCounterpartyNames") List<String> topCreditCounterpartyNames,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT " +
            "t.customer_id AS customerId, " +
            "c.customer_name AS customerName, " +
            "t.customer_iban AS customerIban, " +
            "t.counterparty_name AS counterpartyName, " +
            "t.counterparty_bank_country AS counterpartyBankCountry, " +
            "t.currency AS currency, " +
            "ROUND(SUM(CASE WHEN t.dc = 'C' THEN t.amount_org ELSE 0 END), 2) AS totalIncoming, " +
            "ROUND(SUM(CASE WHEN t.dc = 'D' THEN t.amount_org ELSE 0 END), 2) AS totalOutgoing, " +
            "ROUND(SUM(CASE WHEN t.dc = 'C' THEN t.amount_org ELSE 0 END) - " +
            "SUM(CASE WHEN t.dc = 'D' THEN t.amount_org ELSE 0 END), 2) AS netFlow, " +
            "COUNT(CASE WHEN t.dc = 'C' THEN 1 END) AS incomingCount, " +
            "COUNT(CASE WHEN t.dc = 'D' THEN 1 END) AS outgoingCount, " +
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'C' THEN t.amount_org END), 2), 0.00) AS averageIncoming, " +  //if value will be NULL, COALESCE will return 0.00
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'D' THEN t.amount_org END), 2), 0.00) AS averageOutgoing, " +
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'C' THEN t.amount_org END), 2), 0.00) - " +
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'D' THEN t.amount_org END), 2), 0.00) AS averageNetFlow, " +
            "PERCENTILE_DISC(0.5) WITHIN GROUP (ORDER BY CASE WHEN t.dc = 'C' THEN t.amount_org END) AS medianIncoming, " +
            "PERCENTILE_DISC(0.5) WITHIN GROUP (ORDER BY CASE WHEN t.dc = 'D' THEN t.amount_org END) AS medianOutgoing, " +
            "MIN(CASE WHEN t.dc = 'C' THEN t.date END) AS earliestIncomingDate, " +
            "MAX(CASE WHEN t.dc = 'C' THEN t.date END) AS latestIncomingDate, " +
            "MIN(CASE WHEN t.dc = 'D' THEN t.date END) AS earliestOutgoingDate, " +
            "MAX(CASE WHEN t.dc = 'D' THEN t.date END) AS latestOutgoingDate " +
            "FROM transaction t " +
            "JOIN customer c ON t.customer_id = c.customer_id " +
            "WHERE t.customer_id IN :customerIds " +
            "AND t.date >= :startDate AND t.date <= :endDate " +
            "AND t.counterparty_type = 'Legal' " +
            "AND (t.counterparty_id IS NULL OR t.counterparty_id NOT IN :customerIds) " +
            "AND ((t.dc = 'D' AND t.counterparty_name IN :topDebitCounterpartyNames) " +
            "     OR (t.dc = 'C' AND t.counterparty_name IN :topCreditCounterpartyNames)) " +
            "AND t.was_later_rvrs_f = false AND t.is_rvrs_f = false AND t.fee_f = false " +
            "GROUP BY t.customer_id, c.customer_name, t.customer_iban, t.counterparty_name, t.counterparty_bank_country, t.currency " +
            "ORDER BY t.currency",
            nativeQuery = true)
    Set<TransactionSummaryDTO> findCompaniesCountriesTransactionSummaries(
            @Param("customerIds") List<String> customerIds,
            @Param("topDebitCounterpartyNames") List<String> topDebitCounterpartyNames,
            @Param("topCreditCounterpartyNames") List<String> topCreditCounterpartyNames,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT " +
            "t.customer_id AS customerId, " +
            "c.customer_name AS customerName, " +
            "t.customer_iban AS customerIban, " +
            "'Private' AS counterpartyName, " +  // Fixed value
            "t.currency AS currency, " +
            "ROUND(SUM(CASE WHEN t.dc = 'C' THEN t.amount_org ELSE 0 END), 2) AS totalIncoming, " +
            "ROUND(SUM(CASE WHEN t.dc = 'D' THEN t.amount_org ELSE 0 END), 2) AS totalOutgoing, " +
            "ROUND(SUM(CASE WHEN t.dc = 'C' THEN t.amount_org ELSE 0 END), 2) - " +
            "ROUND(SUM(CASE WHEN t.dc = 'D' THEN t.amount_org ELSE 0 END), 2) AS netFlow, " +
            "COUNT(CASE WHEN t.dc = 'C' THEN 1 END) AS incomingCount, " +
            "COUNT(CASE WHEN t.dc = 'D' THEN 1 END) AS outgoingCount, " +
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'C' THEN t.amount_org END), 2), 0.00) AS averageIncoming, " +  //if value will be NULL, COALESCE will return 0.00
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'D' THEN t.amount_org END), 2), 0.00) AS averageOutgoing, " +
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'C' THEN t.amount_org END), 2), 0.00) - " +
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'D' THEN t.amount_org END), 2), 0.00) AS averageNetFlow, " +
            "PERCENTILE_DISC(0.5) WITHIN GROUP (ORDER BY CASE WHEN t.dc = 'C' THEN t.amount_org END) AS medianIncoming, " +
            "PERCENTILE_DISC(0.5) WITHIN GROUP (ORDER BY CASE WHEN t.dc = 'D' THEN t.amount_org END) AS medianOutgoing, " +
            "MIN(CASE WHEN t.dc = 'C' THEN t.date END) AS earliestIncomingDate, " +
            "MAX(CASE WHEN t.dc = 'C' THEN t.date END) AS latestIncomingDate, " +
            "MIN(CASE WHEN t.dc = 'D' THEN t.date END) AS earliestOutgoingDate, " +
            "MAX(CASE WHEN t.dc = 'D' THEN t.date END) AS latestOutgoingDate " +
            "FROM transaction t " +
            "JOIN customer c ON t.customer_id = c.customer_id " +
            "WHERE t.customer_id IN :customerIds " +
            "AND t.counterparty_type = 'Private' " +
            "AND t.date >= :startDate " +
            "AND t.date <= :endDate " +
            "AND t.was_later_rvrs_f = false AND t.is_rvrs_f = false AND t.fee_f = false " +
            "GROUP BY t.customer_id, c.customer_name, t.customer_iban, t.currency " +
            "ORDER BY t.currency",
            nativeQuery = true)
    Set<TransactionSummaryDTO> findPrivateTransactionSummaries(
            @Param("customerIds") List<String> customerIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query(value = "SELECT " +
            "t.customer_id AS customerId, " +
            "c.customer_name AS customerName, " +
            "t.customer_iban AS customerIban, " +
            "'Private' AS counterpartyName, " +  // Fixed value
            "t.counterparty_bank_country AS counterpartyBankCountry, " +
            "t.currency AS currency, " +
            "ROUND(SUM(CASE WHEN t.dc = 'C' THEN t.amount_org ELSE 0 END), 2) AS totalIncoming, " +
            "ROUND(SUM(CASE WHEN t.dc = 'D' THEN t.amount_org ELSE 0 END), 2) AS totalOutgoing, " +
            "ROUND(SUM(CASE WHEN t.dc = 'C' THEN t.amount_org ELSE 0 END), 2) - " +
            "ROUND(SUM(CASE WHEN t.dc = 'D' THEN t.amount_org ELSE 0 END), 2) AS netFlow, " +
            "COUNT(CASE WHEN t.dc = 'C' THEN 1 END) AS incomingCount, " +
            "COUNT(CASE WHEN t.dc = 'D' THEN 1 END) AS outgoingCount, " +
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'C' THEN t.amount_org END), 2), 0.00) AS averageIncoming, " +  //if value will be NULL, COALESCE will return 0.00
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'D' THEN t.amount_org END), 2), 0.00) AS averageOutgoing, " +
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'C' THEN t.amount_org END), 2), 0.00) - " +
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'D' THEN t.amount_org END), 2), 0.00) AS averageNetFlow, " +
            "PERCENTILE_DISC(0.5) WITHIN GROUP (ORDER BY CASE WHEN t.dc = 'C' THEN t.amount_org END) AS medianIncoming, " +
            "PERCENTILE_DISC(0.5) WITHIN GROUP (ORDER BY CASE WHEN t.dc = 'D' THEN t.amount_org END) AS medianOutgoing, " +
            "MIN(CASE WHEN t.dc = 'C' THEN t.date END) AS earliestIncomingDate, " +
            "MAX(CASE WHEN t.dc = 'C' THEN t.date END) AS latestIncomingDate, " +
            "MIN(CASE WHEN t.dc = 'D' THEN t.date END) AS earliestOutgoingDate, " +
            "MAX(CASE WHEN t.dc = 'D' THEN t.date END) AS latestOutgoingDate " +
            "FROM transaction t " +
            "JOIN customer c ON t.customer_id = c.customer_id " +
            "WHERE t.customer_id IN :customerIds " +
            "AND t.counterparty_type = 'Private' " +
            "AND t.date >= :startDate " +
            "AND t.date <= :endDate " +
            "AND t.was_later_rvrs_f = false AND t.is_rvrs_f = false AND t.fee_f = false " +
            "GROUP BY t.customer_id, c.customer_name, t.customer_iban, t.counterparty_bank_country, t.currency " +
            "ORDER BY t.currency",
            nativeQuery = true)
    Set<TransactionSummaryDTO> findPrivateCountriesTransactionSummaries(
            @Param("customerIds") List<String> customerIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);





    @Query(value = "SELECT " +
            "t.customer_id AS customerId, " +
            "c.customer_name AS customerName, " +
            "t.customer_iban AS customerIban, " +
            "t.counterparty_name AS counterpartyName, " +
            "t.counterparty_iban AS counterpartyIban, "+
            "t.counterparty_bank_country AS counterpartyBankCountry, "+
            "t.counterparty_bank_bic_code AS counterpartyBankBicCode, "+
            "t.counterparty_bank_name AS counterpartyBankName, "+
            "t.currency AS currency, " +
            "ROUND(SUM(CASE WHEN t.dc = 'C' THEN t.amount_org ELSE 0 END), 2) AS totalIncoming, " +
            "ROUND(SUM(CASE WHEN t.dc = 'D' THEN t.amount_org ELSE 0 END), 2) AS totalOutgoing, " +
            "ROUND(SUM(CASE WHEN t.dc = 'C' THEN t.amount_org ELSE 0 END) - " +
            "SUM(CASE WHEN t.dc = 'D' THEN t.amount_org ELSE 0 END), 2) AS netFlow, " +
            "COUNT(CASE WHEN t.dc = 'C' THEN 1 END) AS incomingCount, " +
            "COUNT(CASE WHEN t.dc = 'D' THEN 1 END) AS outgoingCount, " +
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'C' THEN t.amount_org END), 2), 0.00) AS averageIncoming, " +  //if value will be NULL, COALESCE will return 0.00
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'D' THEN t.amount_org END), 2), 0.00) AS averageOutgoing, " +
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'C' THEN t.amount_org END), 2), 0.00) - " +
            "COALESCE(ROUND(AVG(CASE WHEN t.dc = 'D' THEN t.amount_org END), 2), 0.00) AS averageNetFlow, " +
            "PERCENTILE_DISC(0.5) WITHIN GROUP (ORDER BY CASE WHEN t.dc = 'C' THEN t.amount_org END) AS medianIncoming, " +
            "PERCENTILE_DISC(0.5) WITHIN GROUP (ORDER BY CASE WHEN t.dc = 'D' THEN t.amount_org END) AS medianOutgoing, " +
            "MIN(CASE WHEN t.dc = 'C' THEN t.date END) AS earliestIncomingDate, " +
            "MAX(CASE WHEN t.dc = 'C' THEN t.date END) AS latestIncomingDate, " +
            "MIN(CASE WHEN t.dc = 'D' THEN t.date END) AS earliestOutgoingDate, " +
            "MAX(CASE WHEN t.dc = 'D' THEN t.date END) AS latestOutgoingDate " +
            "FROM transaction t " +
            "JOIN customer c ON t.customer_id = c.customer_id " +
            "WHERE  t.customer_id IN :customerIds AND t.counterparty_name IN :counterpartyNames " +
            "AND t.date >= :startDate AND t.date <= :endDate " +
            "AND t.counterparty_type = 'Legal' " +
            "AND t.counterparty_bank_country IN :counterpartyCountries " +
            "AND (t.counterparty_id IS NULL OR t.counterparty_id NOT IN :customerIds) " +
            "AND t.was_later_rvrs_f = false AND t.is_rvrs_f = false AND t.fee_f = false " +
            "GROUP BY t.customer_id, c.customer_name, t.customer_iban, t.counterparty_name, t.counterparty_iban, " +
            " t.counterparty_bank_country, t.counterparty_bank_bic_code, t.counterparty_bank_name ,t.currency " +
            "ORDER BY t.currency",
            nativeQuery = true)
    Set<TransactionSummaryForBankStuffDTO> findCompaniesTransactionSummariesWithCounterpartyAccounts(
            @Param("counterpartyNames") List<String> counterpartyNames,
            @Param("customerIds") List<String> customerIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("counterpartyCountries") List<String> counterpartyCountries);

}



