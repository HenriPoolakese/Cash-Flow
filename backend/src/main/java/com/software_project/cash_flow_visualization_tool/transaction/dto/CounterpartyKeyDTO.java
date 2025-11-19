package com.software_project.cash_flow_visualization_tool.transaction.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CounterpartyKeyDTO {
    private String counterpartyName;
    private String counterpartyBankCountry;
    private char transactionType;
    private String counterpartyKey; //for sending front-end
}
