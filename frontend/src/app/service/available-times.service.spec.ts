import { TestBed } from '@angular/core/testing';

import { AvailableTimesService } from './available-times.service';

describe('AvailableTimesService', () => {
  let service: AvailableTimesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AvailableTimesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
