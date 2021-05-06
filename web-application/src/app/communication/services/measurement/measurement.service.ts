import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Measurement } from '../../models/measurement';

@Injectable({
  providedIn: 'root'
})
export class MeasurementService {

  constructor(private http: HttpClient) { }

  getMeasurement(lat: number, lon: number): Observable<Measurement> {
    const url = environment.baseURL + 'actual-measurement?lat='+lat+"&lon="+lon;
    return this.http.get<Measurement>(url);
  }

}
