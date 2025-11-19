package com.software_project.cash_flow_visualization_tool.transaction.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class ExternalTransfersCounterpartyAccountRequestDTO {
    @NotNull(message = "counterpartyKeys cannot be null, must be list ['Companyname1-XXX', 'Companyname2-XXX', ... ] where XXX is country symbol such as 'EST' ")
    private List<String> counterpartyKeys;
    private List<String> customerIds;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
}
