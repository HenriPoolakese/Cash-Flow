import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { memoizeLastReactive } from "../memoize.util";

// Define the Company interface
export interface Customer {
  customerId: string;
  customerName: string;
  customerType: string;
  customerBankBicCode: string;
}

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private apiUrl = '/api/customers/companies';

  constructor(private http: HttpClient) {}

  // Method to fetch companies from the API
  getAllCustomers = (): Observable<Customer[]> => this.http.get<Customer[]>(this.apiUrl);

  memoizedAllCustomers  = memoizeLastReactive(this.getAllCustomers);
}
