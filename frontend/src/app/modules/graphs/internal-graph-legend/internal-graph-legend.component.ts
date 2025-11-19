import { Component } from '@angular/core';
import { NgForOf, NgIf } from "@angular/common";

@Component({
  selector: 'app-internal-graph-legend',
  standalone: true,
  templateUrl: './internal-graph-legend.component.html',
  imports: [
    NgForOf,
    NgIf
  ],
  styleUrls: ['./internal-graph-legend.component.scss']
})
export class InternalGraphLegend {
  isOpen = false;

  nodeTypes = [
    { type: 'Account', image: 'images/wallet.png', description: 'Represents an account node. Currency.' },
    { type: 'Company', image: 'images/enterprise.png', description: 'Represents a company node having transactions with group.' },
    ];

  arrowTypes = [
    { type: 'Net Flow', color: '#1f77b4', description: 'Represents the net flow of all transactions between nodes.' },
    { type: 'Group', color: '#1e1e1e', description: 'Represents a group connection.' },
    { type: 'Account', color: '#acacac', description: 'Represents sum of transactions between internal accounts ' },
   ];

  toggleAccordion() {
    this.isOpen = !this.isOpen;
  }
}
