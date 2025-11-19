import { ChangeDetectorRef, Component, NO_ERRORS_SCHEMA, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from "@angular/common";
import { Customer } from "../../../services/customer/customer.service";
import { FilterComponent } from "../../filter/filter.component";
import { SigmaGraphComponent } from "../../sigma-graph/sigma-graph.component";
import { GraphData, GraphNode } from "../../sigma-graph/graph.models";
import { mapCounterpartyTransfers, mapExternalTransfers } from "../../sigma-graph/graph-mapper";
import { CounterPartyAccountsDTO, ExternalTransfersDTO } from "../../../services/transaction/transaction-dto.model";
import { FlowTableComponent } from "../../flow-table/flow-table.component";
import { FilterData } from "../../filter/filter.types";
import { ExternalGraphLegendComponent } from "../external-graph-legend/external-graph-legend.component";
import { FilterDataStoreService } from "../../../services/filter/filter-data-store.service";
import { TransactionsStoreService } from '../../../services/transaction/transactions-store.service';
import { getInitialFilterData, transferQueryPropsFromFilter } from "../../filter/filter-util";
import { DragDropModule } from '@angular/cdk/drag-drop';
import { take } from 'rxjs/operators';
import { Observable } from "rxjs";
import { ExternalTransfersService } from "./external-transfers.service";
import { formatAmount } from "../../../utils/currency.utils";
import { Counterparty, CounterpartyStoreService } from "../../../services/filter/counterparty-store.service";

interface TableModalData {
  id: number;
  flowDirection: 'from' | 'to' | 'netflow';
  data: any[];
}

@Component({
  selector: 'app-all-by-country-graph',
  standalone: true,
  imports: [CommonModule, FilterComponent, SigmaGraphComponent, FlowTableComponent, ExternalGraphLegendComponent, DragDropModule],
  schemas: [NO_ERRORS_SCHEMA],
  templateUrl: './external-transfers-graph.component.html',
  styleUrl: './external-transfers-graph.component.scss',
})
export class ExternalTransfersGraphComponent implements OnInit {
  @ViewChild(FilterComponent) filterComponent!: FilterComponent;

  customers: Customer[] = [];
  graphData?: GraphData;
  currencies: string[] = [];
  countries: string[] = [];
  openTables: TableModalData[] = [];
  private modalIdCounter = 1;
  filterData = getInitialFilterData();
  isFilterHidden = false;

  constructor(
    private cdr: ChangeDetectorRef,
    private filterDataStoreService: FilterDataStoreService,
    private transactionStoreService: TransactionsStoreService,
    private externalTransfersService: ExternalTransfersService,
    private counterpartyStoreService: CounterpartyStoreService,
  ) {
  }

  ngOnInit(): void {
    this.getCompaniesAndLoadGraphData();
    this.counterpartyStoreService.submitCounterparties$.subscribe(({ counterparties }) => {
      this.getCounterpartyAccounts(counterparties);
    });
  }

  getCompaniesAndLoadGraphData(): void {
    this.externalTransfersService.getAllCustomers().subscribe({
      next: (data) => {
        this.customers = data;
        this.filterData.selectedCompanies = data;
        this.filterData.selectedAmountPresentation = 'full';
        this.getTransactions(this.filterData);
      },
      error: (error) => console.error('Error fetching company data', error),
    });
  }

  getCounterpartyAccounts(counterparties: Counterparty[]): void {
    const counterpartyKeys = counterparties.map(counterparty => counterparty.counterpartyKey ?? 'Unknown Name');
    this.externalTransfersService.getCounterpartyAccounts(counterpartyKeys, this.filterData).subscribe({
      next: (data) => {
        const creditLinks = data.creditLinks.filter(link => {
          return !this.filterData.selectedCurrencies.length || this.filterData.selectedCurrencies.includes(link.currency);
        });
        const debitLinks = data.debitLinks.filter(link => {
          return !this.filterData.selectedCurrencies.length || this.filterData.selectedCurrencies.includes(link.currency);
        });
        const netFlowTable = data.netflowTable.filter(link => {
          return !this.filterData.selectedCurrencies.length || this.filterData.selectedCurrencies.includes(link.currency);
        });

        this.updateFilterOptionsForCounterPartyAccountsDTO(data);
        this.transactionStoreService.setCreditLinks(creditLinks);
        this.transactionStoreService.setDebitLinks(debitLinks);
        this.transactionStoreService.setNetFlowTable(netFlowTable);

        this.graphData = mapCounterpartyTransfers(data, this.filterData);
        this.cdr.detectChanges();
      },
      error: (error) => console.error('Error fetching counterparty data:', error),
    });
  }


  getTransactions(filterData: FilterData): void {
    this.externalTransfersService.getTransactions(filterData).subscribe({
      next: (data: ExternalTransfersDTO) => {
        const creditLinks = data.creditLinks.filter(link => {
          return !filterData.selectedCurrencies.length || filterData.selectedCurrencies.includes(link.currency);
        });
        const debitLinks = data.debitLinks.filter(link => {
          return !filterData.selectedCurrencies.length || filterData.selectedCurrencies.includes(link.currency);
        });
        const netFlowTable = data.netflowTable.filter(link => {
          return !filterData.selectedCurrencies.length || filterData.selectedCurrencies.includes(link.currency);
        });

        this.transactionStoreService.setDebitLinks(debitLinks);
        this.transactionStoreService.setCreditLinks(creditLinks);
        this.transactionStoreService.setNetFlowTable(netFlowTable);
        this.counterpartyStoreService.setTopCreditCounterparties(data.topCreditCounterparties);
        this.counterpartyStoreService.setTopDebitCounterparties(data.topDebitCounterparties);
        this.updateFilterOptionsForExternalTransfersDTO(data);

        this.graphData = mapExternalTransfers(data, filterData);
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error fetching graph data:', error);
      },
      complete: () => {
        console.debug('Transaction data fetch completed.');
      }
    });
  }


  private updateFilterOptionsCommon(nodes: any[]): void {
    this.currencies = Array.from(
      new Set(nodes.map(node => node.currency).filter((currency): currency is string => Boolean(currency)))
    );
    this.filterDataStoreService.setCurrencies(this.currencies);

    // if nodes currecies is less than selected currencies, then set selected currencies to nodes currencies
    if (this.currencies.length < this.filterData.selectedCurrencies.length) {
      this.filterData.selectedCurrencies = [...this.currencies];
    }

    if (!this.filterData.selectedCurrencies.length && this.currencies.length) {
      this.filterData.selectedCurrencies = [...this.currencies];
    }

    if (!this.filterData.selectedCurrencies.every(currency => this.currencies.includes(currency))) {
      this.filterData.selectedCurrencies = [...this.currencies];
    }

    if (this.filterData.selectedViewType === 'country') {
      this.countries = Array.from(
        new Set(
          nodes
            .filter(node => node.type === "company")
            .map(node => node.country)
            .filter((country): country is string => Boolean(country))
        )
      );

      this.filterDataStoreService.setCountries(this.countries);

      if (!this.filterData.selectedCountries.length) {
        this.filterData.selectedCountries = [...this.countries];
      }

      if (!this.filterData.selectedCountries.every(country => this.countries.includes(country))) {
        this.filterData.selectedCountries = [...this.countries];
      }
    }
  }

  private updateFilterOptionsForExternalTransfersDTO(data: ExternalTransfersDTO): void {
    this.updateFilterOptionsCommon(data.nodes);
  }

  private updateFilterOptionsForCounterPartyAccountsDTO(data: CounterPartyAccountsDTO): void {
    this.updateFilterOptionsCommon(data.nodes);
  }

  AllByCountryOnVisualize(filterData: FilterData): void {
    this.filterData = filterData;
    const { selectedCounterparties } = transferQueryPropsFromFilter(filterData);
    this.updateOpenTablesContents();
    if (selectedCounterparties.length > 0) {
      this.getCounterpartyAccounts(selectedCounterparties);
      return;
    } else {
      this.getTransactions(this.filterData);
    }
  }

  private updateOpenTablesContents(): void {
    this.openTables.forEach(table => {
      let source$: Observable<any[]>;
      if (table.flowDirection === 'from') {
        source$ = this.transactionStoreService.creditLinks$;
      } else if (table.flowDirection === 'to') {
        source$ = this.transactionStoreService.debitLinks$;
      } else {
        source$ = this.transactionStoreService.netFlowTable$;
      }

      source$.pipe(take(1)).subscribe(filteredLinks => {
        table.data = filteredLinks.map(link => ({
          participantName: table.flowDirection === 'from' ? link.targetName : link.sourceName,
          account: link.sebCustomerAccount,
          amount: formatAmount(link.amounts, this.filterData.selectedAmountPresentation),
          currency: link.currency,
          frequency: link.count,
          averageAmount: formatAmount(link.avgAmounts, this.filterData.selectedAmountPresentation),
          earliestDate: link.earliestDate,
          latestDate: link.latestDate,
          counterparty: table.flowDirection === 'from' ? link.sourceName : link.targetName,
          counterpartyAccount: link.counterpartyAccount,
        }));
      });
    });
  }


  onNodeClick(node: GraphNode): void {
    console.log('Node clicked:', node);
  }


  AllByCountryAddDataToTableModal(flowDirection: 'to' | 'from' | 'netflow'): void {
    const existingTable = this.openTables.find(table => table.flowDirection === flowDirection);
    if (existingTable) {
      console.log(`Table for flowDirection '${flowDirection}' is already open.`);
      return;
    }

    let source$: Observable<any[]>;
    if (flowDirection === 'from') {
      source$ = this.transactionStoreService.creditLinks$;
    } else if (flowDirection === 'to') {
      source$ = this.transactionStoreService.debitLinks$;
    } else {
      source$ = this.transactionStoreService.netFlowTable$;
    }

    source$.pipe(take(1)).subscribe(filteredLinks => {
      const formattedData = filteredLinks.map(link => ({
        participantName: flowDirection === 'from' ? link.targetName : link.sourceName,
        account: link.sebCustomerAccount,
        amount: formatAmount(link.amounts.original, this.filterData.selectedAmountPresentation),
        currency: link.currency,
        frequency: link.count,
        averageAmount: formatAmount(link.avgAmounts.original, this.filterData.selectedAmountPresentation),
        earliestDate: link.earliestDate,
        latestDate: link.latestDate,
        counterparty: flowDirection === 'from' ? link.sourceName : link.targetName,
        counterpartyAccount: link.counterpartyAccount,
      }));

      this.openTables.push({
        id: this.modalIdCounter++,
        flowDirection,
        data: formattedData
      });
    });
  }

  AllByCountryHandleCloseTableModal(id: number): void {
    this.openTables = this.openTables.filter(modal => modal.id !== id);
    if (this.filterComponent) {
      this.filterComponent.handleClosedTable();
    }
  }

  toggleFilter() {
    this.isFilterHidden = !this.isFilterHidden;
  }

  protected readonly close = close;
}
