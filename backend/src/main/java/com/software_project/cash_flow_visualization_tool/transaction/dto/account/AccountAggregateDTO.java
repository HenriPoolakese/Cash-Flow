package com.software_project.cash_flow_visualization_tool.transaction.dto.account;


import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AccountAggregateDTO {

    //All accounts under 1 node (Company node)

    private List<OwnerNodeDTO> ownerNodes;
    private List<AccountLinkDTO> ownerAndAccountLink;
    private List<AccountNodeDTO> accountNodes;
    private List<AccountLinkDTO> accountLinks;
    private List<AccountNetFlowLinkDTO> netFlowLinks;





}
