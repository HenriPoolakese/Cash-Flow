import { Injectable } from '@angular/core';
import { Customer } from '../customer/customer.service';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StateService {
  private myCompanySubject = new BehaviorSubject<Customer | null>(this.loadCompanyFromLocalStorage());
  myCompany$ = this.myCompanySubject.asObservable();

  private showSummarySubject = new BehaviorSubject<boolean>(this.loadShowSummaryFromLocalStorage());
  showSummary$ = this.showSummarySubject.asObservable();

  saveMyCompany(participant: Customer): void {
    this.myCompanySubject.next(participant);
    this.setShowSummary(true);
    this.saveToLocalStorage('myCompany', participant);
  }

  getCompany(): Customer | null {
    return this.myCompanySubject.getValue();
  }

  setShowSummary(show: boolean): void {
    this.showSummarySubject.next(show);
    this.saveToLocalStorage('showSummary', show);
  }

  getShowSummary(): boolean {
    return this.showSummarySubject.getValue();
  }

  clearCompany(): void {
    this.myCompanySubject.next(null);
    this.setShowSummary(false);
    this.removeFromLocalStorage('myCompany');
    this.removeFromLocalStorage('showSummary');
  }

  private loadCompanyFromLocalStorage(): Customer | null {
    return this.loadFromLocalStorage<Customer>('myCompany');
  }

  private loadShowSummaryFromLocalStorage(): boolean {
    return this.loadFromLocalStorage<boolean>('showSummary') ?? false; // Default to false if not set
  }

  private loadFromLocalStorage<T>(key: string): T | null {
    if (typeof localStorage !== 'undefined') {
      const item = localStorage.getItem(key);
      return item ? JSON.parse(item) : null;
    }
    return null; // Return null if localStorage is not available
  }

  private saveToLocalStorage(key: string, value: any): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(key, JSON.stringify(value));
    }
  }

  private removeFromLocalStorage(key: string): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem(key);
    }
  }

  private activeCompanies: Customer[] = [];

  saveActiveCompanies(companies: Customer[]): void {
    this.activeCompanies = companies;
    console.log("Active companies saved:", this.activeCompanies);
  }

  getActiveCompanies(): Customer[] {
    return this.activeCompanies;
  }
}
