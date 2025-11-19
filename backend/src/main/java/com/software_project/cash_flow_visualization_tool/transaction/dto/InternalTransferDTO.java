package com.software_project.cash_flow_visualization_tool.transaction.dto;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class InternalTransferDTO {
    private String sourceIban;
    private String destinationIban;
    private BigDecimal amount;
    private String currency;


}
