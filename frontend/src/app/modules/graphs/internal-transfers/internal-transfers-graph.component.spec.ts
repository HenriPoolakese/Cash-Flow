import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { InternalTransfersGraphComponent } from './internal-transfers-graph.component';
import { TransactionService } from '../../../services/transaction/transaction.service';
import { StateService } from '../../../services/state/state.service';
import { Customer } from "../../../services/customer/customer.service";
import {
  ExternalNode,
  ExternalTransfersDTO,
  InternalTransfersDTO, OwnerNode
} from "../../../services/transaction/transaction-dto.model";

describe('CompanyAccountsGraphComponent', () => {
  let component: InternalTransfersGraphComponent;
  let fixture: ComponentFixture<InternalTransfersGraphComponent>;
  let transactionService: jasmine.SpyObj<TransactionService>;
  let stateService: jasmine.SpyObj<StateService>;
  let router: jasmine.SpyObj<Router>;
  let route: ActivatedRoute;

  beforeEach(async () => {
    const transactionServiceSpy = jasmine.createSpyObj('TransactionService', ['getInternalTransfers']);
    const stateServiceSpy = jasmine.createSpyObj('StateService', ['getCustomer', 'clearCompany']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [InternalTransfersGraphComponent],
      providers: [
        { provide: TransactionService, useValue: transactionServiceSpy },
        { provide: StateService, useValue: stateServiceSpy },
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: { get: () => 'mockCustomerId' } }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(InternalTransfersGraphComponent);
    component = fixture.componentInstance;
    transactionService = TestBed.inject(TransactionService) as jasmine.SpyObj<TransactionService>;
    stateService = TestBed.inject(StateService) as jasmine.SpyObj<StateService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    route = TestBed.inject(ActivatedRoute);

    fixture.detectChanges();
  });

  beforeEach(async () => {
    const mockTransactions: InternalTransfersDTO = {
      ownerNodes: [
        { customer_id: '1', customer_name: 'Company A', country: 'EST' } as OwnerNode,
        { customer_id: '2', customer_name: 'Company B', country: 'EST' } as OwnerNode
      ],
      ownerAndAccountLink: [
        { source: '1', target: '2', label: 'Internal Transfer', amount: 1000, currency: 'EUR' }
      ],
      accountNodes: [],
      accountLinks: [],
    };


    transactionService.getInternalTransfers.and.returnValue(of(mockTransactions));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});
