package com.software_project.cash_flow_visualization_tool.transaction.dto.account;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OwnerNodeDTO {

    private String customer_id;
    private String customer_name;
    private String country; //by default its 'EST'


}
