import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TransactionService } from './transaction.service';
import { provideHttpClient } from "@angular/common/http";
import { mockExternalTransactions, mockInternalTransactions } from "./transaction.mocks";

describe('TransactionService', () => {
  let service: TransactionService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        TransactionService
      ]
    });

    service = TestBed.inject(TransactionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch internal transfers', () => {
    service.getInternalTransfers(['1', '2'], '2023-01-01', '2023-12-31').subscribe((data) => {
      expect(data).toEqual(mockInternalTransactions);
    });

    const req = httpMock.expectOne(`/api/transactions/internal-transfers?customerIds=1,2&startDate=2023-01-01&endDate=2023-12-31`);
    expect(req.request.method).toBe('GET');
    req.flush(mockInternalTransactions);
  });

  it('should fetch external transfers', () => {
    service.getExternalTransfers(['1', '2'], '2023-01-01', '2023-12-31').subscribe((data) => {
      expect(data).toEqual(mockExternalTransactions);
    });

    const req = httpMock.expectOne(`/api/transactions/external-transfers?customerIds=1,2&startDate=2023-01-01&endDate=2023-12-31`);
    expect(req.request.method).toBe('GET');
    req.flush(mockExternalTransactions);
  });

  it('should fetch both internal and external transfers in parallel', () => {
    service.getAllTransfers(['1', '2'], '2023-01-01', '2023-12-31').subscribe(([internal, external]) => {
      expect(internal).toEqual(mockInternalTransactions);
      expect(external).toEqual(mockExternalTransactions);
    });

    const internalReq = httpMock.expectOne(`/api/transactions/internal-transfers?customerIds=1,2&startDate=2023-01-01&endDate=2023-12-31`);
    const externalReq = httpMock.expectOne(`/api/transactions/external-transfers?customerIds=1,2&startDate=2023-01-01&endDate=2023-12-31`);

    expect(internalReq.request.method).toBe('GET');
    expect(externalReq.request.method).toBe('GET');

    internalReq.flush(mockInternalTransactions);
    externalReq.flush(mockExternalTransactions);
  });
});
