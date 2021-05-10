import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Measurement } from '../../models/measurement';

@Injectable({
  providedIn: 'root'
})
export class MeasurementService {

  constructor(private http: HttpClient) { }

  getMeasurementCoordinates(lat: number, lon: number): Observable<Measurement> {
    const url = environment.baseURL + 'actual-measurement-coordinates?lat=' + lat + '&lon=' + lon;
    return this.http.get<Measurement>(url);
  }

  getMeasurementLocation(location: String): Observable<Measurement> {
    const url = environment.baseURL + 'actual-measurement-location?location=' + location;
    return this.http.get<Measurement>(url);
  }

}
