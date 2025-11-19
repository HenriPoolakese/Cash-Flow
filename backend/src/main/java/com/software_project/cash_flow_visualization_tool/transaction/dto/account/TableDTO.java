package com.software_project.cash_flow_visualization_tool.transaction.dto.account;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TableDTO {
    private String source;
    private String target;
    private String sourceName;
    private String targetName;
    private String label;
    private Map<String, BigDecimal> amounts;
    private Map<String, BigDecimal> avgAmounts;
    private String currency;
    private int count;
    private LocalDate earliestDate;
    private LocalDate latestDate;
}
