import {Component, Input, OnInit} from '@angular/core';
import {MeasurementService} from '../communication/services/measurement/measurement.service';
import {Measurement} from '../communication/models/measurement';
import {HttpErrorResponse} from '@angular/common/http';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {RequestCoordinates} from '../communication/models/requestCoordinates';
import {RequestLocation} from '../communication/models/requestLocation';
import {NgxSpinnerService} from 'ngx-spinner';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  measurement: Measurement;
  private requested = false;
  error = false;
  private checkFormErrors = false;
  private valuesLine1: Map<String, String> = new Map([['co', 'g'], ['no', 'f'], ['no2', 'f'], ['o3', 'f']]);
  private valuesLine2: Map<String, String> = new Map([['so2', 'g'], ['pm25', 'f'], ['pm10', 'f'], ['nh3', 'f']]);
  private valuesLine3: Map<String, String[]> = new Map([['wind', ['g', 'fas fa-paper-plane']], ['humidity', ['g', 'fas fa-tint']],
    ['pressure', ['g', 'fas fa-tachometer-alt']]]);

  selectedSearch: String = 'Coordinates';

  @Input() requestCoordinates: RequestCoordinates = new RequestCoordinates();
  @Input() requestLocation: RequestLocation = new RequestLocation();

  private coordinatesForm: FormGroup;
  private locationForm: FormGroup;



  constructor(private formBuilder: FormBuilder, private measurementService: MeasurementService, private spinner: NgxSpinnerService) {
    this.coordinatesForm = this.formBuilder.group({
      longitude: ['', [Validators.required, Validators.min(-180.000000), Validators.max(180.000000),
        Validators.pattern('^-?\\d+\\.\\d{6,6}$')]],
      latitude: ['', [Validators.required, Validators.min(-90.000000), Validators.max(90.000000),
        Validators.pattern('^-?\\d+\\.\\d{6,6}$')]]
    });

    this.locationForm = this.formBuilder.group({
      location: ['', [Validators.required, Validators.maxLength(255)]],
    });
  }

  ngOnInit() {
  }

  getActualMeasurement(lat: number, long: number ): void {
    this.spinner.show();
    this.requested = true;
    this.error = false;
    this.measurementService.getMeasurement(lat, long).subscribe(measurement => {
      this.measurement = measurement;
      this.requested = false;
      this.spinner.hide();
    }, (err: HttpErrorResponse) => {
      this.error = true;
      this.requested = false;
      this.spinner.hide();
    });
  }

  selectSearchType(event) {
    this.selectedSearch = event;
    this.checkFormErrors = false;
    this.error = false;
  }

  errorsCheck() {
    this.checkFormErrors = true;
  }

  // convenience getter for easy access to form fields
  get f(): any { return this.coordinatesForm.controls; }

  // convenience getter for easy access to form fields
  get l(): any { return this.locationForm.controls; }

}
