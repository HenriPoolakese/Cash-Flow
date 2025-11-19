package com.software_project.cash_flow_visualization_tool.transaction.dto.account;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface InternalTransactionSummaryDTO {
    String getCustomerId();
    String getCounterpartyId();
    String getCustomerName();
    String getCustomerIban();
    String getCounterpartyName();
    String getCounterpartyIban();
    String getCounterpartyBankCountry();
    String getCustomertype();
    String getCurrency();
    BigDecimal getTotalIncoming();
    BigDecimal getTotalOutgoing();
    BigDecimal getNetFlow();
    Integer getIncomingCount();
    Integer getOutgoingCount();
    BigDecimal getAverageIncoming();
    BigDecimal getAverageOutgoing();
    BigDecimal getAverageNetFlow();
    LocalDate getEarliestIncomingDate();
    LocalDate getLatestIncomingDate();
    LocalDate getEarliestOutgoingDate();
    LocalDate getLatestOutgoingDate();
}
