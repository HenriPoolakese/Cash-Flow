import { Routes } from '@angular/router';
import { AppDescriptionComponent } from "../modules/app-description/app-description.component";
import {
  ExternalTransfersGraphComponent
} from "../modules/graphs/external-transfers/external-transfers-graph.component";
import {
  InternalTransfersGraphComponent
} from "../modules/graphs/internal-transfers/internal-transfers-graph.component";
import { CompanySelectedGuard } from "../guards/company-selected.guard";

export const routes: Routes = [
  { path: 'transactionflows', component: ExternalTransfersGraphComponent },
  { path: 'internalflows', component: InternalTransfersGraphComponent },
  { path: 'description', component: AppDescriptionComponent },
  { path: '**', redirectTo: 'transactionflows', pathMatch: 'full' }
];
