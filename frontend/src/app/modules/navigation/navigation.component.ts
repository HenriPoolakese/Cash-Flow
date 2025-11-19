import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from "@angular/router";
import { NgForOf } from "@angular/common";

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    NgForOf
  ],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.scss'
})
export class NavigationComponent {
  isMenuOpen = false;

  menuItems = [
    { label: 'Transaction Flows', link: '/transactionflows' },
    { label: 'Internal Flows', link: '/internalflows' },
    { label: 'Description', link: '/description' }
  ];

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }

  closeMenu(): void {
    this.isMenuOpen = false;
  }
}
