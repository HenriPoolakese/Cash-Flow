package com.software_project.cash_flow_visualization_tool.transaction.dto.account;


import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AccountNodeDTO {

    private String customer_id;
    private String customer_name;
    private String customer_type;
    private String customer_iban;

    private String label;
    private String country;
    private String currency;





}
