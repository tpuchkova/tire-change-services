import {AfterViewInit, Component, Input} from '@angular/core';
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
import {merge, of as observableOf} from 'rxjs';
import {catchError, map, startWith, switchMap} from 'rxjs/operators';
import {AvailableTime, AvailableTimesService} from "./service/available-times.service";

interface WorkshopName {
  value: string;
  viewValue: string;
}

interface CarType {
  value: string;
  viewValue: string;
}

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
export class AppComponent implements AfterViewInit {
  displayedColumns: string[] = ['time', 'workshopName', 'carType', 'address', 'book'];

  availableTimesService: AvailableTimesService | null | undefined
  data: AvailableTime[] = [];

  isLoadingResults = true;
  isRateLimitReached = false;

  constructor(private _httpClient: HttpClient) { }

  ngAfterViewInit() {
    this.loadAvailableTimes();
  }

  @Input()
  selectedWorkshop: string = "all";
  @Input()
  selectedCarType: string = "all";

  startDate: string = "";
  endDate: string = "";


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

  onDateRangeChange(startDate: string, endDate: string) {
    this.startDate = startDate;
    this.endDate = endDate;
    console.log("startDate " + this.startDate + "; endDate " + endDate);
  }

  onSearch() {
    this.loadAvailableTimes();
  }

  onBook(id: string) {
    //dialog.Open
    console.log('Current time:', new Date().toLocaleTimeString());
  }

  private loadAvailableTimes() {
    this.availableTimesService = new AvailableTimesService(this._httpClient);

    merge()
      .pipe(
        startWith({}),
        switchMap(() => {
          this.isLoadingResults = true;
          return this.availableTimesService!.getAvailableTimes(
            this.startDate,
            this.endDate,
            this.selectedWorkshop,
            this.selectedCarType
          ).pipe(catchError(() => observableOf(null)));
        }),
        map(data => {
          this.isLoadingResults = false;
          this.isRateLimitReached = data === null;

          if (data === null) {
            return [];
          }

          return data;
        }),
      )
      .subscribe(data => (this.data = data));
  }
}
