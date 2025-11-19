import {BehaviorSubject, Subject} from "rxjs";
import {Injectable} from "@angular/core";

export interface Counterparty {
  counterpartyName: string;
  counterpartyBankCountry: string | null;
  transactionType: string;
  counterpartyKey: string | null;
}

@Injectable({
  providedIn: 'root',
})

export class CounterpartyStoreService {
  private topDebitCounterpartiesSubject = new BehaviorSubject<Counterparty[]>([]);
  public topDebitCounterparties$ = this.topDebitCounterpartiesSubject.asObservable();

  private topCreditCounterpartiesSubject = new BehaviorSubject<Counterparty[]>([]);
  public topCreditCounterparties$ = this.topCreditCounterpartiesSubject.asObservable();

  private submitCounterpartiesSubject = new Subject<{ counterparties: Counterparty[] }>();
  submitCounterparties$ = this.submitCounterpartiesSubject.asObservable();

  setTopDebitCounterparties(topDebitCounterparties: Counterparty[]): void {
    this.topDebitCounterpartiesSubject.next(topDebitCounterparties);
  }

  setTopCreditCounterparties(topCreditCounterparties: Counterparty[]): void {
    this.topCreditCounterpartiesSubject.next(topCreditCounterparties);
  }

  triggerSubmitCounterparties(payload: { counterparties: Counterparty[] }): void {
    this.submitCounterpartiesSubject.next(payload);
  }
}
