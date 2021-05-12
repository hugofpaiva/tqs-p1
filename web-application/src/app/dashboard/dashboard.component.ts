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
  notFound = false;
  error = false;
  private checkFormErrors = false;
  private valuesLine1: Map<String, String> = new Map([['co', 'μg/m3'], ['no', 'μg/m3'], ['no2', 'μg/m3'], ['o3', 'μg/m3']]);
  private valuesLine2: Map<String, String> = new Map([['so2', 'μg/m3'], ['pm25', 'μg/m3'], ['pm10', 'μg/m3'], ['nh3', 'μg/m3']]);
  private valuesLine3: Map<String, String[]> = new Map([['wind', ['km/h', 'fas fa-paper-plane']], ['humidity', ['%', 'fas fa-tint']],
    ['pressure', ['', 'fas fa-tachometer-alt']]]);

  selectedSearch: String = 'Coordinates';

  @Input() requestCoordinates: RequestCoordinates = new RequestCoordinates();
  @Input() requestLocation: RequestLocation = new RequestLocation();

  private coordinatesForm: FormGroup;
  private locationForm: FormGroup;



  constructor(private formBuilder: FormBuilder, private measurementService: MeasurementService, private spinner: NgxSpinnerService) {
    this.coordinatesForm = this.formBuilder.group({
      longitude: ['longitude', [Validators.required, Validators.min(-180), Validators.max(180)]],
      latitude: ['latitude', [Validators.required, Validators.min(-90), Validators.max(90)]]
    });

    this.locationForm = this.formBuilder.group({
      location: ['location', [Validators.required, Validators.maxLength(255)]],
    });
  }

  ngOnInit() {
  }

  getActualMeasurementCoordinates(lat: number, long: number ): void {
    this.spinner.show();
    this.requested = true;
    this.notFound = false;
    this.error = false;
    this.measurementService.getMeasurementCoordinates(lat, long).subscribe(measurement => {
      this.measurement = measurement;
      this.requested = false;
      this.spinner.hide();
    }, (err: HttpErrorResponse) => {
      this.error = true;
      this.requested = false;
      this.spinner.hide();
    });
  }

  getActualMeasurementLocation(location: String): void {
    this.spinner.show();
    this.notFound = false;
    this.requested = true;
    this.error = false;
    this.measurementService.getMeasurementLocation(location).subscribe(measurement => {
      this.measurement = measurement;
      this.requested = false;
      this.spinner.hide();
    }, (err: HttpErrorResponse) => {
      if (err.status === 404) {
        this.notFound = true;
      }
      this.error = true;
      this.requested = false;
      this.spinner.hide();
    });
  }

  selectSearchType(event) {
    this.selectedSearch = event;
    this.checkFormErrors = false;
    this.error = false;
    this.notFound = false;
    this.measurement = undefined;
  }

  errorsCheck() {
    this.checkFormErrors = true;
  }

  // convenience getter for easy access to form fields
  get f(): any { return this.coordinatesForm.controls; }

  // convenience getter for easy access to form fields
  get l(): any { return this.locationForm.controls; }

}
