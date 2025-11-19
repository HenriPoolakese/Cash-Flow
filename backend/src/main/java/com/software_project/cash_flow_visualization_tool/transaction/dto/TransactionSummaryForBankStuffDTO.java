package com.software_project.cash_flow_visualization_tool.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionSummaryForBankStuffDTO {
    String getCustomerId();
    String getCustomerName();
    String getCustomerIban();
    String getCounterpartyName();
    String getCounterpartyIban();
    String getCounterpartyBankCountry();
    String getCounterpartyBankBicCode();
    String getCounterpartyBankName();

    String getCurrency();
    BigDecimal getTotalIncoming();
    BigDecimal getTotalOutgoing();
    BigDecimal getNetFlow();
    Integer getIncomingCount();
    Integer getOutgoingCount();
    BigDecimal getAverageIncoming();
    BigDecimal getAverageOutgoing();
    BigDecimal getAverageNetFlow();
    BigDecimal getMedianIncoming();
    BigDecimal getMedianOutgoing();
    LocalDate getEarliestIncomingDate();
    LocalDate getLatestIncomingDate();
    LocalDate getEarliestOutgoingDate();
    LocalDate getLatestOutgoingDate();
}
