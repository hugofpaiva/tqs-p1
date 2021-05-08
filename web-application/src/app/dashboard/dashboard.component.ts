import { Component, OnInit } from '@angular/core';
import {MeasurementService} from '../communication/services/measurement/measurement.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  constructor(private measurementService: MeasurementService) { }

  ngOnInit() {
    this.getActualMeasurement(40.666666, 32.543546);
  }


  getActualMeasurement(lat: number, long: number ): void {
    this.measurementService.getMeasurement(lat, long).subscribe(measurement => {
      console.log(measurement);
    });
  }

}
