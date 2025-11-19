import { Component } from '@angular/core';
import { NgForOf, NgIf } from "@angular/common";

@Component({
  selector: 'app-external-graph-legend',
  standalone: true,
  templateUrl: './external-graph-legend.component.html',
  imports: [
    NgForOf,
    NgIf
  ],
  styleUrls: ['./external-graph-legend.component.scss']
})
export class ExternalGraphLegendComponent {
  isOpen = false;

  nodeTypes = [
    { type: 'Account', image: 'images/wallet-35723d.png', description: 'Represents an account node.' },
    { type: 'Company', image: 'images/enterprise.png', description: 'Represents a company node having transactions with group. When grouped by country is visualized as a flag.' },
    { type: 'Private', image: 'images/spy.png', description: 'Represents private individuals having transactions with the group.' },
    { type: 'Bank', image: 'images/bank.png', description: 'Represents bank that the account belongs to' },
  ];

  arrowTypes = [
    { type: 'Credit', color: '#ae1a17', description: 'Indicates the sum of credit transactions between nodes.' },
    { type: 'Debit', color: '#1fb449', description: 'Indicates the sum of credit transactions between nodes.' },
    { type: 'Net Flow', color: '#1f77b4', description: 'Represents the net flow of all transactions between nodes.' },
    { type: 'Group', color: '#c3c3c3', description: 'Represents a group connection.' },
  ];

  toggleAccordion() {
    this.isOpen = !this.isOpen;
  }
}
