import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MeasurementsDayComponent } from './measurements-day.component';

describe('MeasurementsDayComponent', () => {
  let component: MeasurementsDayComponent;
  let fixture: ComponentFixture<MeasurementsDayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MeasurementsDayComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MeasurementsDayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
