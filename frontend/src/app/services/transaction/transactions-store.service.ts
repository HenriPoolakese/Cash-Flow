// src/app/services/transactions-store.service.ts

import { Injectable } from '@angular/core';
import {BehaviorSubject, Subject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TransactionsStoreService {

  private debitLinksSubject = new BehaviorSubject<any[]>([]);
  public debitLinks$ = this.debitLinksSubject.asObservable();

  private creditLinksSubject = new BehaviorSubject<any[]>([]);
  public creditLinks$ = this.creditLinksSubject.asObservable();

  private netFlowTableSubject  = new BehaviorSubject<any[]>([]);
  public netFlowTable$ = this.netFlowTableSubject.asObservable();

  setDebitLinks(debitLinks: any[]): void {
    this.debitLinksSubject.next(debitLinks);
  }

  setCreditLinks(creditLinks: any[]): void {
    this.creditLinksSubject.next(creditLinks);
  }

  setNetFlowTable(netflowTable: any[]): void {
    this.netFlowTableSubject.next(netflowTable);
  }
}
