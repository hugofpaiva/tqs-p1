import { Component, OnInit } from '@angular/core';
import {MeasurementService} from '../communication/services/measurement/measurement.service';
import {Measurement} from '../communication/models/measurement';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  private measurement: Measurement;
  private requested = false;
  private valuesLine1: Map<String, String> = new Map([['co', 'g'], ['no', 'f'], ['no2', 'f'], ['o3', 'f']]);
  private valuesLine2: Map<String, String> = new Map([['so2', 'g'], ['pm25', 'f'], ['pm10', 'f'], ['nh3', 'f']]);
  private valuesLine3: Map<String, String> = new Map([['wind', 'g'], ['humidity', 'f'], ['pressure', 'f']]);





  constructor(private measurementService: MeasurementService) { }

  ngOnInit() {
    this.getActualMeasurement(40.666666, 32.543546);
  }

  getActualMeasurement(lat: number, long: number ): void {
    this.measurementService.getMeasurement(lat, long).subscribe(measurement => {
      this.measurement = measurement;
      console.log(this.measurement);
    });
  }

}
