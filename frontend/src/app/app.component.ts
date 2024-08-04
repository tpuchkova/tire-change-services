import {Component, Input, Output} from '@angular/core';
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
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

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
  //data: AvailableTime[] = [];
  data: Element[] = ELEMENT_DATA;

  @Input()
  selectedWorkshop: String = "all";

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

export interface AvailableTime {
  time: string;
  workshopName: string;
  address: string;
  carType: string;
}

export interface AvailableTimes {
  items: AvailableTime[];
}

// export class ExampleHttpDatabase {
//   constructor(private _httpClient: HttpClient) {}
//
//   getRepoIssues(): Observable<AvailableTimes> {
//     const href = 'http://localhost:9090/getAvailableTimes';
//     const requestUrl = `${href}?q=repo:angular/components&sort=${sort}&order=${order}&page=${
//       page + 1
//     }`;
//
//     return this._httpClient.get<AvailableTimes>(requestUrl);
//   }
// }
