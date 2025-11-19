import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { forkJoin, Observable } from 'rxjs';
import {
  CounterPartyAccountsDTO,
  ExternalTransfersDTO,
  InternalTransactionSummaryDTO,
  InternalTransfersDTO
} from './transaction-dto.model';
import { memoizeLastReactive } from "../memoize.util";

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private apiUrl = '/api/transactions';

  constructor(private http: HttpClient) {
  }

  getInternalTransfers = (
    customerIds: string[],
    startDate = '1970-01-01',
    endDate = new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString().split('T')[0]
  ): Observable<InternalTransfersDTO> => {
    const url = `${this.apiUrl}/internal-transfers`;

    return this.http.post<InternalTransfersDTO>(url, {
      customerIds,
      startDate,
      endDate
    });
  };

  getInternalTransactionSummaries(
    customerIds: string[],
    startDate = '1970-01-01',
    endDate = new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString().split('T')[0]
  ): Observable<InternalTransactionSummaryDTO> {
    const url = `${this.apiUrl}/internal-transaction-summaries`;

    return this.http.post<InternalTransactionSummaryDTO>(url, {
      customerIds,
      startDate,
      endDate
    });
  }

  getExternalTransfers = (customerIds: string[], startDate?: string, endDate?: string, topN?: number | null, viewType?: string): Observable<ExternalTransfersDTO> => {
    const url = `${this.apiUrl}/external-transfers`;
    return this.http.post<ExternalTransfersDTO>(url, {
      customerIds,
      startDate,
      endDate,
      topN,
      viewType
    });
  };


  getAllTransfers(customerIds: string[], startDate?: string, endDate?: string): Observable<[InternalTransfersDTO, ExternalTransfersDTO]> {
    return forkJoin([
      this.getInternalTransfers(customerIds, startDate, endDate),
      this.getExternalTransfers(customerIds, startDate, endDate)
    ]);
  }

  getExternalTransfersWithCounterparties(
    counterpartyKeys: string[],
    customerIds: string[],
    startDate: string | undefined,
    endDate: string | undefined
  ): Observable<CounterPartyAccountsDTO> {
    const url = `${this.apiUrl}/external-transfers-with-counterparties-accounts`;
    return this.http.post<any>(url, {
      counterpartyKeys,
      customerIds,
      startDate,
      endDate
    });
  }


  // these will only rerun when parameters actually change. only last call is memoized
  memoizedInternalTransfers = memoizeLastReactive(this.getInternalTransfers);

  memoizedExternalTransfers = memoizeLastReactive(this.getExternalTransfers);
}
