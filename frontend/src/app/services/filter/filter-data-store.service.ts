// src/app/services/filter-data-store.service.ts

import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import {getInitialFilterData} from "../../modules/filter/filter-util";
import {FilterData} from "../../modules/filter/filter.types";

@Injectable({
  providedIn: 'root'
})
export class FilterDataStoreService {
  private currenciesSubject = new BehaviorSubject<string[]>([]);
  currencies$ = this.currenciesSubject.asObservable();

  private countriesSubject = new BehaviorSubject<string[]>([]);
  countries$ = this.countriesSubject.asObservable();

  private filterDataSubject = new BehaviorSubject<FilterData>(getInitialFilterData());
  filterData$ = this.filterDataSubject.asObservable();

  // Method to update currencies
  setCurrencies(currencies: string[]): void {
    this.currenciesSubject.next(currencies);
  }

  setCountries(countries: string[]): void {
    this.countriesSubject.next(countries);
  }

  // Method to update filter data
  setFilterData(filterData: FilterData): void {
    this.filterDataSubject.next(filterData);
  }
}
