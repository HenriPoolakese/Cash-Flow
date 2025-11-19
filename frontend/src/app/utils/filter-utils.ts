import { Counterparty } from '../services/filter/counterparty-store.service';
import {Customer} from "../services/customer/customer.service";

export class FilterUtils {
  static combineCounterparties(
    topDebitCounterparties: Counterparty[],
    topCreditCounterparties: Counterparty[]
  ): Counterparty[] {
    const allCounterparties = [...topDebitCounterparties, ...topCreditCounterparties];
    return Array.from(
      new Map(allCounterparties.map(counterparty => [counterparty.counterpartyKey, counterparty])).values()
    );
  }

  static filterCustomers(participants: Customer[], searchTerm: string): Customer[] {
    return participants.filter(participant =>
      participant.customerName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      participant.customerType.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }

  static filterCounterparties(counterparties: Counterparty[], searchTermCounterparty: string): Counterparty[] {
    return counterparties.filter(counterparty =>
      counterparty.counterpartyKey?.toLowerCase().includes(searchTermCounterparty.toLowerCase())
    );
  }
}
