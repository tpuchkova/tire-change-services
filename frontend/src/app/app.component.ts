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
import {AvailableTime, AvailableTimesService, NameValue} from "./service/available-times.service";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {ContactDataDialog} from "./components/contact-data-dialog/contact-data-dialog.component";
import {ErrorComponent} from "./components/error/error.component";
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
  readonly nameValueAll: NameValue = {name: 'All', value: 'all'};
  readonly displayedColumns: string[] = ['time', 'workshopName', 'carType', 'address', 'book'];
  data: AvailableTime[] = [];

  isLoadingResults = true;

  constructor(public dialog: MatDialog, private availableTimesService: AvailableTimesService) { }

  ngAfterViewInit() {
    this.loadAvailableTimes();
    this.availableTimesService.getWorkshopsAndCarTypes()
      .subscribe(workshopsAndCarTypes => {
        this.workshopNames = [this.nameValueAll, ...workshopsAndCarTypes.workshops];
        this.carTypes = [this.nameValueAll, ...workshopsAndCarTypes.carTypes]
      })
  }

  @Input()
  selectedWorkshop: string = "all";
  @Input()
  selectedCarType: string = "all";

  startDate: string = "";
  endDate: string = "";

  workshopNames: NameValue[] = [
    this.nameValueAll,
  ];
  carTypes: NameValue[] = [
    this.nameValueAll,
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
      if (contactInformation) {
        this.availableTimesService.sendBookRequest(id, contactInformation, workshopName)
          .subscribe({
            next: availableTime => this.onBookingResponse(availableTime),
            error: err => this.onBookingError(err),
          })
      }
    });
  }

  private onBookingResponse(availableTime: AvailableTime) {
    const dialogRef = this.dialog.open(BookedTimeDialogComponent, {
      data: availableTime,
    });

    dialogRef.afterClosed().subscribe(() => this.loadAvailableTimes());
  }

  private onBookingError(error: string) {
    const dialogRef = this.dialog.open(ErrorComponent);
    dialogRef.afterClosed().subscribe(() => this.loadAvailableTimes());
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
