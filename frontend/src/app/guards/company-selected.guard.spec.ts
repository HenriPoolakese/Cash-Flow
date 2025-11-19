import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { CompanySelectedGuard } from './company-selected.guard';
import { StateService } from '../services/state/state.service';  // Adjust path if necessary
import { of } from 'rxjs';
import { Customer } from "../services/customer/customer.service";

describe('CompanySelectedGuard', () => {
  let guard: CompanySelectedGuard;
  let stateService: jasmine.SpyObj<StateService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const stateServiceSpy = jasmine.createSpyObj('StateService', ['getCustomer']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        CompanySelectedGuard,
        { provide: StateService, useValue: stateServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    guard = TestBed.inject(CompanySelectedGuard);
    stateService = TestBed.inject(StateService) as jasmine.SpyObj<StateService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should allow activation if a company is selected', () => {
    stateService.getCompany.and.returnValue({ customerId: '123', customerName: 'Test Company' } as Customer);

    expect(guard.canActivate()).toBeTrue();
  });

  it('should redirect to /select-company if no company is selected', () => {
    stateService.getCompany.and.returnValue(null);

    expect(guard.canActivate()).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/select-company']);
  });
});
