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
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {ContactDataDialog} from "./components/contact-data-dialog/contact-data-dialog.component";
import moment from "moment";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {DatePipe} from "@angular/common";
import {BookedTimeDialogComponent} from "./components/booked-time-dialog/booked-time-dialog.component";

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
    MatProgressSpinner,
    DatePipe,
  ],
  providers: [provideNativeDateAdapter()],
  styleUrls: ['./app.component.css']
})
export class AppComponent implements AfterViewInit {
  displayedColumns: string[] = ['time', 'workshopName', 'carType', 'address', 'book'];
  data: AvailableTime[] = [];

  isLoadingResults = true;

  constructor(public dialog: MatDialog, private availableTimesService: AvailableTimesService) { }

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
  }

  onSearch() {
    this.loadAvailableTimes();
  }

  onBook(id: string, workshopName: string) {
    const dialogRef = this.dialog.open(ContactDataDialog);

    dialogRef.afterClosed().subscribe(contactInformation => {
      this.availableTimesService.sendBookRequest(id, contactInformation, workshopName)
        .subscribe({
          // next: this.onBookingResponse,
          next: availableTime => this.onBookingResponse(availableTime),
          error: this.onBookingError,
          complete: this.onBookingComplete
        })
    });
  }

  private onBookingResponse(availableTime: AvailableTime) {
    const dialogRef = this.dialog.open(BookedTimeDialogComponent, {
      data: availableTime,
    });

    dialogRef.afterClosed().subscribe(() => this.loadAvailableTimes());
  }

  private onBookingError(error: string) {
    console.log(error);
  }

  private onBookingComplete() {
    console.log("Booking complete(hide progress circle)");
  }

  private loadAvailableTimes() {
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

          if (data === null) {
            return [];
          }

          return data;
        }),
      )
      .subscribe(data => (this.data = data));
  }
}
