import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {AppDescriptionComponent} from "./modules/app-description/app-description.component";
import {NavigationComponent} from "./modules/navigation/navigation.component";
import {FilterComponent} from "./modules/filter/filter.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    AppDescriptionComponent,
    NavigationComponent,
    FilterComponent,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'cash-flow-visualization-frontend';
}
