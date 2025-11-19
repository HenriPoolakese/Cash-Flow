import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { StateService } from '../services/state/state.service';  // Adjust this path based on your project structure

@Injectable({
  providedIn: 'root'
})
export class CompanySelectedGuard implements CanActivate {

  constructor(private stateService: StateService, private router: Router) {}

  canActivate(): boolean {
    const company = this.stateService.getCompany();

    if (company) {
      return true; // Allow navigation if a company is selected
    } else {
      // Redirect to the /select-company route if no company is selected
      this.router.navigate(['/select-company']);
      return false;
    }
  }
}
