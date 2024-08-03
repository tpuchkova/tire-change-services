import { Component } from '@angular/core';
import {MatTable, MatTableModule} from '@angular/material/table';

export interface Element {
  date: string;
  wsName: string;
  carType: string;
  address: string;
}

const ELEMENT_DATA: Element[] = [
  { date: '2023-01-01', wsName: 'WS1', carType: 'Sedan', address: '123 Main St' },
  { date: '2023-01-02', wsName: 'WS2', carType: 'SUV', address: '456 Oak St' },
  // Add more data as needed
];

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: true,
  imports: [
    MatTable
  ],
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  displayedColumns: string[] = ['date', 'wsName', 'carType', 'address', 'book'];
  dataSource = ELEMENT_DATA;

  onSearch() {
    // Implement search functionality here
  }

  onBook() {
    console.log('Current time:', new Date().toLocaleTimeString());
  }
}

