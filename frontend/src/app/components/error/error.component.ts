import { Component } from '@angular/core';
import { MatDialogRef, MatDialogActions, MatDialogContent } from "@angular/material/dialog";

@Component({
  selector: 'app-error',
  standalone: true,
  imports: [
      MatDialogContent,
      MatDialogActions,
  ],
  templateUrl: './error.component.html',
  styleUrl: './error.component.css'
})
export class ErrorComponent {
    constructor(
      public dialogRef: MatDialogRef<ErrorComponent>
    ) {}

    onOkClick(): void {
      this.dialogRef.close();
    }
}
