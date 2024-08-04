import { Component } from '@angular/core';
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef,
  MatRow, MatRowDef,
  MatTable,
} from '@angular/material/table';
import {MatFormField} from "@angular/material/form-field";
import {MatDatepickerToggle, MatDateRangeInput, MatDateRangePicker} from "@angular/material/datepicker";
import {MatOption, provideNativeDateAdapter} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatFormFieldModule} from '@angular/material/form-field';
import {DateRangePickerComponent} from "./components/date-range-picker/date-range-picker.component";
import {MatSelect} from "@angular/material/select";

export interface Element {
  date: string;
  wsName: string;
  carType: string;
  address: string;
}

interface WorkshopName {
  value: string;
  viewValue: string;
}

interface CarType {
  value: string;
  viewValue: string;
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
    MatTable,
    MatHeaderCell,
    MatCell,
    MatHeaderRow,
    MatRow,
    MatColumnDef,
    MatHeaderCellDef,
    MatCellDef,
    MatHeaderRowDef,
    MatRowDef,
    MatFormField,
    MatDateRangeInput,
    MatDatepickerToggle,
    MatDateRangePicker,
    MatFormFieldModule,
    MatDatepickerModule,
    DateRangePickerComponent,
    MatSelect,
    MatOption,
  ],
  providers: [provideNativeDateAdapter()],
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  displayedColumns: string[] = ['date', 'wsName', 'carType', 'address', 'book'];
  dataSource = ELEMENT_DATA;
  workshopNames: WorkshopName[] = [
    {value: 'all', viewValue: 'All'},
    {value: 'london', viewValue: 'London'},
    {value: 'manchester', viewValue: 'Manchester'},
  ];
  carTypes: CarType[] = [
    {value: 'all', viewValue: 'All'},
    {value: 'passenger car', viewValue: 'Passenger car'},
    {value: 'truck', viewValue: 'Truck'},
  ];

  onSearch() {
    // Implement search functionality here
  }

  onBook() {
    console.log('Current time:', new Date().toLocaleTimeString());
  }
}


