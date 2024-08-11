import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookedTimeDialogComponent } from './booked-time-dialog.component';

describe('BookedTimeDialogComponent', () => {
  let component: BookedTimeDialogComponent;
  let fixture: ComponentFixture<BookedTimeDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookedTimeDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BookedTimeDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
