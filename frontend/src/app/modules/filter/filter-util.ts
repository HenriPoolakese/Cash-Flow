import { FilterData } from "./filter.types";

export function getInitialFilterData(): FilterData {
  const sixMonthRange = calculateDateRange('1 Year'); // Use 1 Year as default
  return {
    ...sixMonthRange,
    selectedCompanies: [],
    selectedCountries: [],
    selectedCurrencies: [],
    selectedTimePeriod: '6 Months',
    selectedFlow: 'Incoming/Outgoing',
    selectedTopCounterparties: 10,
    selectedAmountPresentation: 'full',
    selectedViewType: 'company',
    selectedCounterparties: []
  };
}

/**
 * Calculates a date range based on the provided period string.
 */
export function calculateDateRange(period: '1 Month' | '6 Months' | '1 Year' | null): { fromDate: Date, toDate: Date } {
  const toDate = new Date();
  let fromDate = new Date();

  switch (period) {
    case '1 Month':
      fromDate.setMonth(toDate.getMonth() - 1);
      break;
    case '6 Months':
      fromDate.setMonth(toDate.getMonth() - 6);
      break;
    case '1 Year':
      fromDate.setFullYear(toDate.getFullYear() - 1);
      break;
    default:
      break;
  }

  return { fromDate, toDate };
}

/**
 * Gets properties used by transactionService from filter
 */
export function transferQueryPropsFromFilter(filterData: FilterData) {
  let { selectedTimePeriod, fromDate, toDate, selectedTopCounterparties, selectedViewType, selectedCounterparties } = filterData;
  if (selectedTimePeriod == null) {
    fromDate = fromDate ? new Date(fromDate) : null;
    toDate = toDate ? new Date(toDate) : null;
  }

  // Simplified date calculation
  if (selectedTimePeriod && (!fromDate || !toDate)) {
    const calculatedDates = calculateDateRange(selectedTimePeriod as '1 Month' | '6 Months' | '1 Year');
    fromDate = fromDate || calculatedDates.fromDate;
    toDate = toDate || calculatedDates.toDate;
  }

  const customerIds = filterData.selectedCompanies.map(c => c.customerId);
  const fromDateStr = fromDate ? fromDate.toISOString().split('T')[0] : undefined;
  const toDateStr = toDate ? toDate.toISOString().split('T')[0] : undefined;

  return { customerIds, fromDateStr, toDateStr, selectedTopCounterparties, selectedViewType, selectedCounterparties }
}
