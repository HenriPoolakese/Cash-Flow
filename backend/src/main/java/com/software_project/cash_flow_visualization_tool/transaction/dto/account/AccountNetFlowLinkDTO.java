package com.software_project.cash_flow_visualization_tool.transaction.dto.account;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AccountNetFlowLinkDTO {
    private String sourceAccountId;
    private String targetAccountId;
    private BigDecimal flowAmountToSource;  // flow to source account
    private BigDecimal flowAmountToTarget;  // flow to target account
    private BigDecimal netFlow;             // NET
    private String currency;
    private int transactionCount;
    private BigDecimal average;

    private BigDecimal amountK;
    private BigDecimal amountM;
    private BigDecimal averageK;
    private BigDecimal averageM;
    private LocalDate earliestDate;
    private LocalDate latestDate;

    private List<BigDecimal> flowAmounts = new ArrayList<>();
    private BigDecimal median;
    private BigDecimal medianK;
    private BigDecimal medianM;

    public void updateFlowAmounts(BigDecimal tran){
        flowAmounts.add(tran);
    }
    public void updateNetFlow() {
        netFlow = flowAmountToSource.subtract(flowAmountToTarget).abs();
    }

    public void updateCount() {
        transactionCount++;
    }

    public void updateAverage(){
        average = netFlow.divide(BigDecimal.valueOf(transactionCount), RoundingMode.HALF_UP);
        averageK = netFlow.divide(BigDecimal.valueOf(1000*transactionCount), RoundingMode.HALF_UP);
        averageM = netFlow.divide(BigDecimal.valueOf(1000000*transactionCount), RoundingMode.HALF_UP);
    }

    public void updateAmounts(){
        amountK = netFlow.divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);
        amountM = netFlow.divide(BigDecimal.valueOf(1000000), RoundingMode.HALF_UP);
    }

    public void updateMedian(){
        flowAmounts.sort(BigDecimal::compareTo);
        if (flowAmounts.size() == 1 ){
            median = flowAmounts.get(0);
            medianK = median.divide(BigDecimal.valueOf(1000),RoundingMode.HALF_UP);
            medianM = median.divide(BigDecimal.valueOf(1000000),RoundingMode.HALF_UP);
        } else if (flowAmounts.size() % 2 == 0) {
            BigDecimal middle1 = flowAmounts.get(flowAmounts.size() / 2 - 1);
            BigDecimal middle2 = flowAmounts.get(flowAmounts.size() / 2);
            median = middle1.add(middle2).divide(new BigDecimal(2),RoundingMode.HALF_UP);
            medianK = median.divide(BigDecimal.valueOf(1000),RoundingMode.HALF_UP);
            medianM = median.divide(BigDecimal.valueOf(1000000),RoundingMode.HALF_UP);
        }else {
            median = flowAmounts.get(flowAmounts.size()/2);
            medianK = median.divide(BigDecimal.valueOf(1000),RoundingMode.HALF_UP);
            medianM = median.divide(BigDecimal.valueOf(1000000),RoundingMode.HALF_UP);
        }
    }

}
