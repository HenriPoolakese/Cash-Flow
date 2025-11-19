import {Customer} from "../../services/customer/customer.service";
import {Counterparty} from "../../services/filter/counterparty-store.service";

export interface FilterData {
  fromDate: Date | null;
  toDate: Date | null;
  selectedTimePeriod: string | null;
  selectedFlow: string;
  selectedCompanies: Customer[];
  selectedCurrencies: string[];
  selectedCountries: string[];
  selectedTopCounterparties: number | null;
  selectedAmountPresentation: string;
  selectedViewType: string;
  selectedCounterparties: Counterparty[];
}
