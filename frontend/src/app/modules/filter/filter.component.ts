import {
  ChangeDetectorRef,
  Component,
  CUSTOM_ELEMENTS_SCHEMA,
  EventEmitter,
  Input,
  NO_ERRORS_SCHEMA,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgClass, NgForOf, NgIf } from '@angular/common';
import { Customer, CustomerService } from '../../services/customer/customer.service';
import { FilterData } from './filter.types';
import { banks, collapsedSections, timePeriods } from './filter-options';
import { FilterDataStoreService } from '../../services/filter/filter-data-store.service';
import { CounterpartyStoreService, Counterparty } from '../../services/filter/counterparty-store.service';
import { getNextPage, getPreviousPage } from '../../utils/pagination-utils';
import {ValidationUtils} from '../../utils/validation-utils';
import {FilterUtils} from '../../utils/filter-utils';
import {Observable} from "rxjs";

@Component({
  selector: 'app-filter',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    NgForOf,
    NgClass,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
  templateUrl: './filter.component.html',
  styleUrls: ['./filter.component.scss'],
})
export class FilterComponent implements OnInit {
  // Section: Inputs and Outputs
  @Input() filter: string[] = [];
  @Output() inFilterDisplayTableEvent = new EventEmitter<{ flowDirection: 'from' | 'to' | 'netflow' }>();
  @Output() inFilterVisualize = new EventEmitter<FilterData>();

  // Section: General Properties
  participants: Customer[] = [];
  filteredParticipants: Customer[] = [];
  errorMessage: string = '';
  searchTerm: string = '';

  // Section: Filter Options
  banks = banks;
  selectedBanks: string[] = [...banks];
  countries: string[] = [];
  selectedCountries: string[] = [];
  currencies: string[] = [];
  selectedCurrencies: string[] = [];

  // Section: Counterparties
  topDebitCounterparties: Counterparty[] = [];
  topCreditCounterparties: Counterparty[] = [];
  topCounterpartiesCount: number = 10;
  counterparties: Counterparty[] = [];
  filteredCounterparties: Counterparty[] = [];
  selectedCounterparties: Counterparty[] = [];
  searchTermCounterparty: string = '';

  // Section: UI States and Settings
  collapsedSections = collapsedSections;
  itemsPerPage: number = 10;
  currentPage: number = 1;
  showWarning: boolean = false;
  showTopCounterparties: boolean = true;
  showCounterpartiesBox: boolean = false;
  current: number = 1;

  // Section: Selected Filter Criteria
  selectedFlow: string = 'Incoming/Outgoing';
  selectedCompanies: Customer[] = [];
  selectedTimePeriod: string | null = '1 Year';
  timePeriods = timePeriods;
  fromDate: Date | null = null;
  toDate: Date | null = null;
  flowDirection: 'from' | 'to' | 'netflow' | null = null;
  groupingOption: 'country' | 'company' = 'company';
  amountRepresentation: string = 'full';
  showCountries : boolean = false;

  constructor(
    private customerService: CustomerService,
    private filterDataStoreService: FilterDataStoreService,
    private counterpartyStoreService: CounterpartyStoreService,
    private cdr: ChangeDetectorRef
  ) {}

  // Section: Lifecycle Methods
  ngOnInit(): void {
    this.loadCurrencies();
    this.loadCountries();
    this.loadCounterparties();
    this.getParticipants();
  }

  // Section: Data Loading
  private loadCurrencies(): void {
    this.filterDataStoreService.currencies$.subscribe(currencies => {
      this.currencies = currencies;

      if (!this.selectedCurrencies.every(currency => currencies.includes(currency))) {
        this.selectedCurrencies = this.currencies;
      }

      if (!this.selectedCurrencies.length) {
        this.selectedCurrencies = [...currencies];
      } else {
        this.selectedCurrencies = this.selectedCurrencies.filter(currency => currencies.includes(currency));
      }
    });
  }

