import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ExternalTransfersGraphComponent } from './external-transfers-graph.component';
import { TransactionService } from '../../../services/transaction/transaction.service';
import { StateService } from '../../../services/state/state.service';
import { CustomerService, Customer } from '../../../services/customer/customer.service';
import {
  ExternalNode,
  ExternalTransfersDTO,
  InternalTransfersDTO,
  OwnerNode
} from "../../../services/transaction/transaction-dto.model";

describe('AllByCountryGraphComponent', () => {
  let component: ExternalTransfersGraphComponent;
  let fixture: ComponentFixture<ExternalTransfersGraphComponent>;
  let transactionService: jasmine.SpyObj<TransactionService>;
  let customerService: jasmine.SpyObj<CustomerService>;
  let stateService: jasmine.SpyObj<StateService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const transactionServiceSpy = jasmine.createSpyObj('TransactionService', ['getExternalTransfers']);
    const customerServiceSpy = jasmine.createSpyObj('CustomerService', ['getAllCustomers']);
    const stateServiceSpy = jasmine.createSpyObj('StateService', ['saveMyCompany']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ExternalTransfersGraphComponent],
      providers: [
        { provide: TransactionService, useValue: transactionServiceSpy },
        { provide: CustomerService, useValue: customerServiceSpy },
        { provide: StateService, useValue: stateServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ExternalTransfersGraphComponent);
    component = fixture.componentInstance;
    transactionService = TestBed.inject(TransactionService) as jasmine.SpyObj<TransactionService>;
    customerService = TestBed.inject(CustomerService) as jasmine.SpyObj<CustomerService>;
    stateService = TestBed.inject(StateService) as jasmine.SpyObj<StateService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;


    fixture.detectChanges();
  });

  beforeEach(async () => {
    const mockCustomers: Customer[] = [
      { customerId: '1', customerName: 'Company A', customerType: 'Type A', customerBankBicCode: 'BIC1' },
      { customerId: '2', customerName: 'Company B', customerType: 'Type B', customerBankBicCode: 'BIC2' }
    ];
    const mockTransactions: ExternalTransfersDTO = {
      nodes: [
        { id: 1, name: 'Company A', type: 'company', country: 'Estonia', currency: 'EUR', iban:'EE'} as ExternalNode,
        { id: 2, name: 'Company B', type: 'company', country: 'Estonia', currency: 'EUR', iban:'EE'} as ExternalNode,
      ],
      groupLinks: [
        { source: '1', target: '2', label: 'External Transfer', currency: 'EUR' }
      ],
      debitLinks: [],
      creditLinks: [],
      netflowLinks: [],
      netflowTable: [],
      topDebitCounterparties: [],
      topCreditCounterparties: [],
    };

    customerService.getAllCustomers.and.returnValue(of(mockCustomers));
    transactionService.getExternalTransfers.and.returnValue(of(mockTransactions));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch companies and load graph data on init', () => {

    component.ngOnInit();

    expect(customerService.getAllCustomers).toHaveBeenCalled();
    expect(transactionService.getExternalTransfers).toHaveBeenCalledWith(['1', '2']);
  });

});
