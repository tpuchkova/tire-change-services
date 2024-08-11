import {Component, Inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {ReactiveFormsModule} from "@angular/forms";
import {AvailableTime} from "../../service/available-times.service";
import {DatePipe} from "@angular/common";

@Component({
  selector: 'app-booked-time-dialog',
  standalone: true,
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogContent,
    MatDialogTitle,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    DatePipe
  ],
  templateUrl: './booked-time-dialog.component.html',
  styleUrl: './booked-time-dialog.component.css'
})
export class BookedTimeDialogComponent {
  constructor(public dialogRef: MatDialogRef<BookedTimeDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public availableTime: AvailableTime,
  ) {}

  onOkClick(): void {
    this.dialogRef.close();
  }
}
