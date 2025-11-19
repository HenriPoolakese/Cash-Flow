import {Injectable} from "@angular/core";
import {TransactionService} from "../../../services/transaction/transaction.service";
import {Customer, CustomerService} from "../../../services/customer/customer.service";
import {Observable} from "rxjs";
import {FilterData} from "../../filter/filter.types";
import {CounterPartyAccountsDTO, ExternalTransfersDTO} from "../../../services/transaction/transaction-dto.model";
import {transferQueryPropsFromFilter} from "../../filter/filter-util";

@Injectable({
  providedIn: 'root',
})
export class ExternalTransfersService {
  constructor(
    private transactionService: TransactionService,
    private customerService: CustomerService
  ) {}

  getAllCustomers(): Observable<Customer[]> {
    return this.customerService.memoizedAllCustomers();
  }

  getTransactions(filterData: FilterData): Observable<ExternalTransfersDTO> {
    const { customerIds, fromDateStr, toDateStr, selectedTopCounterparties, selectedViewType } = transferQueryPropsFromFilter(filterData);
    return this.transactionService.memoizedExternalTransfers(customerIds, fromDateStr, toDateStr, selectedTopCounterparties, selectedViewType);
  }

  getCounterpartyAccounts(counterpartyKeys: string[], filterData: FilterData): Observable<CounterPartyAccountsDTO> {
    const { customerIds, fromDateStr, toDateStr } = transferQueryPropsFromFilter(filterData);
    if (!counterpartyKeys || !customerIds || !fromDateStr || !toDateStr) {
      console.error('Missing required parameters for fetching counterparty accounts.');
    }
    return this.transactionService.getExternalTransfersWithCounterparties(counterpartyKeys, customerIds, fromDateStr, toDateStr)
  }
}