  private loadCountries(): void {
    this.filterDataStoreService.countries$.subscribe(countries => {
      this.countries = countries;
      if (!this.selectedCountries.length && countries.length) {
        this.selectedCountries = [...countries];
      }

      if (!this.selectedCountries.every(country => countries.includes(country))) {
        this.selectedCountries = this.countries;
      }
    });
  }

  private loadCounterparties(): void {
    this.counterpartyStoreService.topDebitCounterparties$.subscribe(debitData => {
      this.topDebitCounterparties = debitData;
      this.combineCounterparties();
    });

    this.counterpartyStoreService.topCreditCounterparties$.subscribe(creditData => {
      this.topCreditCounterparties = creditData;
      this.combineCounterparties();
    });
  }

  private combineCounterparties(): void {
    this.counterparties = FilterUtils.combineCounterparties(
      this.topDebitCounterparties,
      this.topCreditCounterparties
    );
    this.filteredCounterparties = [...this.counterparties];
  }

  selectTimePeriod(period: string): void {
    this.selectedTimePeriod = period;
    this.fromDate = null;
    this.toDate = null;
    this.showWarning = false;
  }

  onDateChange(): void {
    this.selectedTimePeriod = null;
    this.checkValidity();
  }

  getParticipants(): void {
    this.customerService.memoizedAllCustomers().subscribe({
      next: (data: Customer[]) => {
        this.participants = data;
        this.selectedCompanies = [...data];
        this.filteredParticipants = [...data];
      },
      error: err => (this.errorMessage = `Error: ${err.message}`),
    });
  }

  // Section: Filtering
  filterCompanies(): void {
    this.filteredParticipants = FilterUtils.filterCustomers(this.participants, this.searchTerm);
    this.currentPage = 1;
  }

  filterCounterparties(): void {
    this.filteredCounterparties = FilterUtils.filterCounterparties(this.counterparties, this.searchTermCounterparty);
    this.current = 1;
  }

  // Section: Selection Handling
  toggleSelection(list: string[], item: string): void {
    const index = list.indexOf(item);
    index > -1 ? list.splice(index, 1) : list.push(item);
  }

  toggleBank(bank: string): void {
    this.toggleSelection(this.selectedBanks, bank);
  }

  toggleCountry(country: string): void {
    this.selectedCountries = this.selectedCountries.includes(country)
      ? this.selectedCountries.filter(c => c !== country)
      : [...this.selectedCountries, country];
  }

  toggleCurrency(currency: string): void {
    this.selectedCurrencies = this.selectedCurrencies.includes(currency)
      ? this.selectedCurrencies.filter(c => c !== currency)
      : [...this.selectedCurrencies, currency];
  }

  toggleCustomer(customer: Customer): void {
    const index = this.selectedCompanies.indexOf(customer);
    index > -1 ? this.selectedCompanies.splice(index, 1) : this.selectedCompanies.push(customer);
  }

  toggleCounterparty(counterparty: Counterparty): void {
    const index = this.selectedCounterparties.findIndex(
      c => c.counterpartyKey === counterparty.counterpartyKey
    );
    if (index > -1) {
      this.selectedCounterparties = [
        ...this.selectedCounterparties.slice(0, index),
        ...this.selectedCounterparties.slice(index + 1),
      ];
    } else {
      this.selectedCounterparties = [...this.selectedCounterparties, counterparty];
    }
  }

  toggleSection(section: string): void {
    this.collapsedSections[section] = !this.collapsedSections[section];
  }

  toggleSelectAllCompanies(): void {
    if (this.areAllCompaniesSelected()) {
      this.selectedCompanies = [];
    } else {
      this.selectedCompanies = [...this.participants];
    }
  }

  toggleSelectAllCurrencies(): void {
    if (this.areAllCurrenciesSelected()) {
      this.selectedCurrencies = [];
    } else {
      this.selectedCurrencies = [...this.currencies];
    }
  }

