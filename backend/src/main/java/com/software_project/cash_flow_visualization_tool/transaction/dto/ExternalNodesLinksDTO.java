package com.software_project.cash_flow_visualization_tool.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExternalNodesLinksDTO {

    private Set<ExternalNodeDTO> nodes;
    private Set<LinkDTO> groupLinks;
    private List<LinkDTO> debitLinks;
    private List<LinkDTO> creditLinks;
    private List<LinkDTO> netflowLinks;
    private List<LinkDTO> netflowTable;
    private Set<CounterpartyKeyDTO> topDebitCounterparties; //companies names
    private Set<CounterpartyKeyDTO> topCreditCounterparties;

}
