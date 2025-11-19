import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-description',
  standalone: true,
  imports: [CommonModule],  // Add this if you need common Angular directives
  templateUrl: './app-description.component.html',
  styleUrls: ['./app-description.component.scss']
})
export class AppDescriptionComponent {

}
