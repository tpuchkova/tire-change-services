import {ChangeDetectionStrategy, Component, Input, Output} from '@angular/core';
import {provideNativeDateAdapter} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatFormFieldModule} from '@angular/material/form-field';

@Component({
  selector: 'date-range-picker',
  templateUrl: 'date-range-picker.component.html',
  standalone: true,
  imports: [MatFormFieldModule, MatDatepickerModule],
  providers: [provideNativeDateAdapter()],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DateRangePickerComponent {
  // @Output()
  // startDate: String = "";
  // @Output()
  // endDate: String = "";
  @Input()
  dateRangeChangeCallback = (startDate: string, endDate: string) => {}

  onDateRangeChange(dateRangeStart: HTMLInputElement, dateRangeEnd: HTMLInputElement) {
    this.dateRangeChangeCallback(dateRangeStart.value, dateRangeEnd.value);
  }
}
