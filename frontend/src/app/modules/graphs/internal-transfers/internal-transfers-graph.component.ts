import { ChangeDetectorRef, Component, NO_ERRORS_SCHEMA, OnInit } from '@angular/core';
import { CommonModule } from "@angular/common";
import { TransactionService } from "../../../services/transaction/transaction.service";
import { SigmaGraphComponent } from "../../sigma-graph/sigma-graph.component";
import { FilterComponent } from "../../filter/filter.component";
import { GraphData, GraphLink, GraphNode } from "../../sigma-graph/graph.models";
import { InternalTransfersDTO } from "../../../services/transaction/transaction-dto.model";
import { mapInternalTransfers } from "../../sigma-graph/graph-mapper";
import { Customer, CustomerService } from "../../../services/customer/customer.service";
import { FilterData } from "../../filter/filter.types";
import { getInitialFilterData, transferQueryPropsFromFilter } from "../../filter/filter-util";
import { FilterDataStoreService } from "../../../services/filter/filter-data-store.service";
import { InternalGraphLegend } from "../internal-graph-legend/internal-graph-legend.component";

@Component({
  selector: 'app-internal-transfers-graph',
  standalone: true,
  imports: [CommonModule, SigmaGraphComponent, FilterComponent, InternalGraphLegend],
  schemas: [NO_ERRORS_SCHEMA],
  templateUrl: './internal-transfers-graph.component.html',
  styleUrl: './internal-transfers-graph.component.scss',

})
export class InternalTransfersGraphComponent implements OnInit {

  customers: Customer[] = [];
  graphData?: GraphData;
  currencies: string[] = [];
  filterData = getInitialFilterData();

  isFilterHidden = false;


  constructor(
    private transactionService: TransactionService,
    private customerService: CustomerService,
    private cdr: ChangeDetectorRef,
    private filterDataStoreService: FilterDataStoreService
  ) {
  }

  ngOnInit(): void {
    this.getCompaniesAndLoadGraphData();
  }

  /**
   * Fetches all customers and initializes the graph data with a default 1-month period.
   */
  getCompaniesAndLoadGraphData(): void {
    this.customerService.memoizedAllCustomers()?.subscribe({
      next: (data) => {
        this.customers = data;
        this.filterData.selectedCompanies = data;
        this.getTransactions(this.filterData);
      },
      error: (error) => console.error('Error fetching company data', error),
    });
  }

  getTransactions(filterData: FilterData): void {
    const { customerIds, fromDateStr, toDateStr } = transferQueryPropsFromFilter(filterData);
    const requestStartTime = Date.now();
    this.transactionService.memoizedInternalTransfers(customerIds, fromDateStr, toDateStr)
      .subscribe({
        next: (data: InternalTransfersDTO) => {
          const requestEndTime = Date.now();

          this.updateFilterOptions(data);

          this.graphData = mapInternalTransfers(data, this.filterData);
          this.cdr.detectChanges();

          const renderEndTime = Date.now();
          const requestDuration = requestEndTime - requestStartTime;
          const visualizationDuration = renderEndTime - requestEndTime;
          const totalDuration = renderEndTime - requestStartTime;
          console.log(`Request duration: ${requestDuration} ms`);
          console.log(`Visualization duration: ${visualizationDuration} ms`);
          console.log(`Total duration: ${totalDuration} ms`);

        },
        error: (error) => {
          console.error('Error fetching graph data', error);
        }
      });
  }

  // Helper method to update currencies and countries
  private updateFilterOptions(data: InternalTransfersDTO): void {
    // Extract unique currencies
    this.currencies = Array.from(
      new Set(data.accountNodes.map(node => node.currency)
        .filter((currency): currency is string => Boolean(currency)))
    );

    this.filterDataStoreService.setCurrencies(this.currencies);

    if (this.filterData.selectedCurrencies.length === 0 && this.currencies.length > 0) {
      this.filterData.selectedCurrencies = [...this.currencies];
    }
  }


  // Event Handlers

  /**
   * Handles the inFilterVisualize event from the FilterComponent.
   */
  onVisualize(filterData: FilterData): void {
    this.filterData = filterData;
    this.getTransactions(filterData);
  }

  onNodeClick(node: GraphNode): void {
    console.log('Node clicked:', node);
    // Add logic on what happens
  }

  onEdgeClick(node: GraphLink): void {
    console.log('Edge clicked:', node);
    // Add logic on what happens
  }


  // UI State Management

  /**
   * Toggles the visibility of the filter component.
   */
  toggleFilter() {
    this.isFilterHidden = !this.isFilterHidden;
  }

}
