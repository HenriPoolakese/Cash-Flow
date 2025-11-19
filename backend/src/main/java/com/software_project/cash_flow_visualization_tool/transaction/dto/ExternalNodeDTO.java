package com.software_project.cash_flow_visualization_tool.transaction.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ExternalNodeDTO {
    private Integer id;
    private String name;
    private String country;
    private String type; // company node or account node or Bank
    private String iban; // can be null for company nodes
    private String currency;
}
