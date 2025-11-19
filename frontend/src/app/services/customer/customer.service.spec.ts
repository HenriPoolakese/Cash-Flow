import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Customer, CustomerService } from './customer.service';
import { TransactionService } from "../transaction/transaction.service";
import { provideHttpClient } from "@angular/common/http";

describe('CustomerService', () => {
  let service: CustomerService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        TransactionService
      ]
    });

    service = TestBed.inject(CustomerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Ensure no outstanding requests are present after each test
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all customers', () => {
    const mockCustomers: Customer[] = [
      {
        customerId: '1',
        customerName: 'Company A',
        customerType: 'Type A',
        customerBankBicCode: 'BICCODEA'
      },
      {
        customerId: '2',
        customerName: 'Company B',
        customerType: 'Type B',
        customerBankBicCode: 'BICCODEB'
      }
    ];

    service.getAllCustomers().subscribe(customers => {
      expect(customers).toEqual(mockCustomers);
    });

    const req = httpMock.expectOne('/api/customers/companies');
    expect(req.request.method).toBe('GET');
    req.flush(mockCustomers); // Mock the response with mockCustomers
  });
});
