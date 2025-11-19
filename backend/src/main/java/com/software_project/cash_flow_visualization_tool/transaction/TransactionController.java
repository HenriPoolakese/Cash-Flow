package com.software_project.cash_flow_visualization_tool.transaction;

import com.software_project.cash_flow_visualization_tool.transaction.dto.ExternalNodesLinksDTO;
import com.software_project.cash_flow_visualization_tool.transaction.dto.ExternalNodesLinksWithBankDTO;
import com.software_project.cash_flow_visualization_tool.transaction.dto.ExternalTransfersCounterpartyAccountRequestDTO;
import com.software_project.cash_flow_visualization_tool.transaction.dto.ExternalTransfersRequestDTO;
import com.software_project.cash_flow_visualization_tool.transaction.dto.account.AccountAggregateDTO;

import com.software_project.cash_flow_visualization_tool.transaction.dto.account.TableDTO;
import com.software_project.cash_flow_visualization_tool.transaction.dto.account.TransferRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;


import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // New endpoint to get internal transfers
    /*
    @GetMapping("/internal-transfers-one-company/{myCompanyId}")
    public AccountAggregateDTO getInternalTransfersOneCompany(
        @PathVariable String myCompanyId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String dateSTART,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String dateEND) {
            System.out.println("Received myCompanyId: " + myCompanyId);
            System.out.println("Received dateSTART: " + dateSTART);
            System.out.println("Received dateEND: " + dateEND);

            AccountAggregateDTO result = transactionService.getInternalTransfersOneCustomer(myCompanyId, dateSTART, dateEND);

            if (result.getNodes().isEmpty()) {
                throw new InternalTransactionsNotFoundException("No internal transactions found");
            }

            return result;
        }*/

    @PostMapping("/internal-transfers")
    public AccountAggregateDTO getInternalTransfersBetweenCompanies(@RequestBody TransferRequestDTO transferRequestDTO) {
        log.info("Received customerIds: {}", transferRequestDTO.getCustomerIds());
        log.info("Received startDate: {}", transferRequestDTO.getStartDate());
        log.info("Received endDate: {}", transferRequestDTO.getEndDate());

        return transactionService.getInternalTransfersBetweenCompanies(
                transferRequestDTO.getCustomerIds(),
                transferRequestDTO.getStartDate() != null ? transferRequestDTO.getStartDate() : null,
                transferRequestDTO.getEndDate() != null ? transferRequestDTO.getEndDate() : null
        );
    }

    @PostMapping("/internal-transaction-summaries")
    public List<TableDTO> getInternalTransactionSummaries(@RequestBody TransferRequestDTO transferRequestDTO) {
        log.info("Received customerIds: {}", transferRequestDTO.getCustomerIds());
        log.info("Received startDate: {}", transferRequestDTO.getStartDate());
        log.info("Received endDate: {}", transferRequestDTO.getEndDate());

        // Convert string to LocalDate if needed (optional, if your DTO uses String)
        LocalDate startDate = transferRequestDTO.getStartDate() != null ? transferRequestDTO.getStartDate() : null;
        LocalDate endDate = transferRequestDTO.getEndDate() != null ? transferRequestDTO.getEndDate() : null;

        return transactionService.getInternalTransactionSummaries(
                transferRequestDTO.getCustomerIds(),
                startDate,
                endDate
        );
    }

    @PostMapping("/external-transfers-with-counterparties-accounts")
    public ExternalNodesLinksWithBankDTO getExternalTransfersWithCounterpartyAccounts(@RequestBody @Validated ExternalTransfersCounterpartyAccountRequestDTO request) {
        return transactionService.getExternalTransfersWithCounterpartyAcc(
                request.getCounterpartyKeys(),
                request.getCustomerIds(),
                request.getStartDate(),
                request.getEndDate()
        );

    }
    @PostMapping("/external-transfers")
    public ExternalNodesLinksDTO getExternalTransfers(@RequestBody @Validated ExternalTransfersRequestDTO request) {
        return transactionService.getExternalTransfers(
                request.getCustomerIds(),
                request.getStartDate(),
                request.getEndDate(),
                request.getTopN(),
                request.getViewType()
        );

    }

}
