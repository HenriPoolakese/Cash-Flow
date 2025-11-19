package com.software_project.cash_flow_visualization_tool.transaction.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class ExternalTransfersRequestDTO {
    private List<String> customerIds;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    private Integer topN;
    @NotNull(message = "viewType is required, must be either 'company' or 'country' and cannot be null")
    @Pattern(regexp = "company|country", message = "viewType must be either 'company' or 'country'")
    private String viewType; //"company" or "country"

}