  toggleSelectAllCountries(): void {
    if (this.areAllCountriesSelected()) {
      this.selectedCountries = [];
    } else {
      this.selectedCountries = [...this.countries];
    }
  }

  toggleSelectAllCounterparties(): void {
    if (this.isAllCounterpartiesSelected()) {
      this.selectedCounterparties = [];
    } else {
      this.selectedCounterparties = [...this.filteredCounterparties];
    }
  }


  // Section: Selection Checks
  isCounterpartySelected(counterparty: Counterparty): boolean {
    return this.selectedCounterparties.some(
      selected => selected.counterpartyKey === counterparty.counterpartyKey
    );
  }

  isCompanySelected(company: any): boolean {
    return this.selectedCompanies.some(selected => selected.customerName === company.customerName);
  }

  isAllCounterpartiesSelected(): boolean {
    return this.filteredCounterparties.every(counterparty =>
      this.selectedCounterparties.some(selected => selected.counterpartyKey === counterparty.counterpartyKey)
    );
  }

  areAllCurrenciesSelected(): boolean {
    return this.currencies.length > 0 && this.selectedCurrencies.length === this.currencies.length;
  }

  areAllCountriesSelected(): boolean {
    return this.countries.length > 0 && this.selectedCountries.length === this.countries.length;
  }

  areAllCompaniesSelected(): boolean {
    return this.participants.length > 0 && this.selectedCompanies.length === this.participants.length;
  }

  // Section: Pagination
  nextCounterpartiesPage(): void {
    this.currentPage = getNextPage(this.currentPage, this.itemsPerPage, this.filteredCounterparties.length);
  }

  prevCounterpartiesPage(): void {
    this.currentPage = getPreviousPage(this.currentPage);
  }

  nextParticipantsPage(): void {
    this.currentPage = getNextPage(this.currentPage, this.itemsPerPage, this.filteredParticipants.length);
  }

  prevParticipantsPage(): void {
    this.currentPage = getPreviousPage(this.currentPage);
  }

  // Section: Event Emitters
  emitFilterDisplayTable(): void {
    if (this.flowDirection) {
      this.inFilterDisplayTableEvent.emit({ flowDirection: this.flowDirection });
    }
  }

  emitFilterVisualize(): void {
    this.checkValidity();

    if (!this.showWarning) {
      if (this.groupingOption === 'country') {
        this.showCounterpartiesBox = true;
        this.showCountries = true;
      } else {
        this.showCounterpartiesBox = false;
        this.showCountries = false;
        this.selectedCounterparties = [];
      }
      this.inFilterVisualize.emit({
        fromDate: this.fromDate,
        toDate: this.toDate,
        selectedTimePeriod: this.selectedTimePeriod,
        selectedFlow: this.selectedFlow,
        selectedCountries: this.selectedCountries,
        selectedCurrencies: this.selectedCurrencies,
        selectedCompanies: this.selectedCompanies,
        selectedTopCounterparties: this.showTopCounterparties ? this.topCounterpartiesCount : null,
        selectedAmountPresentation: this.amountRepresentation,
        selectedViewType: this.groupingOption,
        selectedCounterparties: this.selectedCounterparties,
      });
    }
  }

  handleClosedTable(): void {
    this.flowDirection = null;
  }

  // Section: Validations
  checkValidity(): void {
    this.showWarning = ValidationUtils.checkValidity(this.selectedTimePeriod, this.fromDate, this.toDate);
  }

  // Section: Getters
  get paginatedCompanies(): Customer[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredParticipants.slice(start, end);
  }

  get paginatedCounterparties(): Counterparty[] {
    const sortedCounterparties = [...this.filteredCounterparties].sort((a, b) => {
      const valA = a.counterpartyKey;
      const valB = b.counterpartyKey;
      if (valA === null || valB === null) {
        return 0;
      }
      return valA < valB ? -1 : valA > valB ? 1 : 0;
    });
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return sortedCounterparties.slice(start, end);
  }

  protected readonly Math = Math;
}
