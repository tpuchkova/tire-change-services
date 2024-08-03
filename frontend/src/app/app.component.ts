import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {GetAvailableTimesComponent} from "./components/get-available-times/get-available-times.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, GetAvailableTimesComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'frontend';
}
